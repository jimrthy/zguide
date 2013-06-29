(ns zguide.mspoller
  (:require [zguide.zhelpers :as mq])
  (:gen-class))

(defn main []
  (mq/with-context [ctx 1]
    ;; Connect to task ventilator
    (mq/with-socket [receiver ctx mq/pull]
      (mq/connect receiver "tcp://localhost:5557")
      ;; Connect to weather server
      (mq/with-socket [subscriber ctx mq/sub]
        (mq/connect subscriber "tcp://localhost:5556")
        (mq/subscribe subscriber "10001 ")

        ;; Initialize poll set
        (let [poller (mq/socket-poller-in [receiver subscriber])]
          (println "Entering event loop")
          (while (not (.isInterrupted (Thread/currentThread)))
            (.poll poller)
            (when (.pollin poller 0)
              (let [msg (mq/recv-str receiver)]
                (println "Work: " msg)))
            (when (.pollin poller 1)
              (let [msg (mq/recv-str subscriber)]
                (println "Weather: " msg)))))))))
