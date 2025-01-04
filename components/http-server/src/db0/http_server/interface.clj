(ns db0.http-server.interface
  (:require [db0.logger.interface :refer [log]]
            [integrant.core :as ig]
            [muuntaja.core :as m]
            [org.httpkit.server :as hk]
            [reitit.openapi :as openapi]
            [reitit.ring :as r]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.swagger-ui :as swagger]))

(def ^:private api-base-path
  "/api/v1")

(def ^:private openapi-path
  (str api-base-path "/openapi.json"))

(def ^:private openapi-route
  [openapi-path
   {:get {:no-doc true
          :openapi {:info {:title "db0 api"
                           :description "some db0 api description1"
                           :version "0.0.1"}}
          :handler (openapi/create-openapi-handler)}}])

(defn- create-swagger-handler
  []
  (swagger/create-swagger-ui-handler
   {:path api-base-path
    :config {:validatorUrl nil
             :urls [{:name "openapi" :url openapi-path}]
             :urls.primaryName "openapi"
             :operationsSorter "alpha"}}))

(defn- route
  [routes]
  (let [routes* (merge routes openapi-route)
        middleware [muuntaja/format-response-middleware]]
    (r/ring-handler
     (r/router
      routes*
      {:data {:muuntaja m/instance
              :middleware middleware}})
     (r/routes
      (create-swagger-handler)
      (r/create-default-handler)))))

(defmethod ig/init-key ::http-server
  [_ {:keys [routes logger] :as options}]
  (let [server (hk/run-server (route routes) options)]
    (log logger ::http-server-started {:options options})
    server))

(defmethod ig/halt-key! ::http-server
  [_ http-server]
  (http-server :timeout 100))