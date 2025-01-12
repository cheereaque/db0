(ns db0.filesystem.filesystem-test
  (:require [clojure.test :refer [deftest use-fixtures]]
            [db0.fault.interface :refer [ok?]]
            [db0.filesystem.core :as fs]
            [db0.filesystem.interface :as ifs]
            [fudje.sweet :refer :all]))

(def ^:dynamic *fs*
  "Filesystem object to use in tests"
  nil)

(defn- with-temp-fs
  [f]
  (let [fs (fs/create-filesystem {:cleanup? true})]
    (binding [*fs* fs]
      (try
        (f)
        (finally
          (ifs/cleanup! fs))))))

(use-fixtures :each with-temp-fs)

(deftest filesystem-exists?
  (let [directory (str (random-uuid))]
    (fact "filesystem-exists? returns true for existing directory"
          (ok? (ifs/create-directory! *fs* directory)) => truthy
          (ifs/exists? *fs* directory) => truthy))

  (let [unknown-directory (str (random-uuid))]
    (fact "filesystem-exists? returns false for unknown directory"
          (ifs/exists? *fs* unknown-directory) => falsey)))

(deftest filesystem-create-directory
  (let [directory (str (random-uuid))]
    (fact "filesystem-create-directory creates directory correctly"
          (ok? (ifs/create-directory! *fs* directory)) => truthy
          (ifs/exists? *fs* directory) => truthy)))

(deftest filesystem-create-directories
  (let [directories (take 2 (repeat (random-uuid)))]
    (fact "filesystem-create-directories creates directories correctly"
          (ok? (ifs/create-directories! *fs* directories)) => truthy
          (map #(ifs/exists? *fs* %) directories) => (every-checker truthy))))

(deftest filesystem-create-file
  (let [file "test-file"
        data "some data"]
    (fact "filesystem-create-file creates file correctly"
          (ok? (ifs/write-file! *fs* file data {})) => truthy
          (ifs/exists? *fs* file) => truthy
          (ifs/read-file! *fs* file) => data)))