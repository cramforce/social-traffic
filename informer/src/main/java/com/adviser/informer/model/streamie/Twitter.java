package com.adviser.informer.model.streamie;

import lombok.Data;
import java.io.Serializable;

@Data
public class Twitter implements Serializable {
  private static final long serialVersionUID = -6160452491334563871L;

  private String user_id;
  private String screen_name;
  private int statusCode;
  private String oauth;

}
