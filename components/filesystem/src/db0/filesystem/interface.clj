(ns db0.filesystem.interface)

(defprotocol IFilesystem
  (create-directory! [this path]))
