(ns hermes.io-test
  (:use clojure.test)
  (:require [hermes.core :as g]
            [hermes.io :as io]
            [hermes.type :as t]
            [hermes.vertex :as v]
            [hermes.edge :as e]
            [clojure.java.io :as clj-io]))

(defn has-n-vertices [n]
  (is (= n (count (seq (.getVertices g/*graph*))))))

(defn has-n-edges [n]
  (is (= n (count (seq (.getEdges g/*graph*))))))

;;These tests will come back in 0.3.0 when we get access to g.V again. 
;; (deftest io-test
;;   (testing "Loading and saving graphs graphml"
;;   (g/open)
;;   (let [filename "my-test-graph.graphml"
;;         file (clj-io/file filename)]
;;     (letfn [(delete-graph-file [] (clj-io/delete-file file true))] ;; Delete file *silently* (no failure if it don't exist).
;;       (delete-graph-file)
;;       (let [vertex-1 (v/create!)
;;             vertex-2 (v/create!)
;;             edge (e/upconnect! vertex-1 vertex-2 "edge")]
;;         (io/write-graph-graphml filename))

;;       ;; Open new graph and read it
;;       (g/open)
;;       (io/load-graph-graphml filename)

;;       (has-n-vertices 2)
;;       (has-n-edges 1)

;;       (delete-graph-file))))
;;   )


;; (deftest test-loading-and-saving-graphs-gml
;;   (g/open)
;;   (let [filename "my-test-graph.gml"
;;         file (clj-io/file filename)]
;;     (letfn [(delete-graph-file [] (clj-io/delete-file file true))] ;; Delete file *silently* (no failure if it don't exist).
;;       (delete-graph-file)
;;       (let [vertex-1 (v/create!)
;;             vertex-2 (v/create!)
;;             edge (e/upconnect! vertex-1 vertex-2 "edge")]
;;         (io/write-graph-gml filename))

;;       ;; Open new graph and read it
;;       (g/open)
;;       (io/load-graph-gml filename)

;;       (has-n-vertices 2)
;;       (has-n-edges 1)

;;       (delete-graph-file))))

;; (deftest test-loading-and-saving-graphs-graphson
;;   (testing "Without type information"
;;     (g/open)
;;     (let [filename "my-test-graph.graphson"
;;           file (clj-io/file filename)]
;;       (letfn [(delete-graph-file [] (clj-io/delete-file file true))] ;; Delete file *silently* (no failure if it don't exist).
;;         (delete-graph-file)
;;         (let [vertex-1 (v/create!)
;;               vertex-2 (v/create!)
;;               edge (e/upconnect! vertex-1 vertex-2 "edge")]
;;           (io/write-graph-graphson filename))

;;         ;; Open new graph and read it
;;         (g/open)
;;         (io/load-graph-graphson filename)

;;         (has-n-vertices 2)
;;         (has-n-edges 1)

;;         (delete-graph-file))))

;;   (testing "With a graph with type information"
;;     (letfn [(init-graph-with-types []
;;               (g/open)
;;               (t/create-vertex-key :my-int Integer)
;;               (t/create-vertex-key :my-long Long)
;;               (t/create-vertex-key :my-float Float)
;;               (t/create-vertex-key :my-double Double)
;;               (t/create-vertex-key :my-boolean Boolean))]
;;       (let [filename-typed "my-test-graph-typed.graphson"
;;             filename-untyped "my-test-graph-untyped.graphson"
;;             file-typed (clj-io/file filename-typed)
;;             file-untyped (clj-io/file filename-untyped)]
;;         (letfn [(delete-graph-files []
;;                   (clj-io/delete-file file-typed true)
;;                   (clj-io/delete-file file-untyped true))] ;; Delete files *silently* (no failure if it don't exist).

;;           (delete-graph-files)
;;           (init-graph-with-types)

;;           (let [vertex-1 (v/create! {:my-int (int 1)
;;                                      :my-long (long 2)
;;                                      :my-float (float 3)
;;                                      :my-double (double 4)
;;                                      :my-boolean true})
;;                 vertex-2 (v/create! {:my-int (int 10)
;;                                      :my-long (long 20)
;;                                      :my-float (float 30)
;;                                      :my-double (double 40)
;;                                      :my-boolean false})
;;                 edge (e/upconnect! vertex-1 vertex-2 "edge")]

;;             ; Write one with type info
;;             (io/write-graph-graphson filename-typed true)

;;             ; Write one without type info
;;             (io/write-graph-graphson filename-untyped false))

;;           (testing "Loading a graphson without type infomation"
;;             (init-graph-with-types)
;;             (is (thrown? java.lang.IllegalArgumentException
;;               (io/load-graph-graphson filename-untyped)) "Causes type errors to be thrown"))

;;           (testing "Loading a graphson with type infomation"
;;             (init-graph-with-types)
;;             (io/load-graph-graphson filename-typed)

;;             (has-n-vertices 2)
;;             (has-n-edges 1))

;;           (delete-graph-files))))))
