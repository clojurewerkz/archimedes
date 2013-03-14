(ns archimedes.vertex-test
  (:use [clojure.test])
  (:require [archimedes.core :as g]
            [archimedes.vertex :as v]))

(deftest test-delete
  (g/use-clean-graph!)
  (let [u (v/create-with-id! 100 {:name "v1"})]
    (v/delete! u)
    (is (=  nil (v/find-by-id 100)))
    (is (empty? (v/find-by-kv :name "v1")))))

(deftest test-simple-property-mutation
  (g/use-clean-graph!)
  (let [u (v/create-with-id! 100 {:name "v1" :a 1 :b 1})]
    (v/assoc! u :b 2)
    (v/dissoc! u :a)
    (is (= 2   (v/get u :b)))
    (is (= nil (v/get u :a)))
    (is (= 10 (v/get u :a 10)))    ))

(deftest test-multiple-property-mutation
  (g/use-clean-graph!)
  (let [u (v/create-with-id! 100
            {:name "v1" :a 0 :b 2})]
    (v/merge! u {:a 1 :b 2 :c 3})
    (is (= 1   (v/get u :a)))
    (is (= 2   (v/get u :b)))
    (is (= 3   (v/get u :c)))))

(deftest test-to-map
  (g/use-clean-graph!)
  (let [v1 (v/create-with-id! 100 {:name "v1" :a 1 :b 2 :c 3})
        props (v/to-map v1)]
    (is (= 1 (props :a)))
    (is (= 2 (props :b)))
    (is (= 3 (props :c)))))

(deftest test-find-by-id-single
  (g/use-clean-graph!)
  (let [v1 (v/create-with-id! 100 {:prop 1})
        v1-maybe (v/find-by-id 100)]
    (is (= 1 (v/get v1-maybe :prop)))))

(deftest test-find-by-id-multiple
  (g/use-clean-graph!)
  (let [v1 (v/create-with-id! 100 {:prop 1})
        v2 (v/create-with-id! 101 {:prop 2})
        v3 (v/create-with-id! 102 {:prop 3})
        ids (map v/id-of [v1 v2 v3])
        v-maybes (apply v/find-by-id ids)]
    (is (= (range 1 4) (map #(v/get % :prop) v-maybes)))))

(deftest test-find-by-kv
  (g/use-clean-graph!)
  (let [v1 (v/create-with-id! 100 {:age  1
                                   :name "A"})
        v2 (v/create-with-id! 101 {:age 2
                                   :name "B"})
        v3 (v/create-with-id! 102 {:age 2
                                   :name "C"})]
    (is (= #{"A"}
           (set (map #(v/get % :name) (v/find-by-kv :age 1)))))
    (is (= #{"B" "C"}
           (set (map #(v/get % :name) (v/find-by-kv :age 2)))))))

;; ;; (deftest test-upsert!
;; ;;   (g/use-clean-graph!)
;; ;;   (let [v1-a (v/upsert! :first-name
;; ;;                         {:first-name "Zack" :last-name "Maril" :age 21})
;; ;;         v1-b (v/upsert! :first-name
;; ;;                         {:first-name "Zack" :last-name "Maril" :age 22})
;; ;;         v2   (v/upsert! :first-name
;; ;;                         {:first-name "Brooke" :last-name "Maril" :age 19})]
;; ;;     (is (= 22
;; ;;            (v/get-property (v/refresh (first v1-a)) :age)
;; ;;            (v/get-property (v/refresh (first v1-b)) :age)))
;; ;;     (v/upsert! :last-name {:last-name "Maril"
;; ;;                            :heritage "Some German Folks"})
;; ;;     (is (= "Some German Folks"
;; ;;            (v/get-property (v/refresh (first v1-a)) :heritage)
;; ;;            (v/get-property (v/refresh (first v1-b)) :heritage)
;; ;;            (v/get-property (v/refresh (first v2)) :heritage)))))
