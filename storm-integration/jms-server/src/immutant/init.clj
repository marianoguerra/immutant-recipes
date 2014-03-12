(ns immutant.init
  (:import de.svenjacobs.loremipsum.LoremIpsum)
  (:require [cheshire.core :as json]
            [clojure.tools.logging :as log]
            [immutant.messaging :as msg]
            [immutant.jobs :as jobs]))

(def lorem (LoremIpsum.))
(def in-queue (msg/as-queue "process-input"))
(def out-topic (msg/as-topic "process-output"))

(msg/start in-queue)
(msg/start out-topic)

(def usernames ["bob" "patrick" "sandy"])
(defn random-username []
  (rand-nth usernames))

(def channels ["clojure" "immutant" "storm"])
(defn random-channel []
  (rand-nth channels))

(defn random-message []
  (.getWords lorem (+ 2 (rand-int 20)) (rand-int 50)))


(defn send-events-to-jms []
  (let [username (random-username)
        msg (random-message)
        channel (random-channel)
        timestamp (.getTime (java.util.Date.))
        event {:username username
               :msg msg
               :channel channel
               :timestamp timestamp}]

    (log/info "Sending" event)
    (msg/publish in-queue event :encoding :json)))

(defn on-message-received [msg]
  (let [event (json/parse-string msg)]
    (log/info "Received" event)))

(msg/listen out-topic on-message-received)
(jobs/schedule :send-events-to-jms send-events-to-jms :every 5000)
