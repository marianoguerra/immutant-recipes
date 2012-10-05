(ns test-messaging.messaging)

(def messages (ref []))

(defn publish [topic data]
  (dosync (ref-set messages (conj @messages {:topic topic :data data}))))
