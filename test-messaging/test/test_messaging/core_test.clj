(ns test-messaging.core-test
  (:use clojure.test
        test-messaging.core)
  (:require
    [ring.mock.request :as req]
    [test-messaging.messaging :as msgapi]))

(deftest send-new-message-sends-it-to-the-message-bus
 (with-redefs [*messaging-publish* msgapi/publish]
             (new-event (req/request :post "/api/message" "42"))
             (is (= (:data (first @msgapi/messages)) 42))))
