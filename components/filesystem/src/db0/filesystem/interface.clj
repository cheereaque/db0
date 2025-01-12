(ns db0.filesystem.interface)

(defprotocol IFilesystem
  (create-directory! [this path])

  (create-directories! [this paths])

  (delete-directory! [this path])

  (exists? [this path])

  (write-file! [this path data options])

  (read-file! [this path])

  (cleanup! [this]))

