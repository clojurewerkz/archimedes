(ns archimedes.element
  (:refer-clojure :exclude [keys vals assoc! dissoc! get])
  (:import (com.tinkerpop.blueprints Element)))


(defn get
  ([elem key] (get elem key nil))
  ([elem key not-found]
     (let [value (.getProperty elem (name key))]
       (or value not-found))))

(defn keys [elem]
  (set (map keyword (.getPropertyKeys elem))))

(defn vals [elem]
  (set (map #(.getProperty elem %) (.getPropertyKeys elem))))

(defn id-of [elem]
  (.getId elem))

(defn assoc! [elem & kvs]  
  ;;Avoids changing keys that shouldn't be changed.
  ;;Important when using types. You aren't ever going to change a
  ;;user's id for example.
  (doseq [[key value] (partition 2 kvs)]
    (when (not= value (get elem (name key)))
      (.removeProperty elem (name key)) ;;Hacky work around! Yuck!
      (.setProperty elem (name key) value)))
  elem)

(defn merge!
  [elem & maps]
  (doseq [d maps]
    (apply assoc! (cons elem (flatten (into [] d)))))
  elem)

(defn dissoc! [elem & keys]
  (doseq [key keys] (.removeProperty elem (name key)))
  elem)

(defn update!
  [elem key f & args]
  (let [curr-val (get elem key)
        new-val  (apply f (cons curr-val args))]
    (assoc! elem key new-val)))

(defn clear!
  ([elem]
     (apply dissoc! (cons elem (keys)))))
