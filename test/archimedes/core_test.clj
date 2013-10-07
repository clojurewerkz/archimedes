(ns archimedes.core-test
  (:use clojure.test)
  (:require [archimedes.core :as c]
            [archimedes.vertex :as v])
  (:import  (com.tinkerpop.blueprints Element TransactionalGraph TransactionalGraph$Conclusion)
            (com.tinkerpop.blueprints.impls.tg TinkerGraphFactory TinkerGraph)))

(deftest test-opening-a-graph-in-memory
  (testing "Graph in memory"
    (is (= (type (c/clean-tinkergraph))
           TinkerGraph))))


