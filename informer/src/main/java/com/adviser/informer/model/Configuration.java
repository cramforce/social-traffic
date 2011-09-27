package com.adviser.informer.model;

import java.io.FileInputStream;
import java.util.Properties;

public abstract class Configuration {

  private static Properties _properties = null;

  public static String get(String p) {
    return get(p, "");
  }

  public static String get(String p, String def) {
    return load().getProperty(p, def);
  }

  public static Properties load() {
    return load(null);
  }

  public static Properties load(String name) {
    if (_properties != null)
      return _properties;
    _properties = new Properties();
    final String fname = name + ".properties";
    try {
      _properties.load(new FileInputStream(fname));
      System.out.println("loaded " + fname);
    } catch (Exception e) {
      System.err.println("load of " + fname + " failed:"+e.getMessage());
    }
    return _properties;
  }

}
