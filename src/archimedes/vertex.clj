(ns archimedes.vertex
  (:refer-clojure :exclude [keys vals assoc! dissoc! get])
  (:import (com.tinkerpop.blueprints Vertex Direction Graph)
           (com.tinkerpop.blueprints.impls.tg TinkerGraph))
  (:require [archimedes.core :refer (*graph* *pre-fn* *element-id-key*)]
            [archimedes.util :refer (keywords-to-str-array)]
            [archimedes.conversion :refer (to-edge-direction)]
            [potemkin :as po]
            [archimedes.element :as ele]))

(po/import-fn ele/get)
(po/import-fn ele/keys)
(po/import-fn ele/vals)
(po/import-fn ele/id-of)
(po/import-fn ele/assoc!)
(po/import-fn ele/merge!)
(po/import-fn ele/dissoc!)
(po/import-fn ele/update!)
(po/import-fn ele/clear!)


;;
;; Transaction management
;;

(defn refresh
  "Gets a vertex back from the database and refreshes it to be usable again."
  [vertex]
  (*pre-fn*)
  (.getVertex ^Graph *graph* vertex))

;;
;; Removal methods
;;

(defn remove!  
  "Remove a vertex."
  [vertex]
  (*pre-fn*)
  (.removeVertex ^Graph *graph* vertex))


;;
;;Information getters
;;
(defn to-map
  "Returns a persistent map representing the vertex."
  [vertex]
  (*pre-fn*)
  (->> (keys vertex)
       (map #(vector (keyword %) (get vertex %)))
       (into { *element-id-key* (id-of vertex)})))

;;Finders
(defn find-by-id
  "Retrieves nodes by id from the graph."
  [& ids]
  (*pre-fn*)
  (if (= 1 (count ids))
    (.getVertex ^Graph *graph* (first ids))
    (seq (for [id ids] (.getVertex ^Graph *graph* id)))))

(defn find-by-kv
  "Given a key and a value, returns the set of all vertices that
   sastify the pair."
  [k v]
  (*pre-fn*)
  (set (.getVertices ^Graph *graph* (name k) v)))

(defn get-all-vertices
  "Returns all vertices."
  []
  (*pre-fn*)
  (set (.getVertices ^Graph *graph*)))

(defn edges-of
  "Returns edges that this vertex is part of with direction and with given labels"
  [^Vertex v direction & labels]
  (*pre-fn*)  
  (.getEdges v (to-edge-direction direction) (keywords-to-str-array labels)))

(defn all-edges-of
  "Returns edges that this vertex is part of, with given labels"
  [^Vertex v & labels]
  (*pre-fn*)
  (.getEdges v Direction/BOTH (keywords-to-str-array labels)))

(defn outgoing-edges-of
  "Returns outgoing (outbound) edges that this vertex is part of, with given labels"
  [^Vertex v & labels]
  (*pre-fn*)  
  (.getEdges v Direction/OUT (keywords-to-str-array labels)))

(defn incoming-edges-of
  "Returns incoming (inbound) edges that this vertex is part of, with given labels"
  [^Vertex v & labels]
  (*pre-fn*)  
  (.getEdges v Direction/IN (keywords-to-str-array labels)))

(defn connected-vertices-of
  "Returns vertices connected to this vertex with a certain direction by the given labels"
  [^Vertex v direction & labels]
  (*pre-fn*)  
  (.getVertices v (to-edge-direction direction) (keywords-to-str-array labels)))

(defn connected-out-vertices
  "Returns vertices connected to this vertex by an outbound edge with the given labels"
  [^Vertex v & labels]
  (*pre-fn*)  
  (.getVertices v Direction/OUT (keywords-to-str-array labels)))

(defn connected-in-vertices
  "Returns vertices connected to this vertex by an inbound edge with the given labels"  
  [^Vertex v & labels]
  (*pre-fn*)  
  (.getVertices v Direction/IN (keywords-to-str-array labels)))

(defn all-connected-vertices
  "Returns vertices connected to this vertex with the given labels"  
  [^Vertex v & labels]
  (*pre-fn*)  
  (.getVertices v Direction/BOTH (keywords-to-str-array labels)))

;;
;; Creation methods
;;

(defn create!  
  "Create a vertex, optionally with the given property map."
  ([]
     (create! {}))
  ([data]
     (*pre-fn*)
     (let [^Vertex new-vertex (.addVertex ^Graph *graph* nil)]
       (merge! new-vertex data))))

(defn create-with-id!  
  "Create a vertex, optionally with the given property map."
  ([id]
     (create-with-id! id {}))
  ([id data]
     (*pre-fn*)
     (let [^Vertex new-vertex (.addVertex ^Graph *graph* id)]
       (merge! new-vertex data))))

(defn upsert!
  "Given a key and a property map, upsert! either creates a new node
   with that property map or updates all nodes with the given key
   value pair to have the new properties specifiied by the map. Always
   returns the set of vertices that were just update or created."
  [k m]
  ;;N.B. find-by-kv calls *pre-fn*
  (let [vertices (find-by-kv (name k) (k m))]
    (if (empty? vertices)
      (set [(create! m)]) 
      (do
        (doseq [vertex vertices] (merge! vertex m))
        vertices))))

(defn unique-upsert!
  "Like upsert!, but throws an error when more than one element is returned."
  [& args]
  (let [upserted (apply upsert! args)]
    (if (= 1 (count upserted))
      (first upserted)
      (throw (Throwable.
              (str
               "Don't call unique-upsert! when there is more than one element returned.\n"
               "There were " (count upserted) " vertices returned.\n"
               "The arguments were: " args "\n"))))))

(defn upsert-with-id!
  "Given a key and a property map, upsert! either creates a new node
   with that property map or updates all nodes with the given key
   value pair to have the new properties specifiied by the map. Always
   returns the set of vertices that were just update or created."
  [id k m]
  (let [vertices (find-by-kv (name k) (k m))]
    (if (empty? vertices)
      (set [(create-with-id! id m)]) 
      (do
        (doseq [vertex vertices] (merge! vertex m))
        vertices))))

(defn unique-upsert-with-id!
  "Like upsert!, but throws an error when more than one element is returned."
  [& args]
  (let [upserted (apply upsert-with-id! args)]
    (if (= 1 (count upserted))
      (first upserted)
      (throw (Throwable.
              (str
               "Don't call unique-upsert! when there is more than one element returned.\n"
               "There were " (count upserted) " vertices returned.\n"
               "The arguments were: " args "\n"))))))
