package com.adviser.informer;

import org.apache.camel.CamelContext;
import org.apache.camel.component.jetty.JettyHttpComponent;
import org.apache.camel.impl.DefaultCamelContext;

import com.adviser.informer.model.Done;
import com.adviser.informer.model.Streamies;
import com.adviser.informer.model.Traffics;

/**
 * Hello world!
 *
 */
public class Server 
{
    public static void main(final String[] args )
    {
      final Streamies streamies = Streamies.init();
      streamies.addObserver(new Done() {

        public void done() {
          System.out.println("Initial Read Done");
          Traffics.init(streamies);
          final CamelContext camelContext = new DefaultCamelContext();
          camelContext.disableJMX();
          final JettyHttpComponent jhc = new JettyHttpComponent();
          //final HeaderFilterStrategy hfs = new DefaultHttpFilterStrategy();
          //jhc.setHeaderFilterStrategy(hfs);
          camelContext.addComponent("jetty", jhc);
          try {
            camelContext.addRoutes(new Router(args, streamies));
            camelContext.start();
          } catch (Exception e) {
            e.printStackTrace();
          }
          
        }
        
      });

    }
}
