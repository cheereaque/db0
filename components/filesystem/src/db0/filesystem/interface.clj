(ns db0.filesystem.interface)

(defprotocol IFilesystem
  (start! [this])

  (stop! [this])

  (create-directory! [this path])

  (create-directories! [this paths])

  (valid-directory? [this path])

  (exists? [this path])

  (->file [this path data options])

  (<-file [this path])

  (cleanup! [this]))

