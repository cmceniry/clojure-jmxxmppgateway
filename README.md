JMX/XMPP Gateway in Clojure
===========================

This is just a simple standalone JMX->XMPP gateway, and some misc tools. Based on the clojure-jmxhttpgateway project.

Example
-------

Start up a java process with JMX enabled without any security:

   -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=4270 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false

Update the code (yeah, to be fixed) with the connect info:

   (def connect-info {:username "clj@chriss-macbook-air.local"
                      :password "clj"
                      :host "localhost"
                      :domain "chriss-macbook-air.local"})

Then start up the jmxxmppgateway.server:

   lein run -m jmxxmppgateway.server

Connect to the agent via xmpp and pass requests through it in an ugly request:

   me@myxmppdomain: localhost:4270|java.lang:type=Memory|HeapMemoryUsage
   bot@myxmppdomain: HeapMemoryUsage : {:committed 85000192, :init 0, :max 129957888, :used 8645872}

