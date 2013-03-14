(ns archimedes.element-test
  (:use [clojure.test])
  (:require [archimedes.core :as g]
            [archimedes.vertex :as v]
            [archimedes.edge :as e]))

(deftest test-get-keys
  (g/use-clean-graph!)
  (let [a (v/create-with-id! 100 {:name "v1" :a 1 :b 1})
        b (v/create-with-id! 101 {:name "v2" :a 1 :b 1})
        c (e/connect-with-id! 102 a :label b {:name "e1" :a 1 :b 1})
        coll-a (v/keys a)
        coll-b (v/keys b)
        coll-c (v/keys c)]
    (is (= #{:name :a :b} coll-a coll-b coll-c))
    (is (= clojure.lang.PersistentHashSet (type coll-a)))))

(deftest test-get-id
  (g/use-clean-graph!)
  (let [a (v/create-with-id! 100)
        b (v/create-with-id! 101)
        c (e/connect-with-id! 102 a :label b )]
    (is (= java.lang.String (type (v/id-of a))))
    (is (= java.lang.String (type (e/id-of c))))))

(deftest test-remove-property!
  (g/use-clean-graph!)
  (let [a (v/create-with-id! 100 {:a 1})
        b (v/create-with-id! 101)
        c (e/connect-with-id! 102 a :label b {:a 1})]
    (v/dissoc! a :a)
    (v/dissoc! c :a)
    (is (nil? (:a (v/to-map a))))
    (is (nil? (:a (v/to-map c))))))


(deftest test-clear!
  (g/use-clean-graph!)
  (let [a (v/create-with-id! 100 {:a 1})
        b (v/create-with-id! 101)
        c (e/connect-with-id! 102 a :label b {:a 1})]
    (v/clear! a)
    (e/clear! c)
    (is (empty? (v/keys a)))
    (is (empty? (e/keys c)))))


(deftest test-update!
  (g/use-clean-graph!)
  (let [a (v/create-with-id! 100 {:a 1})
        b (v/create-with-id! 101)
        c (e/connect-with-id! 102 a :label b {:a 1})]
    (v/update! a :a + 9)
    (v/update! a :b (constantly 10))
    (e/update! c :a + 9)
    (e/update! c :b (constantly 10))    
    (is (= 10 (v/get a :a)))
    (is (= 10 (v/get c :a)))
    (is (= 10 (v/get a :b)))
    (is (= 10 (v/get c :b)))))
