(ns db0.logger.interface)

(defprotocol ILogger
  (log [this event args]))