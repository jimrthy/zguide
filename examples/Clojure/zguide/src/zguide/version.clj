(ns zguide.version
  (:gen-class)
  (:import [org.zeromq ZMQ]))

(defn main []
  (let [s (ZMQ/getVersionString)
        i (ZMQ/getFullVersion)]
    (println "Version String: " s " Version int: " i)))
