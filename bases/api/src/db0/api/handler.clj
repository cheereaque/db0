(ns db0.api.handler
  (:require [integrant.core :as ig]))

(defmethod ig/init-key ::ping
  [_ _]
  (fn [& _]
    {:status 200 :body "pong2"}))

(defmethod ig/init-key ::databases
  [_ _]
  (fn [& _]
    {:status 200 :body []}))

(defmethod ig/init-key ::create-database
  [& args]
  (fn [& args*]
    (tap> {:args args :args* args*})
    {:status 200 :body []}))