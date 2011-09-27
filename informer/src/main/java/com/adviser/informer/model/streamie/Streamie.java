package com.adviser.informer.model.streamie;

import java.util.Iterator;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import org.ektorp.support.CouchDbDocument;

@Data
public class Streamie extends CouchDbDocument implements Total {

  
  private static final long serialVersionUID = 978550300085359507L;

  private Twitter twitter;

  private List<Client> clients;

  private Completed completed;

  @Getter
  transient private long inTotal = 0;
  @Getter
  transient private long outTotal = 0;
  
  public void add(String ip, long timeStamp, long inAmount, long outAmount) {
    final Iterator<Client> clients = getClients().iterator();
    while (clients.hasNext()) {
      final Client client = clients.next();
      if (client.getIpv4().equals(ip)) {
        client.getHistory().add(timeStamp, inAmount, outAmount);
        break;
      }
    }
    inTotal += inAmount;
    outTotal += outAmount;
  }
}
