(ns zguide.tasksink
  (:refer-clojure :exclude [send])
  (:require [zguide.zhelpers :as mq]))
 
;;
;; Task sink
;; Binds PULL socket to tcp://localhost:5558
;; Collects results from workers via that socket
;;
;; Isaiah Peng <issaria@gmail.com>
;;

(defn- main []
  (mq/with-context [ctx 1]
    (let [receiver (mq/socket ctx mq/pull)]
      (try
        (mq/bind receiver "tcp://*:5558")
        ;; Wait for start of batch
        (mq/recv receiver 0)

        (let [tstart (System/currentTimeMillis)]
          (dotimes [i 100]
            (mq/recv receiver)
            (if (= (mod 1 10 0))
              (print ":")
              (print ".")))
          ;; Calculate and report duration of batch
          (println (str "Total elapsed time:" (- (System/currentTimeMillis) tstart) " msec")))
        (finally (.close receiver))))))
