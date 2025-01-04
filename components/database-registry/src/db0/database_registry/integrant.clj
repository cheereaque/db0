(ns db0.database-registry.integrant
  (:require [db0.logger.interface :refer [log]]
            [integrant.core :as ig]))

(defmethod ig/init-key ::database-registry
  [_ {:keys [logger]}]
  (log logger ::database-registry-started {}))

(defmethod ig/halt-key! ::database-registry
  [_ {:keys [logger]}]
  (log logger ::database-registry-stopped {}))
