(ns zguide.wuclient
  (:refer-clojure :exclude [send])
  (:use [zguide.zhelpers :as mq])
  (:import [java.util StringTokenizer]))

;;
;; Weather update client
;; Connects SUB socket to tcp://localhost:5556
;; Collects weather updates and finds avg temp in zipcode
;;
;; Isaiah Peng <issaria@gmail.com>
;; Updated by: James Gatannah <james@gatannah.com>
;;

(def total-temp (atom 0))

(defn- main [& args]
  (mq/with-context [ctx 1]
    (mq/with-socket [subscriber ctx mq/sub]
      ;; filter is a bad variable name
      (let [filter (or (first args) "10001 ")
            args-temp (atom 0)
            nbr 100]
        (println "Collecting updates from weather server for " filter)
        (mq/connect subscriber "tcp://localhost:5556")
        (mq/subscribe subscriber filter)
        (dotimes [i nbr]
          (let [string (mq/recv-str subscriber)
                sscanf (StringTokenizer. string " ")
                zipcode (Integer/parseInt (.nextToken sscanf))
                temperature (Integer/parseInt (.nextToken sscanf))
                relhumidity (Integer/parseInt (.nextToken sscanf))]
            (swap! total-temp #(+ % temperature))
            (println (str "received: " string))))
        (println (str "Average temperature for zipcode '" filter "' was " 
                      (int (/ @total-temp nbr))))))))
