(ns friend-acl.init
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

(defn logout [request]
  (redirect "../s/login.html"))

(defn json-response [data & [status]]
    {:status (or status 200)
        :headers {"Content-Type" "application/json"}
        :body (json-str data)})

(defn login-failed [request]
  (redirect "/friend-acl/s/login.html"))

(defn interactive-login-redirect
  [{:keys [params] :as request}]
  (ring.util.response/redirect (let [param (str "&login_failed=Y&username=" (:username params))
                                     login-uri (-> request ::friend/auth-config :login-uri)]
                                 (str (if (.contains login-uri "?") login-uri (str login-uri "?"))
                                      param))))

(defn interactive-form
  [& {:keys [login-uri credential-fn login-failure-handler redirect-on-auth?] :as form-config
      :or {redirect-on-auth? true}}]
  (ring-params/wrap-params (fn [{:keys [uri request-method params] :as request}]
    (when (and (= (gets :login-uri form-config (::friend/auth-config request)) uri)
               (= :post request-method))
      (let [{:keys [username password] :as creds} {:username (get params "username") :password (get params "password")}]
        ;(println "in interactive form" username password)
        (if-let [user-record (and username password
                                  ((gets :credential-fn form-config (::friend/auth-config request))
                                    (with-meta creds {::friend/workflow :interactive-form})))]
          (workflows/make-auth user-record
                     {::friend/workflow :interactive-form
                      ::friend/redirect-on-auth? redirect-on-auth?})
          ((or (gets :login-failure-handler form-config (::friend/auth-config request)) #'interactive-login-redirect)
            (update-in request [::friend/auth-config] merge form-config))))))))

(defn ping [request]
  (json-response "pong"))

(defn current-authentication [request]
  (json-response {:auth (friend/current-authentication) :id (friend/identity request)}))

(def app (router
  [(GET "/ping" ping)
   (GET "/logout" (friend/logout logout))
   (GET "/auth" current-authentication)
   (GET "/user-only-ping" (friend/wrap-authorize ping [::user]))
   (GET "/admin-only-ping" (friend/wrap-authorize ping [::admin]))]))

(def secure-app (-> app
  (friend/authenticate
    {:login-uri "/friend-acl/api/login"
     :unauthorized-handler unauthorized-handler
     :workflows [(interactive-form
                   :login-uri "/friend-acl/api/login"
                   :login-failure-handler login-failed
                   :credential-fn (partial creds/bcrypt-credential-fn users))]})
  (ring-session/wrap-session)))

(web/start "/api/" secure-app)
(web/start "/s/" (ring-file/wrap-file not-found "public/s/"))
