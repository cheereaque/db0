(ns db0.database-registry.interface)

(definterface IDatabaseRegistry
  (create-database [this name]))
