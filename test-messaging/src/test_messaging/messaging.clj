(ns test-messaging.messaging)

(def messages (atom []))

(defn publish [topic data]
  (swap! messages conj {:topic topic :data data}))
