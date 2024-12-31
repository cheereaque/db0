(ns db0.api.handler
  (:require [db0.logger.interface :as logger]
            [integrant.core :as ig]))

(defmethod ig/init-key ::ping
  [_ _]
  (fn [& _]
    {:status 200 :body "pong2"}))
