package com.adviser.informer.model.traffic;

import java.io.Serializable;

import lombok.Data;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.deser.StdDeserializer.IntegerDeserializer;

@Data
public class IpTuple implements Serializable {
  private static final long serialVersionUID = -7389405833651561486L;

  String srcIP;
  String dstIP;
  String prot;
  @JsonDeserialize(using = IntegerDeserializer.class)
  Integer srcPort;
  @JsonDeserialize(using = IntegerDeserializer.class)
  Integer dstPort;
  @JsonDeserialize(using = IntegerDeserializer.class)
  Integer octets;
  @JsonDeserialize(using = IntegerDeserializer.class)
  Integer packets;

}
