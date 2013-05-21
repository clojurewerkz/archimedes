(ns archimedes.edge-test
  (:use [clojure.test])
  (:require [archimedes.core :as g]
            [archimedes.edge :as e]
            [archimedes.vertex :as v]))

(deftest test-delete
  (g/use-clean-graph!)
  (let [u (v/create-with-id! 100)
        w (v/create-with-id! 101)
        a (e/connect-with-id! 102 u :test w)
        a-id (e/id-of a)]
    (e/remove! a)
    (is (=  nil (e/find-by-id a-id)))))

(deftest test-simple-property-mutation
  (g/use-clean-graph!)
  (let [v1 (v/create-with-id! 100 {:name "v1"})
        v2 (v/create-with-id! 101 {:name "v2"})
        edge (e/connect-with-id! 102 v1 :test v2 {:a 1})]
    (e/assoc! edge :b 2)
    (e/dissoc! edge :a)
    (is (= 2   (e/get edge :b)))
    (is (= nil (e/get edge :a)))))

(deftest test-multiple-property-mutation
  (g/use-clean-graph!)
  (let [v1 (v/create-with-id! 100 {:name "v1"})
        v2 (v/create-with-id! 101 {:name "v2"})
        edge (e/connect-with-id! 102 v1 :test v2  {:a 0})]
    (e/merge! edge {:a 1 :b 2 :c 3})
    (is (= 1 (e/get edge :a)))
    (is (= 2 (e/get edge :b)))
    (is (= 3 (e/get edge :c)))))

(deftest test-get-all-edges
  (g/use-clean-graph!)
  (let [v1 (v/create-with-id! 100 {:name "v1"})
        v2 (v/create-with-id! 101 {:name "v2"})
        edge (e/connect-with-id! 102 v1 :test v2  {:a 0})
        edge (e/connect-with-id! 103 v1 :test v2  {:a 1})
        edge (e/connect-with-id! 104 v1 :test v2  {:a 2})]
    (is (= 3 (count (e/get-all-edges))))))

(deftest test-to-map
  (g/use-clean-graph!)
  (let [v1 (v/create-with-id! 100 {:name "v1"})
        v2 (v/create-with-id! 101 {:name "v2"})
        edge (e/connect-with-id! 102 v1 :test v2 {:a 1 :b 2 :c 3})
        prop-map (e/to-map edge)]
    (is (= {:a 1 :b 2 :c 3} (dissoc prop-map :__id__ :__label__)))))

(deftest test-to-map-id
  (g/use-clean-graph!)
  (let [id :ID
        label :LABEL]    
    (try
      (g/set-element-id-key! id)
      (g/set-edge-label-key! label)
      (let [v1 (v/create-with-id! 100 {:name "v1"})
            v2 (v/create-with-id! 101 {:name "v2"})
            edge (e/connect-with-id! 102 v1 :test v2 {:a 1 :b 2 :c 3})
            prop-map (e/to-map edge)]
      (is (= {:a 1 :b 2 :c 3 id "102" label :test}  prop-map)))
      (finally
        (g/set-element-id-key! :__id__)
        (g/set-edge-label-key! :__label__)))))

(deftest test-endpoints
  (g/use-clean-graph!)
  (let [v1   (v/create-with-id! 100 {:name "v1"})
        v2   (v/create-with-id! 101 {:name "v2"})
        edge (e/connect-with-id! 102 v1 :connexion v2)]
    (is (= ["v1" "v2"] (map #(e/get % :name) (e/endpoints edge))))))

(deftest test-get-vertex
  (g/use-clean-graph!)
  (let [v1   (v/create-with-id! 100 {:name "v1"})
        v2   (v/create-with-id! 101 {:name "v2"})
        edge (e/connect-with-id! 102 v1 :connexion v2)]
    (is (= v1 (e/get-vertex edge :out)))
    (is (= v2 (e/get-vertex edge :in)))))

(deftest test-tail-vertex
  (g/use-clean-graph!)
  (let [v1   (v/create-with-id! 100 {:name "v1"})
        v2   (v/create-with-id! 101 {:name "v2"})
        edge (e/connect-with-id! 102 v1 :connexion v2)]
    (is (= v1 (e/tail-vertex edge)))))

(deftest test-head-vertex
  (g/use-clean-graph!)
  (let [v1   (v/create-with-id! 100 {:name "v1"})
        v2   (v/create-with-id! 101 {:name "v2"})
        edge (e/connect-with-id! 102 v1 :connexion v2)]
    (is (= v2 (e/head-vertex edge)))))

(deftest test-refresh
  (g/use-clean-graph!)
  (let [v1 (v/create-with-id! 100 {:name "v1"})
        v2 (v/create-with-id! 101 {:name "v2"})
        edge (e/connect-with-id! 102 v1 :connexion v2 )
        fresh-edge (e/refresh edge)]
    (is fresh-edge)
    (is (= (.getId edge) (.getId fresh-edge)))
    (is (= (e/to-map edge) (e/to-map fresh-edge)))))

(deftest test-upconnect!
  (testing "Upconnecting once without data"
    (g/use-clean-graph!)
    (let [v1 (v/create-with-id! 100 {:name "v1"})
          v2 (v/create-with-id! 101 {:name "v2"})
          edge (e/unique-upconnect-with-id! 102 v1 :connexion v2)]
      (is (e/connected? v1 v2))
      (is (e/connected? v1 :connexion v2))
      (is (not (e/connected? v2 v1)))
      (is (= 1 (count (seq (.getEdges g/*graph*)))))))

  (testing "Upconnecting once"
    (g/use-clean-graph!)
    (let [v1 (v/create-with-id! 100 {:name "v1"})
          v2 (v/create-with-id! 101 {:name "v2"})
          edge (e/unique-upconnect-with-id! 102 v1 :connexion v2 {:name "the edge"})]
      (is (e/connected? v1 v2))
      (is (e/connected? v1 :connexion v2))
      (is (not (e/connected? v2 v1)))
      (is (= "the edge" (e/get edge :name)))
      (is (= 1 (count (seq (.getEdges g/*graph*)))))))

  (testing "Upconnecting multiple times"
    (g/use-clean-graph!)
    (let [v1 (v/create-with-id! 100 {:name "v1"})
          v2 (v/create-with-id! 101 {:name "v2"})
          edge (e/unique-upconnect-with-id! 102 v1 :connexion v2 {:name "the edge"})
          edge (e/unique-upconnect-with-id! 103 v1 :connexion v2 {:a 1 :b 2})
          edge (e/unique-upconnect-with-id! 104 v1 :connexion v2 {:b 0})]
      (is (e/connected? v1 v2))
      (is (e/connected? v1 :connexion v2))
      (is (not (e/connected? v2 v1)))
      (is (= "the edge" (e/get edge :name)))
      (is (= 1 (e/get edge :a)))
      (is (= 0 (e/get edge :b)))
      (is (= 1 (count (seq (.getEdges g/*graph*))))))))
