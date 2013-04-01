(ns archimedes.core
  (:import (com.tinkerpop.blueprints Element Graph TransactionalGraph
                                     TransactionalGraph$Conclusion)
           (com.tinkerpop.blueprints.impls.tg TinkerGraphFactory)))

(def ^{:dynamic true} *graph*)
(def ^{:dynamic true} *pre-fn* (fn []))

(def ^{:dynamic true} *enable-historical-reenactment-mode* false)

(defn flip-reenactment-bit! []
  (alter-var-root (var *enable-historical-reenactment-mode*) (fn [t] (not t))))

(defn get-graph []
  *graph*)

(defn set-pre-fn!
  [f]
  (alter-var-root (var *pre-fn*) (constantly f)))



(defn set-graph!
  [g]
  (when *enable-historical-reenactment-mode* 
    (println "EUREKA!"))
  (alter-var-root (var *graph*) (constantly g)))

(defn use-new-tinkergraph!
  []
  (set-graph! (TinkerGraphFactory/createTinkerGraph)))

(defn use-clean-graph!
  []
  (use-new-tinkergraph!)
  (doseq [e (seq (.getEdges ^Graph *graph*))]
    (.removeEdge ^Graph *graph* e))
  (doseq [v (seq (.getVertices ^Graph *graph*))]
    (.removeVertex ^Graph *graph* v))
  nil)

(defn shutdown 
  "Shutdown the graph."
  [] (alter-var-root (var *graph*) (fn [^Graph m] (when m (.shutdown m)))))

(defn get-features
  "Get a map of features for a graph.
  (http://tinkerpop.com/docs/javadocs/blueprints/2.1.0/com/tinkerpop/blueprints/Features.html)"
  []
  (-> ^Graph *graph* .getFeatures .toMap))

(defn get-feature
  "Gets the value of the feature for a graph."
  [s]
  (get ^Map (get-features) s))

(defn- transact!*
  [f]
  (if (get-feature "supportsTransactions")
    (try
      (let [tx (.newTransaction ^TransactionalGraph *graph*)
            results                (binding [*graph* tx] (f))]
        (.commit ^TransactionalGraph tx)
        (.stopTransaction ^TransactionalGraph *graph* TransactionalGraph$Conclusion/SUCCESS)
        results)
      (catch Exception e
        (.stopTransaction ^TransactionalGraph *graph* TransactionalGraph$Conclusion/FAILURE)
        (throw e)))
    ;; Transactions not supported.
    (f)))

(defmacro transact!
  [& forms]
  "Perform graph operations inside a transaction."
  `(~transact!* (fn [] ~@forms)))

(defn- retry-transact!*
  [max-retries wait-time-fn try-count f]
  (let [res (try {:value (transact!* f)}
                 (catch Exception e
                   {:exception e}))]
    (if-not (:exception res)
      (:value res)
      (if (> try-count max-retries)
        (throw (:exception res))
        (let [wait-time (wait-time-fn try-count)]
          (Thread/sleep wait-time)
          (recur max-retries wait-time-fn (inc try-count) f))))))

(defmacro retry-transact!
  [max-retries wait-time & forms]
  "Perform graph operations inside a transaction.  The transaction will retry up
  to `max-retries` times.  `wait-time` can be an integer corresponding to the
  number of milliseconds to wait before each try, or it can be a function that
  takes the retry number (starting with 1) and returns the number of
  milliseconds to wait before that retry."
  `(let [wait-time-fn# (if (ifn? ~wait-time)
                         ~wait-time
                         (constantly ~wait-time))]
     (~retry-transact!* ~max-retries wait-time-fn# 1 (fn [] ~@forms))))

(defmacro with-graph
  "Perform graph operations against a specific graph."
  [g & forms]
  `(binding [*graph* ~g]
     ~@forms))
