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
