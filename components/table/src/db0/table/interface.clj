(ns db0.table.interface)

(defprotocol ITable
  (create-table [this db name]))
