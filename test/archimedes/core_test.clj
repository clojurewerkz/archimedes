(ns archimedes.core-test
  (:use clojure.test)
  (:require [archimedes.core :as c]
            [archimedes.vertex :as v])
  (:import  [com.tinkerpop.blueprints.impls.tg TinkerGraphFactory TinkerGraph]
            [com.thinkaurelius.titan.core TitanFactory TitanGraph]
            [java.io File]))

(deftest test-opening-a-graph-in-memory
  (testing "Graph in memory"
    (is (= (type (c/clean-tinkergraph))
           TinkerGraph))))

(deftest test-tinkergraph-does-not-support-transactions
  (testing "We cannot perform a transaction on a Tinkergraph"
    (is (thrown? java.lang.AssertionError
                 (c/with-transaction [g (c/clean-tinkergraph)] nil)))))

(let [filename-chars (vec "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789")]
  (defn- random-filename
    []
    (apply str (repeatedly 10 #(rand-nth filename-chars)))))

(defn- create-temp-dir
  []
  (let [tmpdir (System/getProperty "java.io.tmpdir")]
    (loop [remaining-attempts 10]
      (if (> remaining-attempts 0)
        (let [dir (File. tmpdir (random-filename))]
          (if (.mkdir dir)
            dir
            (recur (dec remaining-attempts))))
        (throw (Exception. "Failed to create temporary directory after 10 attempts"))))))

(defn- new-temp-titan-db
  []
  (let [dir (create-temp-dir)]
    (TitanFactory/open (.getPath dir))))

(deftest test-transaction-rollback-on-exception
  (testing "Uncaught exception reverts added vertex"
    (let [graph (new-temp-titan-db)]
      (try
        (c/with-transaction [tx graph]
          (v/create! tx {:name "Mallory"})
          (is (= (count (v/get-all-vertices tx)) 1))
          (throw (Exception. "Died")))
        (catch Exception e
          (is (= (.getMessage e) "Died"))))
      (is (empty? (v/get-all-vertices graph))))))

(deftest test-threaded-transaction-rollback-on-exception
  (testing "Uncaught exception reverts added vertex"
    (let [graph (new-temp-titan-db)]
      (try
        (c/with-transaction [tx graph :threaded true]
          (v/create! tx {:name "Mallory"})
          (is (= (count (v/get-all-vertices tx)) 1))
          (throw (Exception. "Died")))
        (catch Exception e
          (is (= (.getMessage e) "Died"))))
      (is (empty? (v/get-all-vertices graph))))))

(deftest test-transaction-commit
  (testing "Commit edit to graph"
    (let [graph (new-temp-titan-db)]
      (c/with-transaction [tx graph]
        (v/create! tx [:name "Bob"]))
      (is (= (count (v/get-all-vertices graph)) 1)))))

(deftest test-threaded-transaction-commit
  (testing "Commit edit to graph (threaded=true)"
    (let [graph (new-temp-titan-db)]
      (c/with-transaction [tx graph :threaded true]
        (v/create! tx [:name "Bob"]))
      (is (= (count (v/get-all-vertices graph)) 1)))))
