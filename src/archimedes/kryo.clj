(ns hermes.kryo
  "Experimental support for using clojure data structures as properties in Titan."
  )


(defn prepare [val]
  "Prepares common clojure data structure for storage in Titan by casting them to the corresponding java structures."
  (condp = (type val)
    clojure.lang.PersistentVector   (java.util.ArrayList.  (map prepare val))
    clojure.lang.PersistentList     (java.util.ArrayList.  (map prepare val))
    clojure.lang.PersistentHashSet  (java.util.HashSet. (map prepare val))
    
    clojure.lang.PersistentHashMap
    (java.util.HashMap. (into {} (for [[k v] val]
                                   [(name k) (prepare v)])))
    
    clojure.lang.PersistentArrayMap
    (java.util.HashMap. (into {} (for [[k v] val]
                                   [(name k) (prepare v)])))
    
    clojure.lang.Keyword            (throw (Throwable.
                                            "Clojure keywords are not supported as properties."))
    val))

(defn revert [val]
  "Reverts java data structures back to the corresponding clojure data structures."
  (condp = (type val)
    java.util.ArrayList (vec (map revert (seq val)))
    java.util.HashSet (set (map revert (seq val)))
    java.util.HashMap (into {} (for [[k v] val] [(keyword k) (revert v)]))
    val))
