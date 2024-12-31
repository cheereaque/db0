(ns db0.logger.interface
  (:require [integrant.core :as ig]))

(defprotocol ILogger
  (log [this event args]))

(defmethod ig/init-key ::logger
  [_ _options])
