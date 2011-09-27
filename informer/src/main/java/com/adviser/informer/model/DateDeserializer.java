package com.adviser.informer.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

public class DateDeserializer extends JsonDeserializer<Object> {
  private static final DateFormat formatter = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);
  public static Date str2Date(String str) {
    try {
      return formatter.parse(str);
    } catch (Exception e) {
      throw new IllegalArgumentException("Cannot parse date:"+str, e);
    }    
  }
  public Object deserialize(JsonParser jp, DeserializationContext ctxt) {
    try {
      return str2Date(jp.getText());
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    } 
  }
}