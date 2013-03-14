(ns archimedes.edge
  (:import (com.tinkerpop.blueprints Edge Direction))
  (:require [archimedes.vertex :as v]
            [archimedes.type   :as t])  
  (:use [archimedes.core :only (*graph* *pre-fn* *post-fn*)]
        [archimedes.util :only (immigrate)]))

(immigrate 'archimedes.element)

;;
;;Information getters
;;
(defn find-by-id
  "Retrieves nodes by id from the graph."
  [& ids]
  (ensure-graph-is-transaction-safe)
  (if (= 1 (count ids))
    (.getEdge *graph* (first ids))
    (seq (for [id ids] (.getEdge *graph* id)))))


(defn get-label
  "Get the label of the edge"
  [edge]
  (keyword (.. edge getTitanLabel getName)))

(defn prop-map
  "Get the property map of the edge"
  [edge]
  (into {:__id__ (get-id edge)
         :__label__ (get-label edge)}
        (map #(vector (keyword %) (get-property edge %)) (get-keys edge))))

(defn endpoints
  "Returns the endpoints of the edge in array with the order [starting-node,ending-node]."
  [this]
  (ensure-graph-is-transaction-safe)
  [(.getVertex this Direction/OUT)
   (.getVertex this Direction/IN)])
;;
;;Transaction management
;;

(defn refresh
  "Goes and grabs the edge from the graph again. Useful for \"refreshing\" stale edges."
  [edge]
  (ensure-graph-is-transaction-safe)
  (.getEdge *graph* (.getId edge)))

;;
;;Creation methods
;;

(defn connect!
  "Connects two vertices with the given label, and, optionally, with the given properties."
  ([v1 label v2] (connect! v1 (name label) v2 {}))
  ([v1 label v2 data]
     (ensure-graph-is-transaction-safe)
     (let [edge (.addEdge *graph* v1 v2 (name label))]
       (set-properties! edge data)
       edge)))

;;
;;Deletion methods
;;

(defn delete!
  "Delete an edge."
  [edge]
  (.removeEdge *graph* edge ))
