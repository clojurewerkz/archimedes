;; (ns archimedes.element-test
;;   (:use [clojure.test])
;;   (:import [com.thinkaurelius.titan.graphdb.relations RelationIdentifier])
;;   (:require [archimedes.core :as g]
;;             [archimedes.vertex :as v]
;;             [archimedes.edge :as e]))

;; (deftest test-get-keys
;;   (g/use-clean-graph!)
;;   (let [a (v/create! {:name "v1" :a 1 :b 1})
;;         b (v/create! {:name "v2" :a 1 :b 1})
;;         c (e/connect! a b  "test-label" {:name "e1" :a 1 :b 1})
;;         coll-a (v/get-keys a)
;;         coll-b (v/get-keys b)
;;         coll-c (v/get-keys c)]
;;     (is (= #{:name :a :b} coll-a coll-b coll-c))
;;     (is (= clojure.lang.PersistentHashSet (type coll-a)))))

;; (deftest test-get-id
;;   (g/use-clean-graph!)
;;   (let [a (v/create!)
;;         b (v/create!)
;;         c (e/connect! a b "test-label")]
;;     (is (= java.lang.Long (type (v/get-id a))))
;;     (is (= RelationIdentifier (type (e/get-id c))))))

;; (deftest test-remove-property!
;;   (g/use-clean-graph!)
;;   (let [a (v/create! {:a 1})
;;         b (v/create!)
;;         c (e/connect! a b "test-label" {:a 1})]
;;     (v/remove-property! a :a)
;;     (v/remove-property! c :a)
;;     (is (nil? (:a (v/prop-map a))))
;;     (is (nil? (:a (v/prop-map a))))))
