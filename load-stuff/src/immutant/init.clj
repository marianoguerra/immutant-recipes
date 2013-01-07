(ns immutant.init
  (:require [immutant.messaging :as messaging]
            [immutant.registry :as registry]
            [immutant.web :as web]
            [immutant.util :as util]))

(defn text-response [body & [status]]
  {:status (or status 200)
   :header {"Content-Type" "text/plain"}
   :body body})

(defn handler [request]
  (let [path (:path-info request)]
    (case path
      "/resource-file" (text-response (slurp (clojure.java.io/resource "hello.txt")))
      "/immutant-config" (text-response (str (registry/get :config)))
      "/leiningen-config" (text-response (str (registry/get :project)))
      (text-response (str "not found: " path) 404))))

(web/start "/" handler)
