package com.adviser.informer.model.streamie;

import java.io.Serializable;
import java.sql.Date;

import lombok.Data;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

import com.adviser.informer.model.DateDeserializer;

@Data
public class Client implements Serializable {

  private static final long serialVersionUID = 743055082362827935L;


  transient private UpdateTraffic updateTraffic;
  public void setUpdateTraffic(UpdateTraffic _updateTraffic) {
    updateTraffic = _updateTraffic;
  }
  private String ipv4;
  private String hwaddr;
  private String useragent;
  @JsonDeserialize(using = DateDeserializer.class)
  private Date created_at;
  private Traffic traffic;
  public Traffic getTraffic() {
    if (traffic == null) {
      traffic = new Traffic(updateTraffic);
    }
    return traffic;
  }
}
