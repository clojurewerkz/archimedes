(ns archimedes.io-test
  (:use clojure.test)
  (:require [archimedes.core :as g]
            [archimedes.io :as io]
            [archimedes.type :as t]
            [archimedes.vertex :as v]
            [archimedes.edge :as e]
            [clojure.java.io :as clj-io]))

(defn has-n-vertices [n]
  (is (= n (count (seq (.getVertices g/*graph*))))))

(defn has-n-edges [n]
  (is (= n (count (seq (.getEdges g/*graph*))))))

(deftest test-loading-and-saving-graphs-graphml
  (g/use-clean-graph!)
  (let [filename "my-test-graph.graphml"
        file (clj-io/file filename)]
    (letfn [(delete-graph-file [] (clj-io/delete-file file true))] ;; Delete file *silently* (no failure if it don't exist).
      (delete-graph-file)
      (let [vertex-1 (v/create-with-id! 100)
            vertex-2 (v/create-with-id! 101)
            edge (e/connect-with-id! 102 vertex-1 :edge vertex-2)]
        (io/write-graph-graphml filename))

      ;; Open new graph and read it
      (g/use-clean-graph!)
      (io/load-graph-graphml filename)

      (has-n-vertices 2)
      (has-n-edges 1)

      (delete-graph-file))))

(deftest test-loading-and-saving-graphs-gml
  (g/use-clean-graph!)
  (let [filename "my-test-graph.gml"
        file (clj-io/file filename)]
    (letfn [(delete-graph-file [] (clj-io/delete-file file true))] ;; Delete file *silently* (no failure if it don't exist).
      (delete-graph-file)
      (let [vertex-1 (v/create-with-id! 100)
            vertex-2 (v/create-with-id! 101)
            edge (e/connect-with-id! 102 vertex-1 :edge vertex-2)]
        (io/write-graph-gml filename))

      ;; Open new graph and read it
      (g/use-clean-graph!)
      (io/load-graph-gml filename)

      (has-n-vertices 2)
      (has-n-edges 1)

      (delete-graph-file))))

(deftest test-loading-and-saving-graphs-graphson
  (testing "Without type information"
    (g/use-clean-graph!)
    (let [filename "my-test-graph.graphson"
          file (clj-io/file filename)]
      (letfn [(delete-graph-file [] (clj-io/delete-file file true))] ;; Delete file *silently* (no failure if it don't exist).
        (delete-graph-file)
        (let [vertex-1 (v/create-with-id! 100)
              vertex-2 (v/create-with-id! 101)
              edge (e/connect-with-id! 102 vertex-1 :edge vertex-2)]
          (io/write-graph-graphson filename))

        ;; Open new graph and read it
        (g/use-clean-graph!)
        (io/load-graph-graphson filename)

        (has-n-vertices 2)
        (has-n-edges 1)

        (delete-graph-file))))

  (testing "With a graph with type information"
    (letfn [(init-graph-with-types []
              (g/use-clean-graph!)
              (t/create-vertex-key :my-int Integer)
              (t/create-vertex-key :my-long Long)
              (t/create-vertex-key :my-float Float)
              (t/create-vertex-key :my-double Double)
              (t/create-vertex-key :my-boolean Boolean))]
      (let [filename-typed "my-test-graph-typed.graphson"
            filename-untyped "my-test-graph-untyped.graphson"
            file-typed (clj-io/file filename-typed)
            file-untyped (clj-io/file filename-untyped)]
        (letfn [(delete-graph-files []
                  (clj-io/delete-file file-typed true)
                  (clj-io/delete-file file-untyped true))] ;; Delete files *silently* (no failure if it don't exist).

          (delete-graph-files)
          (init-graph-with-types)

          (let [vertex-1 (v/create-with-id! 100 {:my-int (int 1)
                                                 :my-long (long 2)
                                                 :my-float (float 3)
                                                 :my-double (double 4)
                                                 :my-boolean true})
                vertex-2 (v/create-with-id! 101 {:my-int (int 10)
                                     :my-long (long 20)
                                     :my-float (float 30)
                                     :my-double (double 40)
                                     :my-boolean false})
                edge (e/connect-with-id! 102 vertex-1 :edge vertex-2)]

                                        ; Write one with type info
            (io/write-graph-graphson filename-typed true)

                                        ; Write one without type info
            (io/write-graph-graphson filename-untyped false))

          (testing "Loading a graphson without type infomation"
            (init-graph-with-types)
            (is (thrown? java.lang.IllegalArgumentException
                         (io/load-graph-graphson filename-untyped)) "Causes type errors to be thrown"))

          (testing "Loading a graphson with type infomation"
            (init-graph-with-types)
            (io/load-graph-graphson filename-typed)

            (has-n-vertices 2)
            (has-n-edges 1))

          (delete-graph-files))))))
