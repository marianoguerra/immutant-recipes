(ns async.core
  (:require
    [ring.util.servlet :as servlet]
    [immutant.web.internal :as web-internal])) 

(defn handle-async [handler]
  "return a ring handler that will call handler with request and a on-complete
  function that when called with a response map will resolve the request"

  (fn [request]
    (let [async-ctx (.startAsync web-internal/current-servlet-request)
          response (.getResponse async-ctx)
          on-complete (fn [response-map]
                        (servlet/update-servlet-response response response-map)
                        (.complete async-ctx))
          async-fun (fn [] (handler request on-complete))]

      (.start async-ctx async-fun)
      {})))

