;; (ns archimedes.edge-test
;;   (:use [clojure.test])
;;   (:require [archimedes.core :as g]
;;             [archimedes.edge :as e]
;;             [archimedes.vertex :as v]))

;; (deftest test-delete
;;   (g/use-clean-graph!)
;;   (let [u (v/create-with-id! 100)
;;         w (v/create-with-id! 101)
;;         a (e/connect-with-id! 102 u :test w)
;;         a-id (e/id-of a)]
;;     (e/delete! a)
;;     (is (=  nil (e/find-by-id a-id)))))

;; (deftest test-simple-property-mutation
;;   (g/use-clean-graph!)
;;   (let [v1 (v/create-with-id! 100 {:name "v1"})
;;         v2 (v/create-with-id! 101 {:name "v2"})
;;         edge (e/connect-with-id! 102 v1 :test v2 {:a 1})]
;;     (e/assoc! edge :b 2)
;;     (e/dissoc! edge :a)
;;     (is (= 2   (e/get edge :b)))
;;     (is (= nil (e/get edge :a)))))

;; (deftest test-multiple-property-mutation
;;   (g/use-clean-graph!)
;;   (let [v1 (v/create-with-id! 100 {:name "v1"})
;;         v2 (v/create-with-id! 101 {:name "v2"})
;;         edge (e/connect-with-id! 102 v1 :test v2  {:a 0})]
;;     (e/merge! edge {:a 1 :b 2 :c 3})
;;     (is (= 1 (e/get edge :a)))
;;     (is (= 2 (e/get edge :b)))
;;     (is (= 3 (e/get edge :c)))))

;; (deftest test-property-map
;;   (g/use-clean-graph!)
;;   (let [v1 (v/create-with-id! 100 {:name "v1"})
;;         v2 (v/create-with-id! 101 {:name "v2"})
;;         edge (e/connect-with-id! 102 v1 :test v2 {:a 1 :b 2 :c 3})
;;         prop-map (e/to-map edge)]
;;     (is (= {:a 1 :b 2 :c 3} (dissoc prop-map :__id__ :__label__)))))

;; (deftest test-endpoints
;;   (g/use-clean-graph!)
;;   (let [v1 (v/create-with-id! 100 {:name "v1"})
;;         v2 (v/create-with-id! 101 {:name "v2"})
;;         edge (first (e/connect-with-id! 102 v1 :connexion v2))]
;;     (is (= ["v1" "v2"] (map #(e/get % :name) (e/endpoints edge))))))

;; (deftest test-refresh
;;   (g/use-clean-graph!)
;;   (let [v1 (v/create-with-id! 100 {:name "v1"})
;;         v2 (v/create-with-id! 101 {:name "v2"})
;;         edge (first (e/connect-with-id! 102 v1 :connexion v2 ))
;;         fresh-edge (e/refresh edge)]
;;     (is fresh-edge)
;;     (is (= (.getId edge) (.getId fresh-edge)))
;;     (is (= (e/to-map edge) (e/to-map fresh-edge)))))

;; ;; (deftest test-upconnect!
;; ;;   (testing "Upconnecting once"
;; ;;     (g/use-clean-graph!)
;; ;;     (let [v1 (v/create! {:name "v1"})
;; ;;           v2 (v/create! {:name "v2"})
;; ;;           edge (first (e/upconnect! v1 v2 "connexion" {:name "the edge"}))]
;; ;;       (is (e/connected? v1 v2))
;; ;;       (is (e/connected? v1 v2 "connexion"))
;; ;;       (is (not (e/connected? v2 v1)))
;; ;;       (is (= "the edge" (e/get-property edge :name)))
;; ;;       (is (= 1 (count (seq (.getEdges g/*graph*)))))))

;; ;;   (testing "Upconnecting multiple times"
;; ;;     (g/use-clean-graph!)
;; ;;     (let [v1 (v/create! {:name "v1"})
;; ;;           v2 (v/create! {:name "v2"})
;; ;;           edge (first (e/upconnect! v1 v2 "connexion" {:name "the edge"}))
;; ;;           edge (first (e/upconnect! v1 v2 "connexion" {:a 1 :b 2}))
;; ;;           edge (first (e/upconnect! v1 v2 "connexion" {:b 0}))]
;; ;;       (is (e/connected? v1 v2))
;; ;;       (is (e/connected? v1 v2 "connexion"))
;; ;;       (is (not (e/connected? v2 v1)))
;; ;;       (is (= "the edge" (e/get-property edge :name)))
;; ;;       (is (= 1 (e/get-property edge :a)))
;; ;;       (is (= 0 (e/get-property edge :b)))
;; ;;       (is (= 1 (count (seq (.getEdges g/*graph*))))))))


