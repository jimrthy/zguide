(ns wuserver
  (:refer-clojure :exclude [send])
  (:require [zhelpers :as mq])
  (:import [java.util Random]))

;;
;; Weather update server
;; Binds PUB socket to tcp://*:5556
;; Publishes random weather updates
;;
;; Isaiah Peng <issaria@gmail.com>
;;

(defn -main []
  ;; This sort of approach seems at least a little worrisome.
  ;; Since it's relying on garbage collection to actually free
  ;; the socket and context.
  ;; Could be totally valid, but it doesn't seem right.
  (let [publisher (-> 1 mq/context (mq/socket mq/pub))
        srandom (Random. (System/currentTimeMillis))]
    (mq/bind publisher "tcp://*:5556")
    (mq/bind publisher "ipc://weather.ipc")
    (while true
      (let [zipcode (-> srandom (.nextInt 100000) (+ 1))
              temperature (-> srandom (.nextInt 215) (- 79))
              relhumidity (-> srandom (.nextInt 50) (+ 11))
              update (format "%05d %d %d\u0000" zipcode temperature relhumidity)]
        ;; Send message to all subscribers
        (mq/send publisher update))
      )))
