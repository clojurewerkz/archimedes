(ns archimedes.element
  (:refer-clojure :exclude [keys vals assoc! dissoc! get])
  (:import (com.tinkerpop.blueprints Element)))


(defn get
  ([elem keys] (get elem keys nil))
  ([elem keys not-found]
     (let [value (.getProperty elem (name (first keys)))]
       (if value
         value
         not-found))))

(defn keys [elem]
  (map keyword (.getPropertyKeys elem)))

(defn vals [elem]
  (map #(.getProperty elem %) (.getPropertyKeys elem)))

(defn id-of [elem]
  (.getId elem))

(defn assoc! [elem & kvs]  
  ;;Avoids changing keys that shouldn't be changed.
  ;;Important when using types. You aren't ever going to change a
  ;;user's id for example.
  (map (fn [[value key]]
         (when (not= value (get elem (name key)))
           (.removeProperty elem (name key)) ;;Hacky work around! Yuck!
           (.setProperty elem (name key) value)))
       (partition 2 kvs))
  elem)

(defn merge!
  [elem & maps]
  (doseq [d maps]
    (apply assoc! (cons elem (vec d))))
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
