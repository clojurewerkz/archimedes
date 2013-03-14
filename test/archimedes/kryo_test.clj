(ns hermes.kryo
  (:use [clojure.test])
  (:use [hermes.conf :only (conf clear-db)])
  (:require [hermes.core :as g]
            [hermes.edge :as e]
            [hermes.vertex :as v]
            [hermes.kryo :as k]))

(deftest test-kryo
  (clear-db)
  (g/open conf)
  
  (testing "Persisting vector and back again"
    (let [a [1 2 3]
          b ["a" "b" "c"]
          c [1 "b" "c"]
          a-u (g/transact! (v/create! {:a a}))
          b-u (g/transact! (v/create! {:b b}))
          c-u (g/transact! (v/create! {:c c}))]
      (is (= clojure.lang.PersistentVector
             (type (g/transact! (v/get-property (v/refresh a-u) :a)))))
      (is (= a (g/transact! (v/get-property (v/refresh a-u) :a))))
      (is (= b (g/transact! (v/get-property (v/refresh b-u) :b))))
      (is (= c (g/transact! (v/get-property (v/refresh c-u) :c))))))

  (testing "Persisting list and back again"
    (let [a '(1 2 3)
          b '("a" "b" "c")
          c '(1 "b" "2")
          a-u (g/transact! (v/create! {:a a}))
          b-u (g/transact! (v/create! {:b b}))
          c-u (g/transact! (v/create! {:c c}))]
      (is (= clojure.lang.PersistentVector
             (type (g/transact! (v/get-property (v/refresh a-u) :a)))))
      (is (= a (g/transact! (v/get-property (v/refresh a-u) :a))))
      (is (= b (g/transact! (v/get-property (v/refresh b-u) :b))))
      (is (= c (g/transact! (v/get-property (v/refresh c-u) :c))))))

  (testing "Persisting map and back again"
    (let [a {:a 1 :b 2 :c 3}
          b {:a "a" :b "b" :c "c"}
          c {:a-a "a" :b 2 :c "c"}
          a-u (g/transact! (v/create! {:a a}))
          b-u (g/transact! (v/create! {:b b}))
          c-u (g/transact! (v/create! {:c c}))]
      (is (= clojure.lang.PersistentArrayMap
             (type (g/transact! (v/get-property (v/refresh a-u) :a)))))
      (is (= a (g/transact! (v/get-property (v/refresh a-u) :a))))
      (is (= b (g/transact! (v/get-property (v/refresh b-u) :b))))
      (is (= c (g/transact! (v/get-property (v/refresh c-u) :c))))))

  ;;TODO: figure out how to test PersistentHashMap.

  (testing "Persisting map and back again"
    (let [a {:a 1 :b 2 :c 3}
          b {:a "a" :b "b" :c "c"}
          c {:a-a "a" :b 2 :c "c"}
          a-u (g/transact! (v/create! {:a a}))
          b-u (g/transact! (v/create! {:b b}))
          c-u (g/transact! (v/create! {:c c}))]
      (is (= clojure.lang.PersistentArrayMap
             (type (g/transact! (v/get-property (v/refresh a-u) :a)))))
      (is (= a (g/transact! (v/get-property (v/refresh a-u) :a))))
      (is (= b (g/transact! (v/get-property (v/refresh b-u) :b))))
      (is (= c (g/transact! (v/get-property (v/refresh c-u) :c))))))

  (testing "Mixed data structures"
    (let [a {:a [1 "a" 3] :b #{1 "a" 3}}
          b #{[1 "a" 3] {:a 1 :b 2}}
          c [#{1 "a 3"} {:a 1 :b 2}]
          d #{{:unholy [1 2 3 #{"a"}] :unwieldy "random"}
              {:why "oh why" :a  "test"}}
          a-u (g/transact! (v/create! {:a a}))
          b-u (g/transact! (v/create! {:b b}))
          c-u (g/transact! (v/create! {:c c}))
          d-u (g/transact! (v/create! {:d d}))]
      (is (= a (g/transact! (v/get-property (v/refresh a-u) :a))))
      (is (= b (g/transact! (v/get-property (v/refresh b-u) :b))))
      (is (= c (g/transact! (v/get-property (v/refresh c-u) :c))))
      (is (= d (g/transact! (v/get-property (v/refresh d-u) :d))))))

  (testing "No persisting of keywords"
    (is (thrown? Throwable #"keyword" (g/transact! (v/create! {:a :a})))))
  (g/shutdown)
  )