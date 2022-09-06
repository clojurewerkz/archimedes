(defproject ardoq/archimedes "3.0.0.1"
  :description "Clojure wrapper for Tinkerpop Blueprints"
  :url "https://github.com/clojurewerkz/archimedes"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [potemkin "0.2.0"]
                 [org.apache.tinkerpop/gremlin-core "3.1.1-incubating" :exclusions [org.slf4j/slf4j-api]]]
  :source-paths ["src/clojure"]
  :profiles {:dev    {:dependencies [[gorillalabs/titan-core  "1.1.0g" :exclusions [org.slf4j/slf4j-api]]
                                     [gorillalabs/titan-berkeleyje "1.1.0g" :exclusions [org.slf4j/slf4j-api]]
                                     [org.slf4j/slf4j-nop "1.7.5"]
                                     [clojurewerkz/support "1.0.0" :exclusions [com.google.guava/guava
                                                                                org.clojure/clojure]]
                                     [commons-io/commons-io "2.4"]]}
             :1.5    {:dependencies [[org.clojure/clojure "1.5.1"]]}
             :1.7    {:dependencies [[org.clojure/clojure "1.7.0"]]}
             :master {:dependencies [[org.clojure/clojure "1.8.0-master-SNAPSHOT"]]}}
  :aliases {"all" ["with-profile" "dev:dev,1.5:dev,1.7:dev,master"]}
  :repositories {"sonatype" {:url "http://oss.sonatype.org/content/repositories/releases"
                             :snapshots false
                             :releases {:checksum :fail :update :always}}
                 "sonatype-snapshots" {:url "http://oss.sonatype.org/content/repositories/snapshots"
                                       :snapshots true
                                       :releases {:checksum :fail :update :always}}}
  :global-vars {*warn-on-reflection* true})
