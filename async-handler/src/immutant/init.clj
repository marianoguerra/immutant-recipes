(ns immutant.init
  (:require
    [async.core :as async]
    [immutant.web :as web])) 

(defn async-handler [request on-complete]
  (Thread/sleep 5000)
  (on-complete {:status 200 :body "finished"}))

(web/start "/" (async/handle-async async-handler))
