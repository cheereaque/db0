(ns db0.database.interface)

(defprotocol IDatabase
  (create-database [this name]))
