(ns hermes.edge-test
  (:use [clojure.test]
        [hermes.conf :only (clear-db conf)])
  (:require [hermes.core :as g]
            [hermes.edge :as e]
            [hermes.vertex :as v]))

(deftest test-edges
  (clear-db)
  (g/open conf)

  (testing "Edge deletion"
    (g/transact!
     (let [u (v/create!)
           w (v/create!)
           a (e/connect! u :test w)
           a-id (e/get-id a)]
       (e/delete! a)
       (is (=  nil (e/find-by-id a-id))))))

  (testing "Single property mutation"
    (g/transact!
     (let [v1 (v/create! {:name "v1"})
           v2 (v/create! {:name "v2"})
           edge (e/connect! v1 :test v2 {:a 1})]
       (e/set-property! edge :b 2)
       (e/remove-property! edge :a)
       (is (= 2   (e/get-property edge :b)))
       (is (= nil (e/get-property edge :a))))))

  (testing "Multiple property mutation"
    (g/transact!
     (let [v1 (v/create! {:name "v1"})
           v2 (v/create! {:name "v2"})
           edge (e/connect! v1 :test v2 {:a 0})]
       (e/set-properties! edge {:a 1 :b 2 :c 3})
       (is (= 1 (e/get-property edge :a)))
       (is (= 2 (e/get-property edge :b)))
       (is (= 3 (e/get-property edge :c))))))

  (testing "Property map"
    (g/transact!
     (let [v1 (v/create! {:name "v1"})
           v2 (v/create! {:name "v2"})
           edge (e/connect! v1 :test v2 {:a 1 :b 2 :c 3})
           prop-map (e/prop-map edge)]
       (is (= {:a 1 :b 2 :c 3} (dissoc prop-map :__id__ :__label__))))))

  (testing "Endpoints"
    (g/transact!
     (let [v1 (v/create! {:name "v1"})
           v2 (v/create! {:name "v2"})
           edge (e/connect! v1 :connexion v2)]
       (is (= ["v1" "v2"] (map #(e/get-property % :name) (e/endpoints edge)))))))

  (testing "Refresh"
    (let [v1 (g/transact! (v/create! {:name "v1"}))
          v2 (g/transact! (v/create! {:name "v2"}))
          edge (g/transact! (e/connect! (v/refresh v1) :connexion (v/refresh v2)))
          fresh-edge (g/transact! (e/refresh edge))]
      (is fresh-edge)
      (is (g/transact! (= (.getId (e/refresh edge)) (.getId (e/refresh fresh-edge)))))
      (is (g/transact! (= (e/prop-map (e/refresh edge)) (e/prop-map (e/refresh fresh-edge)))))))

  (testing "Edges between"
    (let [v1 (g/transact! (v/create! {:name "v1"}))
          v2 (g/transact! (v/create! {:name "v2"}))
          edge (g/transact! (e/connect! (v/refresh v1) :connexion (v/refresh v2)))
          found-edges (g/transact! (e/edges-between (v/refresh v1) (v/refresh v2)))]
      (is edge)
      (is (g/transact! (= (e/prop-map (e/refresh edge))
                          (e/prop-map (e/refresh (first found-edges))))))))
  
  (testing "Upconnect!"
    (testing "Upconnecting once"
      (g/transact!
       (let [v1 (v/create! {:name "v1"})
             v2 (v/create! {:name "v2"})
             edge (first (e/upconnect! v1 :connexion v2 {:prop "the edge"}))]
         (is (e/connected? v1 v2))
         (is (e/connected? v1 v2 :connexion))
         (is (not (e/connected? v2 v1)))
         (is (= "the edge" (e/get-property edge :prop))))))

    (testing "Upconnecting multiple times"
      (g/transact!
       (let [v1 (v/create! {:name "v1"})
             v2 (v/create! {:name "v2"})
             edge (first (e/upconnect! v1 :connexion v2 {:prop "the edge"}))
             edge (first (e/upconnect! v1 :connexion v2 {:a 1 :b 2}))
             edge (first (e/upconnect! v1 :connexion v2 {:b 0}))]
         (is (e/connected? v1 v2))
         (is (e/connected? v1 v2 :connexion))
         (is (not (e/connected? v2 v1)))
         (is (= "the edge" (e/get-property edge :prop)))
         (is (= 1 (e/get-property edge :a)))
         (is (= 0 (e/get-property edge :b)))))))

  (testing "unique-upconnect!"
    (testing "Once"
      (g/transact!
       (let [v1 (v/create! {:name "v1"})
             v2 (v/create! {:name "v2"})
             edge (e/unique-upconnect! v1 :connexion v2 {:prop "the edge"})]
         (is (e/connected? v1 v2))
         (is (e/connected? v1 v2 :connexion))
         (is (not (e/connected? v2 v1)))
         (is (= "the edge" (e/get-property edge :prop))))))

    (testing "Multiple times"
      (g/transact!
       (let [v1 (v/create! {:name "v1"})
             v2 (v/create! {:name "v2"})
             edge (e/unique-upconnect! v1 :connexion v2 {:prop "the edge"})
             edge (e/unique-upconnect! v1 :connexion v2 {:a 1 :b 2})
             edge (e/unique-upconnect! v1 :connexion v2 {:b 0})]
         (is (e/connected? v1 v2))
         (is (e/connected? v1 v2 :connexion))
         (is (not (e/connected? v2 v1)))
         (is (= "the edge" (e/get-property edge :prop)))
         (is (= 1 (e/get-property edge :a)))
         (is (= 0 (e/get-property edge :b)))
         (e/connect! v1 :connexion v2)
         (is (thrown? Throwable #"There were 2 vertices returned."
                      (e/unique-upconnect! v1 :connexion v2)))))))
  (g/shutdown)
  (clear-db))