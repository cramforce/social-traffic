package com.adviser.informer.model;

import java.util.Observable;
import java.util.Observer;

public abstract class Done implements Observer {

  public abstract void done();
  
  public void update(Observable o, Object arg) {
    if (arg.equals(Streamies.INITIALIZE)) {
      done();
    }
  }
  
}
