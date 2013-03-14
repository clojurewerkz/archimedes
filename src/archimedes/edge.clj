(ns archimedes.edge
    (:refer-clojure :exclude [keys vals assoc! dissoc! get])
     (:import (com.tinkerpop.blueprints Edge Direction)
              (com.tinkerpop.blueprints.impls.tg TinkerGraph))
     (:require [archimedes.vertex :as v]
               [archimedes.core :refer (*graph* *pre-fn*)]
               [archimedes.util :refer (immigrate)]))

(immigrate 'archimedes.element)

;;
;;Transaction management
;;

(defn refresh
  "Goes and grabs the edge from the graph again. Useful for \"refreshing\" stale edges."
  [edge]
  (*pre-fn*)
   (.getEdge *graph* (.getId edge)))

;;
;;Creation methods
;;

(defn connect!
  "Connects two vertices with the given label, and, optionally, with the given properties."
  ([v1 label v2] (connect! v1 (name label) v2 {}))
  ([v1 label v2 data]
     (*pre-fn*)
     (let [new-edge (.addEdge *graph* v1 v2 (name label))]
       (merge! new-edge data))))

(defn connect-with-id!
  "Connects two vertices with the given label, and, optionally, with the given properties."
  ([id v1 label v2] (connect-with-id! id v1 (name label) v2 {}))
  ([id v1 label v2 data]
     (*pre-fn*)
     (let [new-edge (.addEdge *graph* id v1 v2 (name label))]
       (merge! new-edge data))))

;;
;;Deletion methods
;;

(defn delete!
  "Delete an edge."
  [edge]
  (*pre-fn*)
  (.removeEdge *graph* edge))

;;
;;Information getters
;;
(defn label-of
  "Get the label of the edge"
  [edge]
  (*pre-fn*)
  (keyword (.. edge getLabel)))

(defn to-map
  "Returns a persisten map representing the edge."
  [edge]
  (*pre-fn*)
  (->> (keys edge)
       (map #(vector (keyword %) (get edge %)))
       (into {:__id__ (id-of edge) :__label__ (label-of edge)})))

(defn find-by-id
  "Retrieves edges by id from the graph."
  [& ids]
  (*pre-fn*)
  (if (= 1 (count ids))
                (.getEdge *graph* (first ids))
                (seq (for [id ids] (.getEdge *graph* id)))))





(defn endpoints
  "Returns the endpoints of the edge in array with the order [starting-node,ending-node]."
  [edge]
  (*pre-fn*)
  [(.getVertex edge Direction/OUT)
   (.getVertex edge Direction/IN)])

;; (defn edges-between
;;   "Returns a set of the edges between two vertices, direction considered."
;;   ([v1 v2] (edges-between v1 v2 nil))
;;   ([v1 v2 label]
;;      (*pre-fn*)
;;      ;; Source for these edge queries:
;;      ;; https://groups.google.com/forum/?fromgroups=#!topic/gremlin-users/R2RJxJc1BHI
;;      (let [edges-set (q/query v1
;;                               (q/--E> label)
;;                               q/in-vertex
;;                               (q/has "id" (.getId v2))
;;                               (q/back 2)
;;                               (q/into-vec))]
;;        (when (not (empty? edges-set))
;;          edges-set))))

;; (defn connected?
;;   "Returns whether or not two vertices are connected. Optional third
;;    arguement specifying the label of the edge."
;;   ([v1 v2] (connected? v1 v2 nil))  
;;   ([v1 v2 label]     
;;      (*pre-fn*)
;;      (not (empty? (edges-between v1 v2 label)))))

;;
;;Creation methods
;;

(defn connect!
  "Connects two vertices with the given label, and, optionally, with the given properties."
  ([v1 label v2] (connect! v1 (name label) v2 {}))
  ([v1 label v2 data]
     (*pre-fn*)
     (let [edge (.addEdge *graph* v1 v2 (name label))]
       (merge! edge data)
       edge)))

;; (defn upconnect!
;;   "Upconnect takes all the edges between the given vertices with the
;;    given label and, if the data is provided, merges the data with the
;;    current properties of the edge. If no such edge exists, then an
;;    edge is created with the given data."
;;   ([v1 label v2] (upconnect! v1 (name label) v2 {}))
;;   ([v1 label v2 data]
;;      (*pre-fn*)
;;      (if-let [edges (edges-between v1 v2 (name label))]
;;        (do
;;          (doseq [edge edges] (merge! edge data))
;;          edges)
;;        #{(connect! v1 (name label) v2 data)})))

;; (defn unique-upconnect!
;;   "Like upconnect!, but throws an error when more than element is returned."
;;   [& args]
;;   (let [upconnected (apply upconnect! args)]
;;     (if (= 1 (count upconnected))
;;       (first upconnected)
;;       (throw (Throwable.
;;               (str
;;                "Don't call unique-upconnect! when there is more than one element returned.\n"
;;                "There were " (count upconnected) " edges returned.\n"
;;                "The arguments were: " args "\n"))))))