(ns archimedes.vertex
  (:refer-clojure :exclude [keys vals assoc! dissoc! get])
  (:import (com.tinkerpop.blueprints Vertex)
           (com.tinkerpop.blueprints.impls.tg TinkerGraph))
  (:require [archimedes.core :refer (*graph* *pre-fn*)]
            [archimedes.util :refer (immigrate)]))

(immigrate 'archimedes.element)

;;
;; Transaction management
;;

(defn refresh
  "Gets a vertex back from the database and refreshes it to be usable again."
  [vertex]
  (*pre-fn*)
  (.getVertex *graph* vertex))

;;
;; Deletion methods
;;

(defn delete!  
  "Delete a vertex."
  [vertex]
  (*pre-fn*)
  (.removeVertex *graph* vertex))


;;
;;Information getters
;;
(defn to-map
  "Returns a persistent map representing the vertex."
  [vertex]
  (*pre-fn*)
  (->> (get vertex)
       (map #(vector (keyword %) (get vertex %)))
       (into {:__id__ (id-of vertex)})))

;;Finders
(defn find-by-id
  "Retrieves nodes by id from the graph."
  [& ids]
  (*pre-fn*)
  (if (= 1 (count ids))
    (.getVertex *graph* (first ids))
    (seq (for [id ids] (.getVertex *graph* id)))))

(defn find-by-kv
  "Given a key and a value, returns the set of all vertices that
   sastify the pair."
  [k v]
  (*pre-fn*)
  (set (.getVertices *graph* (name k) v)))


;;
;; Creation methods
;;

(defn create!  
  "Create a vertex, optionally with the given property map."
  ([] (create! {}))
  ([data]
     (*pre-fn*)
     (let [new-vertex (.addVertex *graph*)]
       (merge! new-vertex data))))

(defn create-with-id!  
  "Create a vertex, optionally with the given property map."
  ([id] (create-with-id! id {}))
  ([id data]
     (*pre-fn*)
     (let [new-vertex (.addVertex *graph* id)]
       (merge! new-vertex data))))

;;TODO add upsert-with-id
(defn upsert!
  "Given a key and a property map, upsert! either creates a new node
   with that property map or updates all nodes with the given key
   value pair to have the new properties specifiied by the map. Always
   returns the set of vertices that were just update or created."
  [k m]
  (*pre-fn*);;TODO these calls could probably be removed
  (let [vertices (find-by-kv (name k) (k m))]
    (if (empty? vertices)
      (set [(create! nil m)]) 
      (do
        (doseq [vertex vertices] (merge! vertex m))
        vertices))))

(defn unique-upsert!
  "Like upsert!, but throws an error when more than one element is returned."
  [& args]
  (*pre-fn*);;TODO these calls could probably be removed
  (let [upserted (apply upsert! args)]
    (if (= 1 (count upserted))
      (first upserted)
      (throw (Throwable.
              (str
               "Don't call unique-upsert! when there is more than one element returned.\n"
               "There were " (count upserted) " vertices returned.\n"
               "The arguments were: " args "\n"))))))