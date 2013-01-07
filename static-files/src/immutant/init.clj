(ns immutant.init
  (:require
    [ring.middleware.file :as ring-file]
    [immutant.web :as web]))

(defn text-response [body & [status]]
  {:status (or status 200)
   :header {"Content-Type" "text/plain"}
   :body body})

(defn handler [request]
  (let [path (:path-info request)]
    (if (= path "/api/ping")
      (text-response "pong")
      (text-response "not found" 404))))

(web/start "/" (web/wrap-resource handler "/s/"))
