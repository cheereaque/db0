(ns db0.filesystem.core
  (:require [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [db0.fault.interface :refer [fault]]
            [db0.filesystem.interface :as ifs :refer [IFilesystem]]))

(def ^:private filesystem-default-root
  "/tmp/db0")

(s/def ::root string?)
(s/def ::cleanup? boolean?)

(s/def ::fs
  (s/keys :opt-un [::root
                   ::cleanup?]))

(defn- rootify
  [{:keys [root]} path]
  (io/file (str root "/" path)))

(defrecord Filesystem
           [fs]

  IFilesystem

  (create-directory!
    [{:keys [fs]} path]
    (let [path* (rootify fs path)]
      (try

        (.mkdirs path*)

        (catch Exception e
          (fault ::filesystem-create-directory-error
                 {:context {:path path*}
                  :exception e})))))

  (create-directories!
    [this paths]
    (doseq [path paths]
      (ifs/create-directory! this path)))

  (delete-directory!
    [{:keys [fs] :as this} path]
    (let [path* (rootify fs path)]
      (try

        (when (ifs/exists? this path)
          (run! io/delete-file (reverse (file-seq path*))))

        (catch Exception e
          (fault ::filesystem-delete-directory-error
                 {:context {:path path*}
                  :exception e})))))

  (exists?
    [{:keys [fs]} path]
    (.exists (rootify fs path)))

  (write-file!
    [{:keys [fs]} path data options]
    (let [path* (rootify fs path)]
      (try

        (io/make-parents path*)
        (apply spit path* data (mapcat identity options))

        (catch Exception e
          (fault ::filesystem-write-error
                 {:context {:path path*
                            :options options}
                  :exception e})))))

  (read-file!
    [{:keys [fs]} path]
    (let [path* (rootify fs path)]
      (try

        (slurp path*)

        (catch Exception e
          (fault ::filesystem-read-error
                 {:context {:path path*}
                  :exception e})))))

  (cleanup!
    [{:keys [fs] :as this}]
    (when (:cleanup? fs)
      (ifs/delete-directory! this "/"))))

(defn create-filesystem
  "Create a filesystem"
  [{:keys [root] :as options}]
  (s/assert ::fs options)
  (let [root* (if (not (str/blank? root))
                root
                filesystem-default-root)]

    (->Filesystem (assoc options
                         :root root*))))
