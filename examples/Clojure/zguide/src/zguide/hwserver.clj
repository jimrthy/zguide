(ns zguide.hwserver
  (:gen-class)
  (:import [org.zeromq.ZMQ]))

(defn main []
  (let [context (ZMQ/context 1)]
    (try
      (let [responder (.socket context ZMQ/REP)]
        (try
          (.bind responder "tcp://*:5555")
          (while (not (.isInterrupted (.currentThread Thread)))
            (let [reply (.recv responder 0)]
              (println "Received " (String. reply))
              (.send responder (.getBytes "World") 0))
            (.sleep Thread 1000))
          (finally (.close responder))))
      (finally (.term context)))))
