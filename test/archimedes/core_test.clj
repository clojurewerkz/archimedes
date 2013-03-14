(ns archimedes.core-test
  (:use clojure.test)
  (:require [archimedes.core :as g]
            [archimedes.vertex :as v])
  (:import  (com.tinkerpop.blueprints Element TransactionalGraph TransactionalGraph$Conclusion)
            (com.tinkerpop.blueprints.impls.tg TinkerGraphFactory TinkerGraph)))

(deftest test-opening-a-graph-in-memory
  (testing "Graph in memory"
    (g/use-clean-graph!)
    (is (= (type g/*graph*)
           TinkerGraph))))

(deftest test-with-graph
  (testing "with-graph macro"
    ; Open the usual *graph*
    (g/use-clean-graph!)
    ; Open a real graph the hard wary
    (let [graph (TinkerGraphFactory/createTinkerGraph)]
      (is (= 6 (count (seq (.getVertices graph)))) "graph has the new vertex")
      (is (= 0 (count (seq (.getVertices g/*graph*)))) "the usual *graph* is still empty"))))

(deftest test-retry-transact!
  (testing "with backoff function"
    (g/use-clean-graph!)
    (let [sum (partial reduce +)
          clock (atom [])
          punch-clock (fn [] (swap! clock concat [(System/currentTimeMillis)]))]
      (is (thrown? Exception (g/retry-transact! 3 (fn [n] (* n 100))
                                                (punch-clock)
                                                (/ 1 0))))
      (let [[a,b,c] (map (fn [a b] (- a b)) (rest @clock) @clock)]
        (is (>= a 100))
        (is (>= b 200))
        (is (>= c 300)))))

  (testing "with transaction that returns nil"
    (g/use-clean-graph!)
    (g/retry-transact! 3 10
      (v/create-with-id! 100)
      nil)
    (is (= 1 (count (seq (.getVertices g/*graph*)))) "graph has the new vertex")))


