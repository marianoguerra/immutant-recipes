(ns immutant.init
  (:require
    [async.core :as async]
    [immutant.web :as web])) 

(defn sync-handler [request]
  (Thread/sleep 5000)
  {:status 200 :body "finished"})

(defn async-handler [request on-complete]
  (Thread/sleep 5000)
  (on-complete {:status 200 :body "finished"}))

(web/start "/async" (async/handle-async async-handler))
(web/start "/sync" sync-handler)
