(defproject fitness-joy "0.1.0-SNAPSHOT"
  :description "Whoop+ - live fitness coaching system"
  :dependencies [[org.clojure/clojure "1.11.2"]
                 [clj-http "3.12.3"]
                 [cheshire "5.11.0"]
                 [metosin/malli "0.16.1"]
                 [org.clojure/core.async "1.6.681"]
                 [criterium "0.4.6"]]
  :main ^:skip-aot fitness-joy.core
  :target-path "target/%s"
  :profiles {:dev {:dependencies [[midje "1.10.6"]]
                   :plugins [[lein-midje "3.2.2"]]}
             :uberjar {:aot :all}})