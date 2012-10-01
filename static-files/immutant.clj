(ns static-files.init
  (:require
    [ring.middleware.file :as ring-file]
    [immutant.web :as web]))

(defn not-found [request]
  {:status 404
   :header {"Content-Type" "text/plain"}
   :body "file not found"})

(web/start "s/" (ring-file/wrap-file not-found "public/s/"))
(web/start "api/ping"
           (fn [request]
             {:status 200
              :header {"Content-Type" "application/json"}
              :body "\"pong\""}))
