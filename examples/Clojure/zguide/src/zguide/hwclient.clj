(ns zguide.hwclient
  (:gen-class)
  (:import [org.zeromq ZMQ]))

;; DEBUG ONLY
;;(println "Request: " ZMQ/REQ)

(defn main []
  (let [context (ZMQ/context 1)]
    (try
      (let [sock (.socket context ZMQ/REQ)]
        (try
          (.connect sock "tcp://localhost:5555")
          (dotimes [n 10]
            (let [request "Hello"]
              (println "Sending Hello # " n)
              (.send sock (.getBytes request) 0)

              (let [reply (.recv sock 0)]
                (println "Received: " (String. reply) " " n))))
          (finally
            (.close sock))))
      (finally (.term context)))))
