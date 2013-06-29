(ns zguide.msreader
  (:require [zguide.zhelpers :as mq]))

(defn -main [args]
  (mq/with-context [ctx 1]
    ;; Connect to task ventilator
    (mq/with-socket [receiver ctx mq/pull]
      (mq/connect "tcp://localhost:5557")
      
      ;; Connect to weather server
      (mq/with-socket [subscriber ctx mq/sub]
        (mq/connect "tcp://localhost:5556")
        (mq/subscribe subscriber "10001 ")
        
        ;; Process messages from both sockets
        (loop [frame-count 0]
          ;; Prioritize traffic from task ventilator
          (loop [task-attempts 0]
            (let [message (mq/recv receiver zmq/dont-wait)]
              (when message
                ;; Perform work
                (recur (inc task-attempts)))))
          
          ;; Check for weather updates
          (loop [weather-attempts 0]
            (let [message (mq/recv subscriber)]
              (when message
                ;; Deal with weather
                (recur (inc weather-attempts)))))
          ;; No activity. Wait a bit.
          (Thread/sleep 1)
          (recur (inc frame-count)))
        ;; Note that those loops never exit.
        ))))
