(ns blog.handler
  (:require [compojure.core :refer [defroutes]]
            [blog.routes.home :refer [home-routes]]
            [blog.middleware :as middleware]
            [noir.util.middleware :refer [app-handler]]
            [compojure.route :as route]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.rotor :as rotor]
            [selmer.parser :as parser]
            [environ.core :refer [env]]))

(defroutes
  app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  []
  (timbre/set-config!
    [:appenders :rotor]
    {:min-level :info,
     :enabled? true,
     :async? false,
     :max-message-per-msecs nil,
     :fn rotor/appender-fn})
  (timbre/set-config!
    [:shared-appender-config :rotor]
    {:path "blog.log", :max-size (* 512 1024), :backlog 10})
  (if (env :dev) (parser/cache-off!))
  (timbre/info "blog started successfully"))

(defn destroy
  "destroy will be called when your application
   shuts down, put any clean up code here"
  []
  (timbre/info "blog is shutting down..."))

(def app
 (app-handler
   [home-routes app-routes]
   :middleware
   [middleware/template-error-page middleware/log-request]
   :access-rules
   []
   :formats
   [:json-kw :edn]))
