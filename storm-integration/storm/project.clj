(defproject storm-immutant "0.0.1"
  :source-paths ["src/clj"]
  :java-source-paths ["src/java"]
  :aot :all
  :main storm.immutant.clj.process
  :dependencies [[storm "0.9.0.1"]
                 [cheshire "5.3.1"]
                 [org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.logging "0.2.6"]
                 [com.github.ptgoetz/storm-jms "0.9.0"]
                 [org.hornetq/hornetq-jms-client "2.3.1.Final"]
                 [com.fasterxml.jackson.core/jackson-databind "2.3.2"]]

  :profiles {:dev {:dependencies []}}
  :min-lein-version "2.0.0")
