package com.adviser.informer.model;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.ektorp.changes.ChangesCommand;
import org.ektorp.changes.ChangesFeed;
import org.ektorp.changes.DocumentChange;
import org.ektorp.impl.StdCouchDbConnector;

import com.adviser.informer.model.streamie.Client;
import com.adviser.informer.model.streamie.Streamie;
import com.adviser.informer.model.traffic.Traffic;
import com.adviser.informer.model.traffic.Tuple;

public class Streamies extends Observable implements Runnable {

  public static final int INITIALIZE = 1;
  
  private Map<String, Streamie> byId = null;
  private Map<String, Streamie> byTwitter = null;
  private Map<String, Streamie> byIp = null;
  private Map<String, Streamie> byMac = null;
  private Map<Integer, Streamie> byTraffic = null;
  private Streamies() {
    byId = new ConcurrentHashMap<String, Streamie>();
    byTwitter = new ConcurrentHashMap<String, Streamie>();
    byIp = new ConcurrentHashMap<String, Streamie>();
    byMac = new ConcurrentHashMap<String, Streamie>();
    byTraffic = new ConcurrentHashMap<Integer, Streamie>();
  }

  public Streamie findById(String id) {
    return byId.get(id);
  }

  private void remove(String id) {
    if (id == null) {
      return;
    }
    final Streamie str = byId.get(id);
    if (str == null) {
      return;
    }
    byId.remove(str.getId());
    byTwitter.remove(str.getTwitter().getScreen_name());
    final Iterator<Client> clients = str.getClients().iterator();
    while (clients.hasNext()) {
      final Client client = clients.next();
      byIp.remove(client.getIpv4());
      byMac.remove(client.getHwaddr());
    }
  }

  private void add(Streamie str) {
    remove(str.getId());
    byId.put(str.getId(), str);
    byTwitter.put(str.getTwitter().getScreen_name(), str);
    final Iterator<Client> clients = str.getClients().iterator();
    while (clients.hasNext()) {
      final Client client = clients.next();
      byIp.put(client.getIpv4(), str);
      byMac.put(client.getHwaddr(), str);
    }
  }

  private static Streamies my = null;

  public static Streamies init() {
    if (my == null) {
      my = new Streamies();
      new Thread(my).start();
    }
    return my;
  }

  private class Fetchers implements Runnable {

    private BlockingQueue<DocumentChange> q;
    private Completed c;

    public Fetchers(BlockingQueue<DocumentChange> _q, Completed _c) {
      q = _q;
      c = _c;
    }

    public void run() {
      final StdCouchDbConnector db = new StdCouchDbConnector("streamie",
          CouchDB.connection());
      try {
        while (true) {
          final DocumentChange dc = q.take();
          if (dc.isDeleted()) {
            remove(dc.getId());
          } else {
            final Streamie streamie = db.get(Streamie.class, dc.getId());
            add(streamie);
          }
          System.out.println("Streamie:" + dc.getId());
          c.done(dc.getSequence());
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

    }
  }

  public abstract class Completed {

    private long last = 0;

    public Completed(long _last) {
      last = _last;
    }

    public void done(long seq) {
      if (seq == last) {
        completed(seq);
      }
    }

    public abstract void completed(long last);

  }
  
  public void add(Traffic traffic) {
    final Iterator<Tuple> tuples = traffic.getTuples().iterator();
    while (tuples.hasNext()) {
      final Tuple tuple = tuples.next();
      long inAmount = 0;
      long outAmount = 0;
      String matchIp = tuple.getDstIP();
      Streamie streamie = byIp.get(matchIp);
      if (streamie == null) {
        matchIp = tuple.getSrcIP();
        streamie = byIp.get(matchIp);
        if (streamie == null) {
          continue;
        } else {
          outAmount = tuple.getOctets();
        }
      } else {
        inAmount = tuple.getOctets();
      }
      streamie.add(matchIp, traffic, inAmount, outAmount);
    }
  }
  
  public void run() {

    final BlockingQueue<DocumentChange> q = new LinkedBlockingQueue<DocumentChange>();
    final ChangesCommand cmd = new ChangesCommand.Builder().since(0).build();
    final StdCouchDbConnector db = new StdCouchDbConnector("streamie",
        CouchDB.connection());
    final List<DocumentChange> feed = db.changes(cmd);

    q.addAll(feed);
    System.out.println("Initial Read until len:" + feed.size() + ":"
        + feed.get(feed.size() - 1).getSequence());
    
    final Streamies self = this;
    final Completed c = new Completed(feed.get(feed.size() - 1).getSequence()) {

      @Override
      public void completed(long last) {
        System.out.println("Initial Read completed until:" + last);
        final ChangesCommand cmd = new ChangesCommand.Builder().since(last)
            .build();
        final ChangesFeed feed = db.changesFeed(cmd);
        self.setChanged();
        self.notifyObservers(Streamies.INITIALIZE);
        while (feed.isAlive()) {
          try {
            final DocumentChange change = feed.next();
            q.add(change);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }

      }
    };
    new Thread(new Fetchers(q, c)).start();
    new Thread(new Fetchers(q, c)).start();
  }
}