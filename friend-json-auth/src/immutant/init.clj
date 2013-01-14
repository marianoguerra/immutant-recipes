(ns immutant.init
  (:use
    [ring.util.response :only (redirect)]
    [cemerick.friend.util :only (gets)]
    ring-router.core
    [clojure.data.json :only (read-json json-str)])

  (:require [cemerick.friend :as friend]
            (cemerick.friend [workflows :as workflows]
                             [credentials :as creds])
            [marianoguerra.friend-json-workflow :as json-auth]
            [ring.middleware.file :as ring-file]
            [ring.middleware.file-info :as ring-file-info]
            [ring.middleware.session :as ring-session]
            [ring.util.response :as response]
            [immutant.web :as web]))

(def users {"root" {:username "root"
                    :password (creds/hash-bcrypt "admin_password")
                    :roles #{::admin}}
            "jane" {:username "jane"
                    :password (creds/hash-bcrypt "user_password")
                    :roles #{::user}}})

(defn not-found [request]
  {:status 404
   :header {"Content-Type" "text/plain"}
   :body "not found"})

(defn json-response [data & [status]]
    {:status (or status 200)
        :headers {"Content-Type" "application/json"}
        :body (json-str data)})

(defn ping [request]
  (json-response "pong"))

(defn or-not-found [handler]
  (fn [request]
    (let [response (handler request)]
      (if response
        response
        (not-found request)))))

(def app (or-not-found (web/wrap-resource (router
  [(GET "/api/ping" ping)
   (GET "/api/session" json-auth/handle-session)
   (POST "/api/session" json-auth/handle-session)
   (DELETE "/api/session" json-auth/handle-session)
   (GET "/api/login" (fn [request] (redirect "../login.html")))
   (GET "/api/user-only-ping" (friend/wrap-authorize ping [::user]))
   (GET "/api/admin-only-ping" (friend/wrap-authorize ping [::admin]))]) "/s/")))

(def secure-app
  (-> app
      (friend/authenticate
        {:login-uri "/friend-json-auth/api/session"
         :unauthorized-handler json-auth/unauthorized-handler
         :workflows [(json-auth/json-login
                       :login-uri "/friend-json-auth/api/session"
                       :login-failure-handler json-auth/login-failed
                       :credential-fn (partial creds/bcrypt-credential-fn users))]})
      (ring-session/wrap-session)))

(web/start "/" secure-app)
