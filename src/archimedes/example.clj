(ns hermes.example
  (:require [hermes.core   :as g]
            [hermes.gremlin :as q]
            [hermes.vertex :as v]
            [hermes.edge   :as e]
            [hermes.type   :as t]))

(def conf {:storage {:backend "embeddedcassandra"
                     :hostname "127.0.0.1"
                     :keyspace "hermestest"
                     :cassandra-config-dir
                     (str "file://"
                          (System/getProperty "user.dir")
                          "/resources/test-cassandra.yaml")}})

(defonce graph (g/open conf))

(defonce a (g/transact! (v/create! {:name "a"})))
(defonce c (g/transact! (v/create! {:name "b"})))

(def connected (g/transact!
                (e/upconnect! (v/refresh a)
                              (v/refresh c)
                              "test")))

(def blah (g/transact! (e/edges-between (v/refresh a)
                                        (v/refresh c))))