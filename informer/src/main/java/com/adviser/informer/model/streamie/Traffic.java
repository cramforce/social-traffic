package com.adviser.informer.model.streamie;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

import lombok.Getter;

public class Traffic implements Serializable {

  transient
  private final UpdateTraffic updateTraffic;
  
  public Traffic(UpdateTraffic _updateTraffic) {
    updateTraffic = _updateTraffic;
  }
  
  private static final long serialVersionUID = 5218832076545516584L;

  @Getter
  private LinkedList<Tuple> traffic = new LinkedList<Tuple>();

  
  private Tuple addTraffic(Tuple tuple, long inAmount, long outAmount) {
    tuple.inAmount += inAmount;
    tuple.outAmount += outAmount;
    updateTraffic.updateTraffic(inAmount, outAmount);
    return tuple;
  }

  public Tuple add(long timestamp, long inAmount, long outAmount) {
    synchronized (this) {
      if (traffic.size() == 0) {
        final Tuple tuple = new Tuple(timestamp);
        traffic.add(tuple);
        return addTraffic(tuple, inAmount, outAmount);
      }
      Iterator<Tuple> i = traffic.descendingIterator();
      int pos = traffic.size();
      while (i.hasNext()) {
        Tuple tuple = i.next();
        --pos;
        if (tuple.timestamp < timestamp) {
          // add
          tuple = new Tuple(timestamp);
          traffic.add(pos + 1, tuple);
          return addTraffic(tuple, inAmount, outAmount);
        } else if (tuple.timestamp == timestamp) {
          // found
          return addTraffic(tuple, inAmount, outAmount);
        } else if (tuple.timestamp > timestamp) {
          tuple = new Tuple(timestamp);
          traffic.add(pos, tuple);
          return addTraffic(tuple, inAmount, outAmount);
        }
      }
      return null;
    }
  }
}
