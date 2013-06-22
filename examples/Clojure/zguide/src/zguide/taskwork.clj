(ns zguide.taskwork
  (:refer-clojure :exclude [send])
  (:require [zguide.zhelpers :as mq]))

;;
;; Task worker
;; Connects PULL socket to tcp://localhost:5557
;; Collects workloads from ventilator via that socket
;; Connects PUSH socket to tcp://localhost:5558
;; Sends results to sink via that socket
;;
;; Isaiah Peng <issaria@gmail.com>
;;

(defn- main []
  (mq/with-context [ctx 1]
    (let [receiver (mq/socket ctx mq/pull)
          sender (mq/socket ctx mq/push)]
      (try
        (mq/connect receiver "tcp://localhost:5557")
        (mq/connect sender "tcp://localhost:5558")
        ;; Process tasks forever
        (while true
          (let [string (mq/recv-str receiver)
                msec (Long/parseLong string)]
            (print (str string "."))
            ;; Do the work
            (Thread/sleep msec)
            (mq/send sender "\u0000")))
        (finally
          (.close receiver)
          (.close sender))))))
