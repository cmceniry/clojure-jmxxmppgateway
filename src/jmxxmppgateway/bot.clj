(ns jmxxmppgateway.bot
  (:require
    [jmxhttpgateway.utils :as utils]
    [xmpp-clj :as xmpp]
    [clojure.string :as str]
  )
)

(def pool (ref {}))

(defn get-connection
  "Gets a jmx connection - opening it if necessary"
  [target]
  (if (nil? (@pool target))
      (dosync
        (alter pool assoc target (utils/connect-with-catch target))
        (@pool target))
      (@pool target)))

(defn remove-connection
  "Removes a connection from the pool"
  [target]
  (dosync
   (alter pool dissoc target)))

; This seems like a great place for lazy evaluation
; but not sure on how to approach that
; so using the boring old recur
(defn get-bean-attribute-with-retry
  "Gets an attribute value - reconnecting if necessary"
  [target bean-name attribute attempts]
  (if (= attempts 0)
      nil
      (let [conn (get-connection target)
            val (utils/get-bean-attribute-with-catch conn
                                                                    bean-name
                                                                    attribute)]
           (if (nil? val)
               (do
                (remove-connection target)
                (recur target bean-name attribute (- attempts 1)))
               val))))

(defn pp-bean-attribute ""
  [target bean-name attribute-name]
  (let [val (get-bean-attribute-with-retry target bean-name attribute-name 5)]
       (if (nil? val)
           nil
           (str attribute-name " : " val))))

(def connect-info {:username "clj@chriss-macbook-air.local"
                   :password "clj"
                   :host "localhost"
                   :domain "chriss-macbook-air.local"})

(defn handle-message [message]
  (let [form-user (:from-name message)
        body (str/trim (:body message))
        rsplit (str/split body #"\|")
       ]
    (println rsplit)
    (if (= (count rsplit) 3)
      (let [res (pp-bean-attribute (rsplit 0) (rsplit 1) (rsplit 2))]
        (if (nil? res) "Unable to get" res)
      )
      "Invalid arguments"
    )
  )
)

(defonce my-bot (xmpp/start-bot connect-info (var handle-message)))

(defn -main [& args]
  (let [my-bot (xmpp/start-bot connect-info (var handle-message))]
    (while true nil)
  )
)

