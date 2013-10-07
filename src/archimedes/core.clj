(ns archimedes.core
  (:import (com.tinkerpop.blueprints Element Graph TransactionalGraph
                                     ThreadedTransactionalGraph
                                     TransactionalGraph$Conclusion)
           (com.tinkerpop.blueprints.impls.tg TinkerGraphFactory)))

(def ^{:dynamic true} *element-id-key* :__id__)

(def ^{:dynamic true} *edge-label-key* :__label__)


(defn set-element-id-key! 
  [new-id]
  (alter-var-root (var *element-id-key*) (constantly new-id)))

(defn set-edge-label-key! 
  [new-id]
  (alter-var-root (var *edge-label-key*) (constantly new-id)))

(defn new-tinkergraph
  []
  (TinkerGraphFactory/createTinkerGraph))

(defn clean-tinkergraph
  []
  (let [g (new-tinkergraph)]
  (doseq [e (seq (.getEdges g))] (.removeEdge g e))
  (doseq [v (seq (.getVertices g))] (.removeVertex g v))
  g))

;;TODO Transactions need to be much more fine grain in terms of
;;control. And expections as well. new-transaction will only work on a
;;ThreadedTransactionalGraph.
(defn new-transaction
  "Creates a new transaction based on the given graph object."
  [g]
  (.newTransaction g))

(defn commit
  "Commit all changes to the graph."
  [g]
  (.commit g))

(defn shutdown 
  "Shutdown the graph."
  [g]
  (.shutdown g))

(defn rollback 
  "Stops the current transaction and rolls back any changes made."
  [g]
  (.rollback g))

(defn get-features
  "Get a map of features for a graph.
  (http://tinkerpop.com/docs/javadocs/blueprints/2.1.0/com/tinkerpop/blueprints/Features.html)"
  [g]
  (.. g getFeatures toMap))

(defn get-feature
  "Gets the value of the feature for a graph."
  [g s]
  (get ^Map (get-features g) s))


