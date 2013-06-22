(ns zguide.taskvent
  (:refer-clojure :exclude [send])
  (:require [zguide.zhelpers :as mq])
  (:import [java.util Random]))

;;
;; Task ventilator
;; Binds PUSH socket to tcp://localhost:5557
;; Sends batch of tasks to workers via that socket
;;
;;
;; Isaiah Peng <issaria@gmail.com>
;;

(defn- main []
  (mq/with-context [ctx 1]
    (let
        ;; Socket to send messages on
        [sender (mq/socket ctx mq/push)
         ;; Socket to send messages on
         sink (mq/socket ctx mq/push)
         srandom (Random. (System/currentTimeMillis))
         ;; Total expected cost in msecs
         total-msec (atom 0)]
      (try
        (mq/bind sender "tcp://*:5557")
        (mq/connect sink "tcp://localhost:5558")
        (println "Press Enter when the workers are ready: ")
        (read-line)
        (println "Sending tasks to workers...\n")
        ;; The first message is "0" and signals start of batch
        (mq/send sink "0\u0000")
        (dotimes [i 100]
          (let [workload (-> srandom (.nextInt 100) (+ 1))
                string (format "%d\u0000" workload)]
            (swap! total-msec #(+ % workload))
            (println (str workload "."))
            (mq/send sender string)))
        (println (str "Total expected cost: " @total-msec " msec"))
        (finally 
          (.close sink)
          (.close sender)))))
  ;; Give 0MQ time to deliver
  (Thread/sleep 1000))
