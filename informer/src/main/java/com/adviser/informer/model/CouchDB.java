package com.adviser.informer.model;

import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;

public abstract class CouchDB {
  public static StdCouchDbInstance connection() {
    HttpClient httpClient = new StdHttpClient.Builder()
       .host(Configuration.get("couchdb.server", "127.0.0.1"))
       .port(Integer.parseInt(Configuration.get("couchdb.port", "5984"))).build();

       return new StdCouchDbInstance(httpClient);
     }
  }

	
