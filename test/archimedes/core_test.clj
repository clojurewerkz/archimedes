(ns hermes.core-test  
  (:use clojure.test
        [hermes.conf :only (conf clear-db)])
  (:require [hermes.core :as g]
            [hermes.type :as t]
            [hermes.vertex :as v])
  (:import  (com.thinkaurelius.titan.graphdb.database   StandardTitanGraph)
            (com.thinkaurelius.titan.graphdb.vertices   PersistStandardTitanVertex)))

(clear-db)
(g/open conf)

(deftest test-vertices
  (testing "Stored graph"
    (is (= (type g/*graph*)
           StandardTitanGraph)))

  (testing "Stored graph"
    (let [vertex (g/transact! (.addVertex g/*graph*))]      
      (is (= PersistStandardTitanVertex (type vertex)))))

  (testing "Stored graph"
    (is (thrown? Throwable #"transact!" (v/create!))))

  (testing "Dueling transactions"
    (testing "Without retries"
      (g/transact!
       (t/create-vertex-key-once :vertex-id Long {:indexed true
                                                  :unique true}))
      (let [random-long (long (rand-int 100000))
            f1 (future (g/transact! (v/upsert! :vertex-id {:vertex-id random-long})))
            f2 (future (g/transact! (v/upsert! :vertex-id {:vertex-id random-long})))]

        (is (thrown? java.util.concurrent.ExecutionException
                     (do @f1 @f2)) "The futures throw errors.")))
    (testing "With retries"
      (g/open conf)
      (g/transact!
       (t/create-vertex-key-once :vertex-id Long {:indexed true
                                                  :unique true}))
      (let [random-long (long (rand-int 100000))
            f1 (future (g/retry-transact! 3 100 (v/upsert! :vertex-id {:vertex-id random-long})))
            f2 (future (g/retry-transact! 3 100 (v/upsert! :vertex-id {:vertex-id random-long})))]

        (is (= random-long
               (g/transact!
                (v/get-property (v/refresh (first @f1)) :vertex-id))
               (g/transact!
                (v/get-property (v/refresh (first @f2)) :vertex-id))) "The futures have the correct values.")

        (is (= 1 (count
                  (g/transact! (v/find-by-kv :vertex-id random-long))))
            "*graph* has only one vertex with the specified vertex-id"))))
  (testing "With retries and an exponential backoff function"
    (g/transact!
     (t/create-vertex-key-once :vertex-id Long {:indexed true
                                                :unique true}))
    (let [backoff-fn (fn [try-count] (+ (Math/pow 10 try-count) (* try-count (rand-int 100))))
          random-long (long (rand-int 100000))
          f1 (future (g/retry-transact! 3 backoff-fn (v/upsert! :vertex-id {:vertex-id random-long})))
          f2 (future (g/retry-transact! 3 backoff-fn (v/upsert! :vertex-id {:vertex-id random-long})))]

      (is (= random-long
             (g/transact!
              (v/get-property (v/refresh (first @f1)) :vertex-id))
             (g/transact!
              (v/get-property (v/refresh (first @f2)) :vertex-id))) "The futures have the correct values.")

      (is (= 1 (count
                (g/transact! (v/find-by-kv :vertex-id random-long))))
          "*graph* has only one vertex with the specified vertex-id")))
  (g/shutdown)
  (clear-db))