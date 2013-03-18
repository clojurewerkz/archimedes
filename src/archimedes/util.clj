(ns archimedes.util
  (:require [clojure.reflect :as r])
  (:use [clojure.pprint :only (pprint)]))

;;https://groups.google.com/forum/?fromgroups=#!topic/clojure/GAGF38uI1-o
(defn- merge-meta! 
  "Destructively merge metadata from a source object into a target." 
  [source target] 
  (.setMeta target 
    (merge (meta source) 
           (select-keys (meta target) [:name :ns])))) 

(defn immigrate 
  "Add all the public vars in a list of namespaces to the current 
namespace." 
  [& namespaces] 
  (doseq [ns namespaces] 
    (require ns) 
    (doseq [[sym v] (ns-publics (find-ns ns))] 
      (merge-meta! v 
        (if (.isBound v) 
          (intern *ns* sym (var-get v)) 
          (intern *ns* sym))))))

(defn keywords-to-str-array [strs]
  (into-array String (map name strs)))