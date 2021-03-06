(ns zguide.msreader
  (:require [zguide.zhelpers :as mq]))

(defn -main [args]
  (mq/with-context [ctx 1]
    ;; Connect to task ventilator
    (mq/with-socket [receiver ctx mq/pull]
      (mq/connect receiver "tcp://localhost:5557")
      
      ;; Connect to weather server
      (mq/with-socket [subscriber ctx mq/sub]
        (mq/connect subscriber "tcp://localhost:5556")
        (mq/subscribe subscriber "10001 ")
        
        ;; Process messages from both sockets
        (while true
          ;; Prioritize traffic from task ventilator
          (loop [task-attempts 0]
            (let [message (mq/recv-str receiver mq/dont-wait)]
              (when message
                (println "Working")
                (recur (inc task-attempts)))))
          
          ;; Check for weather updates
          (loop [weather-attempts 0]
            (let [message (mq/recv subscriber mq/dont-wait)]
              (when message
                (println "Weather update")
                (recur (inc weather-attempts)))))
          ;; No activity. Wait a bit.
          (Thread/sleep 1))))))
