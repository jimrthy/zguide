(ns zguide.msreader
  (:require [zguide.zhelpers :as mq]))

(defn -main [args]
  (mq/with-context [ctx 1]
    ;; What about the PULL socket on 5557?
    (error "Start Here")
    (mq/with-socket [receiver ctx mq/pull]
      (mq/connect "tcp://localhost:5556" ;; What does this actually do?
                  ))))
