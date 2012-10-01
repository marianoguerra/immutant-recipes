(ns ring-router-example.init
  (:use
    ring-router.core
    [clojure.data.json :only (read-json json-str)])
  (:require [immutant.web :as web]))

(defn json-response [data & [status]]
    {:status (or status 200)
        :headers {"Content-Type" "application/json"}
        :body (json-str data)})

(defn ping [request]
  (json-response "pong"))

(defn hello-name [request]
  (let [name (get-in request [:route-params :name])]
    (json-response (clojure.string/join ["hello " name "!"]))))

(defn echo-json [request]
    (if (= (:content-type request) "application/json")

      (let [json (read-json (slurp (:body request)))]
        (json-response json))

      (json-response {:error "invalid content type"} 400)))

(defn not-found [request]
  (json-response {:error :not-found :reason "not found"} 404))

(web/start "/"
  (router
    [(GET "/ping" ping)
     (GET "/hello/:name" hello-name)
     (POST "/echo" echo-json)
     (GET "*" not-found)]))

