package com.adviser.informer.model.traffic;

import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.ektorp.support.CouchDbDocument;

import com.adviser.informer.model.DateDeserializer;


@Data
@EqualsAndHashCode(callSuper=true)
public class Traffic extends CouchDbDocument {

  private static final long serialVersionUID = 978550300085359507L;
  
  private List<IpTuple> tuples;
  
  @JsonDeserialize(using = DateDeserializer.class)
  @JsonProperty("created_at")
  private Date createdAt;
  
}
