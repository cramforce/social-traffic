package com.adviser.informer.model.streamie;

import java.io.Serializable;

import lombok.Data;

@Data
public class Tuple implements Serializable {
  private static final long serialVersionUID = 2427119683268512282L;
  public Tuple(long _timestamp) {
    timestamp = _timestamp;
  }

  public long timestamp;
  public long inAmount = 0;
  public long outAmount = 0;
}
