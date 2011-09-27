package com.adviser.informer.model.streamie;

import java.util.Iterator;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.ektorp.support.CouchDbDocument;

@Data
@EqualsAndHashCode(callSuper = true)
public class Streamie extends CouchDbDocument {

  private static final long serialVersionUID = 978550300085359507L;

  private Twitter twitter;

  private List<Client> clients;

  private Completed completed;

  public void add(String ip, Traffic traffic, long inAmount, long outAmount) {
    final Iterator<Client> clients = getClients().iterator();
    while (clients.hasNext()) {
      final Client client = clients.next();
      if (client.getIpv4().equals(ip)) {
        final long ticks = traffic.getCreatedAt().getTime();
        client.getTraffic().add(ticks / 1000, inAmount, outAmount);
        break;
      }
    }
  }
}
