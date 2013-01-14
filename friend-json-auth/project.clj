(defproject friend-json-auth "0.1.0-SNAPSHOT"
  :description "how to use friend to enable role access control with json login"
  :url "https://github.com/marianoguerra/immutant-recipes/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [ring/ring-core "1.1.7"]
                 [ring-router "0.2-SNAPSHOT"]
                 [org.clojure/data.json "0.1.3"]
                 [org.marianoguerra/friend-json-workflow "0.1.0"]
                 [com.cemerick/friend "0.1.3"]
                 [org.clojure/clojure "1.4.0"]])
