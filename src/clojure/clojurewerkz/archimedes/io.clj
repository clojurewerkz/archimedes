(ns clojurewerkz.archimedes.io
  (:require [clojure.java.io :as io]
            [clojurewerkz.archimedes.graph :as g])
  (:import [org.apache.tinkerpop.gremlin.structure.io.graphml GraphMLWriter GraphMLReader]
           [org.apache.tinkerpop.gremlin.structure.io.graphson GraphSONWriter GraphSONReader]))

(defn- load-graph-with-reader
  [reader g string-or-file]
  (let [in-stream (io/input-stream string-or-file)]
    (reader g in-stream)))

(defn- write-graph-with-writer
  [writer g string-or-file]
  (let [out-stream (io/output-stream string-or-file)]
    (writer g out-stream)))

;; GML

;; TODO check whether this is still supported
;;(def load-graph-gml (partial load-graph-with-reader #(GMLReader/inputGraph %1 %2)))
;;(def write-graph-gml (partial write-graph-with-writer #(GMLWriter/outputGraph %1 %2)))

;; GraphML
(def load-graph-graphml (partial load-graph-with-reader #(-> (GraphMLReader/build) (.create) (.readGraph %2 %1))))
(def write-graph-graphml (partial write-graph-with-writer #(-> (GraphMLWriter/build) (.create) (.writeGraph %2 %1))))

;; GraphSON
(def load-graph-graphson (partial load-graph-with-reader #(-> (GraphSONReader/build) (.create) (.readGraph %2 %1))))

;; write-graph-graphson can take an optional 2nd argument:
;; show-types - determines if types are written explicitly to the JSON
;; Note that for Titan Graphs with types, you will want show-types=true.
;; See https://github.com/tinkerpop/blueprints/wiki/GraphSON-Reader-and-Writer-Library
(defn write-graph-graphson
  [g string-or-file & [ show-types ]]
  (write-graph-with-writer
   #(-> (GraphSONWriter/build) (.create) (.writeGraph %2 %1))
   g
   string-or-file))
