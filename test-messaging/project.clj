(defproject test-messaging "0.1.0-SNAPSHOT"
  :description "example to show how to mock the messaging api to test"
  :url "https://github.com/marianoguerra/immutant-recipes/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [org.clojure/data.json "0.1.3"]
                 [ring/ring-core "1.1.6"]
                 [ring-mock "0.1.3"]
                 [org.immutant/immutant-messaging "0.4.0"]
                 [org.clojure/clojure "1.4.0"]])
