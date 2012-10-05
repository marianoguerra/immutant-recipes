(ns test-messaging.core
  (:use [clojure.data.json :only (read-json json-str)])
  (:require [immutant.messaging :as messaging]))

(def ^:dynamic *messaging-publish*
  "module to use for messaging"
  messaging/publish)

(def new-event-topic "/topic/message/created")

(defn publish [topic data]
  (*messaging-publish* topic data))

(defn new-event [request]
  (let [body (:body request)
        body-data (slurp body)
        data (read-json body-data)]

    (publish new-event-topic data)

    {:status 201
      :headers {"Content-Type" "application/json"}
      :body body-data}))

