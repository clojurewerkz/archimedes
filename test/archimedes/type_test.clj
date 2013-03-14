(ns hermes..type-test
  (:use [clojure.test]
        [hermes.conf :only (clear-db conf)])
  (:require [hermes.type :as t]
            [hermes.core :as g]))

(deftest test-create-groups
  (clear-db)
  (g/open conf)
  (g/transact!
   (let [my-group-name "My-Group-Name"
         my-group (t/create-group 100 my-group-name)
         default-group-id (.getID t/default-group)]
     (is (= my-group-name (.getName my-group))
         "The group has the correct name")
     (is (= 100 (.getID my-group))
         "The group has the correct ID")
     (is (not (= default-group-id (.getID my-group)))
         "my-group has a different ID from the default group")))
  (g/shutdown))

(deftest test-create-vertex-key
  (clear-db)
  (g/open conf)
  (g/transact!
   (testing "With no parameters"
     (t/create-vertex-key :first-key Integer)
     ;; Get the key from the graph
     (let [k (t/get-type :first-key)]
       (is (.isPropertyKey k) "the key is a property key")
       (is (not (.isEdgeLabel k)) "the key is not an edge label")
       (is (= "first-key" (.getName k)) "the key has the correct name")
       (is (not (.isFunctional k)) "the key is not functional")
       (is (not (.hasIndex k)) "the key is not indexed")
       (is (not (.isUnique k)) "the key is not unique"))))
  
  (g/transact! 
   (testing "With parameters"
     (t/create-vertex-key :second-key Integer
                          {:functional true
                           :indexed true
                           :unique true})
     ;; Get the key from the graph
     (let [k (t/get-type :second-key)]
       (is (.isPropertyKey k) "the key is a property key")
       (is (not (.isEdgeLabel k)) "the key is not an edge label")
       (is (= "second-key" (.getName k)) "the key has the correct name")
       (is (.isFunctional k) "the key is functional")
       (is (.hasIndex k) "the key is indexed")
       (is (.isUnique k) "the key is unique"))))
  (g/shutdown))

(deftest test-create-edge-label
  (clear-db)
  (g/open conf)
  (g/transact!
   (testing "With no parameters"
     (t/create-edge-label :first-label)
     ;; Get the label from the graph
     (let [lab (t/get-type :first-label)]
       (is (.isEdgeLabel lab) "the label is an edge label")
       (is (not (.isPropertyKey lab)) "the label is not a property key")
       (is (= "first-label" (.getName lab)) "the label has the correct name")
       (is (not (.isFunctional lab)) "the label is not functional")
       (is (not (.isSimple lab)) "the label is not simple")
       (is (not (.isUndirected lab)) "the label is not undirected")
       (is (not (.isUnidirected lab)) "the label is not unidirected")
       (is (= t/default-group (.getGroup lab)) "the label has the default group"))))
  (g/transact!
   (testing "With parameters"

     (t/create-edge-label :second-label
                          {:functional true
                           :simple true
                           :direction "undirected"})
     ;; Get the label from the graph
     (let [lab (t/get-type :second-label)]
       (is (.isEdgeLabel lab) "the label is an edge label")
       (is (not (.isPropertyKey lab)) "the label is not a property key")
       (is (= "second-label" (.getName lab)) "the label has the correct name")
       (is (.isFunctional lab) "the label is functional")
       (is (.isSimple lab) "the label is simple")
       (is (.isUndirected lab) "the label is undirected")
       (is (= t/default-group (.getGroup lab)) "the label has the default group"))))
  (g/transact!
   (testing "With a group"
     (let [a-group (t/create-group 20 "a-group")
           the-label (t/create-edge-label :lab {:group a-group})]
       (is (.isEdgeLabel the-label) "the label is an edge label")
       (is (= a-group (.getGroup the-label)) "the label has the correct group"))))
  (g/shutdown))