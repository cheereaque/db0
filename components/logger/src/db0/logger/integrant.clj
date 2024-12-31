(ns db0.logger.integrant
  (:require [com.brunobonacci.mulog :as mu]
            [db0.logger.interface :as logger]
            [integrant.core :as ig]))

(defrecord MuLogger
           [config]

  logger/ILogger

  (log [_ event args]
    (apply println event (mapcat identity args))))

(defmethod ig/init-key ::logger
  [_ options]
  (MuLogger. {:publisher (mu/start-publisher! options)}))

(defmethod ig/halt-key! ::logger
  [_ {:keys [publisher]}]
  (when-not (nil? publisher)
    (publisher)))
