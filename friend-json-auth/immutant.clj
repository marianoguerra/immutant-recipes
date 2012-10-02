(ns friend-json-auth.init
  (:use
    [cemerick.friend.util :only (gets)]
    [ring.util.response :only (redirect)]
    ring-router.core
    [clojure.data.json :only (read-json json-str)])

  (:require [cemerick.friend :as friend]
            (cemerick.friend [workflows :as workflows]
                             [credentials :as creds])
            [ring.middleware.params :as ring-params]
            [ring.middleware.file :as ring-file]
            [ring.middleware.session :as ring-session]
            [immutant.web :as web]))

(def users {"root" {:username "root"
                    :password (creds/hash-bcrypt "admin_password")
                    :roles #{::admin}}
            "jane" {:username "jane"
                    :password (creds/hash-bcrypt "user_password")
                    :roles #{::user}}})

(defn unauthorized-handler [thing]
  {:status 403
      :headers {"Content-Type" "application/json"}
      :body "\"unauthorized\""})


(defn not-found [request]
  {:status 404
   :header {"Content-Type" "text/plain"}
   :body "file not found"})

(defn json-response [data & [status]]
    {:status (or status 200)
        :headers {"Content-Type" "application/json"}
        :body (json-str data)})

(defn logout [request]
  (json-response {:ok true :reason "logged out"}))

(defn login [request]
  (json-response (friend/current-authentication)))

(defn login-failed [request]
  (json-response {:ok false :reason "authentication failed"} 401))

(defn json-login
  [& {:keys [login-uri credential-fn login-failure-handler] :as form-config}]
  (fn [{:keys [uri request-method body] :as request}]
    (when (and (= (gets :login-uri form-config (::friend/auth-config request)) uri)
               (= :post request-method))
      (let [{:keys [username password] :as creds} (read-json (slurp body))]
        (if-let [user-record (and username password
                                  ((gets :credential-fn form-config (::friend/auth-config request))
                                    (with-meta creds {::friend/workflow :json-login})))]
          (workflows/make-auth user-record
                     {::friend/workflow :json-login
                      ::friend/redirect-on-auth? false})
          ((or (gets :login-failure-handler form-config (::friend/auth-config request)) #'login-failed)
            (update-in request [::friend/auth-config] merge form-config)))))))

(defn ping [request]
  (json-response "pong"))

(defn current-authentication [request]
  (json-response {:auth (friend/current-authentication) :id (friend/identity request)}))

(def app (router
  [(GET "/ping" ping)
   (DELETE "/session" (friend/logout logout))
   (POST "/session" login)
   (GET "/session" (fn [request] (redirect "../s/login.html")))
   (GET "/auth" current-authentication)
   (GET "/user-only-ping" (friend/wrap-authorize ping [::user]))
   (GET "/admin-only-ping" (friend/wrap-authorize ping [::admin]))]))

(def secure-app (-> app
  (friend/authenticate
    {:login-uri "/friend-json-auth/api/session"
     :unauthorized-handler unauthorized-handler
     :workflows [(json-login
                   :login-uri "/friend-json-auth/api/session"
                   :login-failure-handler login-failed
                   :credential-fn (partial creds/bcrypt-credential-fn users))]})
  (ring-session/wrap-session)))

(web/start "/api/" secure-app)
(web/start "/s/" (ring-file/wrap-file not-found "public/s/"))
