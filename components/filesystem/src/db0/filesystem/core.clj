(ns db0.filesystem.core
  (:require [babashka.fs :as bfs]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [db0.filesystem.interface :as ifs :refer [IFilesystem]]
            [failjure.core :as f]))

(s/def ::root-directory string?)
(s/def ::cleanup? boolean?)

(s/def ::config
  (s/keys :opt-un [::root-directory
                   ::cleanup?]))

(defn- not-blank?
  [s]
  (and (string? s)
       (not (str/blank? s))))

(defn- validate-path
  [path]
  (if (not-blank? path)
    (f/try* (bfs/normalize path))
    (f/fail "path is invalid: " path)))

(defn- rootify
  [root path]
  (f/try* (f/ok->> path
                   (validate-path)
                   (bfs/path root))))

(defrecord Filesystem
           [config]

  IFilesystem

  (start!
    [this]
    (s/assert ::config this)
    this)

  (stop!
    [this]
    this)

  (create-directory!
    [{:keys [config]} path]
    (f/when-let-failed?
     [_ (f/try* (f/ok->> path
                         (rootify (:root-directory config))
                         (bfs/create-dirs)))]
     (f/fail (format "unable to create directory: '%s'" path))))

  (create-directories!
    [this paths]
    (let [fails (reduce
                 (fn [fails path]
                   (when (f/failed? (ifs/create-directory! this path))
                     (conj fails path)))
                 [] paths)]
      (when-not (empty? fails)
        (f/fail {::create-directories-errors fails}))))

  (valid-directory?
    [{:keys [config]} path]
    (let [root (:root-directory config)]
      (f/ok? (rootify root path))))

  (exists?
    [{:keys [config]} path]
    (let [root (:root-directory config)]
      (bfs/exists? (rootify root path))))

  (->file
    [{:keys [config]} path data options]
    (let [path* (rootify (:root-directory config) path)
          data* (.getBytes (String. data))]
      ()
      (f/when-let-failed?
       [fail (f/try* (bfs/write-bytes path* data* options))]
       (f/fail {::create-file-error {:path path*
                                     :data data*
                                     :options options
                                     :fail fail}}))))

  (<-file
    [this path]
    nil)

  (cleanup!
    [this]
    this))

