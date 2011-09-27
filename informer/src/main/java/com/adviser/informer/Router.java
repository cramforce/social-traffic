package com.adviser.informer;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.w3c.dom.Document;

import com.adviser.informer.model.Streamies;
import com.adviser.informer.model.streamie.Streamie;

public class Router extends RouteBuilder {
  public class ListenAddress {
    private String addr = "127.0.0.1";
    private String port = "2911";

    public ListenAddress() {
    }

    public ListenAddress(String port, String addr) {
      this.port = port;
      if (addr != null)
        this.addr = addr;
    }

    public String toString() {
      return addr + ":" + port;
    }
  }

  private ListenAddress listenaddress = null;

  private Streamies streamies = null;

  public Router(String[] args, Streamies _streamies) {
    streamies = _streamies;
    if (args.length == 1) {
      listenaddress = new ListenAddress(args[0], null);
    } else if (args.length >= 2) {
      listenaddress = new ListenAddress(args[0], args[1]);
    } else {
      listenaddress = new ListenAddress();
    }
    init();
  }

  private void init() {
  }

  public void configure() {
    System.out.println("Version:" + getServer());
    System.out.println("Listen On:" + listenaddress.toString());
    from(
        "restlet:http://" + listenaddress.toString()
            + "/traffic/byTwitter/{screenName}?restletMethods=get").bean(this,
        "byTwitter");
    from(
        "restlet:http://" + listenaddress.toString()
            + "/traffic/top10?restletMethods=get").bean(this,
        "top10");
  }

  private String _version = null;

  private String getServer() {
    if (_version != null) {
      return _version;
    }
    synchronized (this) {
      if (_version != null) {
        return _version;
      }
      String version = "Informer(development)";
      final InputStream is = Router.class.getClassLoader().getResourceAsStream(
          "META-INF/maven/com.adviser.informer/informer/pom.xml");
      if (is != null) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document doc;
        try {
          DocumentBuilder db = dbf.newDocumentBuilder();
          doc = db.parse(is);
          version = "Informer("
              + doc.getElementsByTagName("version").item(0).getTextContent()
              + ")";
        } catch (Exception e) {
          // System.out.println("IS:"+e.getMessage());
        }
      }
      _version = version;
    }
    return _version;
  }

  public void byTwitter(Exchange exchange) {
    final Message _in = exchange.getIn();
    final String screenName = _in.getHeader("screenName", String.class);
    final Message _out = exchange.getOut();
    Streamie streamie = streamies.findByTwitter(screenName);

    StringWriter str = new StringWriter();
    try {
      (new ObjectMapper()).writeValue(str, streamie);
    } catch (JsonGenerationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (JsonMappingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    _out.setBody(str.toString());
  }
  
  public void top10(Exchange exchange) {
    final Iterator<Streamie> top10 = streamies.top(10).iterator();
    final List<String> screenNames = new LinkedList<String>();
    while (top10.hasNext()) {
      final Streamie s = top10.next();
      screenNames.add(s.getTwitter().getScreen_name());
    }  
    final Message _out = exchange.getOut();
    _out.setBody(screenNames.toString());
  }
}
