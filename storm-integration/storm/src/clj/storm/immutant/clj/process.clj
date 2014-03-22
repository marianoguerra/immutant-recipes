(ns storm.immutant.clj.process
  (:require [cheshire.core :as json]
            [clojure.tools.logging :as log])
  (:import [storm.immutant.java HornetQJmsProvider HornetQJmsConnectionType
            EventTupleProducer]
           [backtype.storm StormSubmitter LocalCluster Constants Config]
           [backtype.storm.contrib.jms JmsMessageProducer JmsProvider
            JmsTupleProducer]
           [javax.jms Session TextMessage Message]
           [backtype.storm.contrib.jms.bolt JmsBolt]
           [backtype.storm.contrib.jms.spout JmsSpout]
           [backtype.storm.tuple Tuple])
  (:use [backtype.storm clojure config])
  (:gen-class))

(defn tick-tuple? [tuple]
  (log/info "Tuple" (.getSourceComponent tuple) (.getSourceStreamId tuple))
  (and (= (.getSourceComponent tuple) Constants/SYSTEM_COMPONENT_ID)
       (= (.getSourceStreamId tuple) Constants/SYSTEM_TICK_STREAM_ID)))

; Consume tuples from a HornetQ JMS Queue
(def proc-in-jms-provider (HornetQJmsProvider. 5445 "process-input"
                                            HornetQJmsConnectionType/Queue))
; Parse the input and generate the Tuples using EventTupleProducer
(def proc-tuple-producer (EventTupleProducer.))

; Create the JMS sprout
(def from-jms-sprout  (JmsSpout.))

; Configure it
(doto from-jms-sprout
 (.setJmsProvider proc-in-jms-provider)
 (.setJmsTupleProducer proc-tuple-producer)
 (.setJmsAcknowledgeMode javax.jms.Session/CLIENT_ACKNOWLEDGE)
 (.setDistributed true))

; Send tuples to a HornetQ JMS Topic
(def proc-out-jms-provider (HornetQJmsProvider. 5445 "process-output"
                                            HornetQJmsConnectionType/Topic))
; Create the JMS bolt
(def to-jms-bolt (JmsBolt.))

; Produce the JMS message to send to the JMS output Topic from a tuple
(def jms-message-producer
  (reify JmsMessageProducer
    (^Message toMessage [this ^Session session ^Tuple input]
      (let [result (into {} input)
            result-json (json/generate-string result)
            message (.createTextMessage session result-json)]
        message))))

; Configure the JMS bolt
(doto to-jms-bolt
 (.setJmsProvider proc-out-jms-provider)
 (.setJmsMessageProducer jms-message-producer))

(defn now []
  (.getTime (java.util.Date.)))

(defspout tick-spout ["time"] {:params [ticker-sleep-ms] :prepare false}
  [collector]
  (Thread/sleep ticker-sleep-ms)
  (emit-spout! collector [(now)]))

(defn handle-tick [state tuple]
  (log/info "tick!"))

(defn handle-event [collector state tuple]
  (let [username (.getString tuple 0)
        channel (.getString tuple 1)
        msg (.getString tuple 2)
        timestamp (.getLong tuple 3)

        ; do the processing here
        new-tuple [username channel msg timestamp]]

    (emit-bolt! collector new-tuple :anchor tuple)
    (ack! collector tuple)))

; Define the bolt that will do the work
(defbolt process ["username" "channel" "msg" "timestamp"] {:prepare true}
  [conf context collector]
  (let [state (atom {})]
    (bolt
      (execute [tuple]
               (if (tick-tuple? tuple)
                 (handle-tick state tuple)
                 (handle-event collector state tuple))))))

; Define the bolt that will do the work when a tick happens
(defbolt do-periodically ["time"] [tuple collector]
  (log/info "do-periodically got tuple" tuple)
  (emit-bolt! collector [(now)]))

; Create the topology
(defn mk-topology []
  (let [process-config (Config.)
        process-bolt-spec (bolt-spec {"from-jms" ["username" "channel"]} process :p 5 :conf {TOPOLOGY-TICK-TUPLE-FREQ-SECS 1})]

    (topology
      {"from-jms" (spout-spec from-jms-sprout :p 5)
       "ticker" (spout-spec (tick-spout 5000) :p 1)}
      {"process" process-bolt-spec
       "do-periodically" (bolt-spec {"ticker" :all} do-periodically :p 1)
       "to-jms" (bolt-spec {"do-periodically" :shuffle} to-jms-bolt :p 5)})))

(defn run-local! []
  (let [cluster (LocalCluster.)]
    (.submitTopology cluster "process-handler" {TOPOLOGY-DEBUG true}
                     (mk-topology))
    @(promise)
    (.shutdown cluster)))

(defn submit-topology! [name]
  (StormSubmitter/submitTopology
   name
   {TOPOLOGY-DEBUG true
    TOPOLOGY-WORKERS 3
    TOPOLOGY-TICK-TUPLE-FREQ-SECS 1}
   (mk-topology)))

(defn -main
  ([]
   (run-local!))
  ([name]
   (submit-topology! name)))
