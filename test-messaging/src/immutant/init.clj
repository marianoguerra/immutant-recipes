(ns immutant.init
  (:use ring-router.core)
  (:require
    [test-messaging.core :as core]
    [immutant.messaging :as messaging]
    [immutant.web :as web]))

(web/start "/"
  (router
    [(POST "/api/message" core/new-event)]))

(messaging/start core/new-event-topic)

