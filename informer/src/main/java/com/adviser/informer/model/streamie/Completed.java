package com.adviser.informer.model.streamie;

import java.sql.Date;

import lombok.Data;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

import com.adviser.informer.model.DateDeserializer;

@Data
public class Completed {

  @JsonDeserialize(using = DateDeserializer.class)
  private Date date;
  private int pid;
  private String rev;
  
}
