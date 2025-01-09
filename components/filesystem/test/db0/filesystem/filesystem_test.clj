(ns db0.filesystem.filesystem-test
  (:require [clojure.test :refer [deftest use-fixtures]]
            [db0.filesystem.core :as fs]
            [db0.filesystem.interface :as ifs]
            [failjure.core :refer [ok? failed?]]
            [fudje.sweet :refer :all]))

(def ^:dynamic *fs*
  "Filesystem object to use in tests"
  nil)

(defn- with-temp-fs
  [f]
  (let [fs (fs/->Filesystem {:root-directory "/tmp/db0"
                             :cleanup? true})]
    (ifs/start! fs)
    (binding [*fs* fs]
      (try
        (f)
        (finally
          (ifs/stop! fs))))))

(use-fixtures :each with-temp-fs)

(deftest filesystem-valid-directory?
  (fact "filesystem-valid-directory? returns true for valid directory"
        (ifs/valid-directory? *fs* "temp1") => truthy)

  (fact "filesystem-valid-directory? returns false for invalid directory"
        (ifs/valid-directory? *fs* nil) => falsey
        (ifs/valid-directory? *fs* "") => falsey
        (ifs/valid-directory? *fs* 123) => falsey))

(deftest filesystem-exists?
  (let [directory (str (random-uuid))]
    (fact "filesystem-exists? returns true for existing directory"
          (ok? (ifs/create-directory! *fs* directory)) => truthy
          (ifs/exists? *fs* directory) => truthy))

  (fact "filesystem-exists? returns false for non-existing directory"
        (ifs/exists? *fs* "/dummy-folder") => falsey
        (ifs/exists? *fs* "") => falsey
        (ifs/exists? *fs* nil) => falsey))

(deftest filesystem-create-directory
  (let [directory (str (random-uuid))]
    (fact "filesystem-create-directory creates directory correctly"
          (ok? (ifs/create-directory! *fs* directory)) => truthy
          (ifs/exists? *fs* directory) => truthy))

  (fact "filesystem-create-directory returns error for invalid directory"
        (failed? (ifs/create-directory! *fs* nil)) => truthy
        (failed? (ifs/create-directory! *fs* "")) => truthy
        (failed? (ifs/create-directory! *fs* 123)) => truthy))

(deftest filesystem-create-directories
  (let [directory-1 "test-1"
        directory-2 "test-2"
        directories [directory-1 directory-2]]
    (fact "filesystem-create-directories creates directories correctly"
          (ok? (ifs/create-directories! *fs* directories)) => truthy
          (ifs/exists? *fs* directory-1) => truthy
          (ifs/exists? *fs* directory-2) => truthy)

    (fact "filesystem-create-directories returns error for invalid directories"
          (failed? (ifs/create-directories! *fs* [nil])) => truthy
          (failed? (ifs/create-directories! *fs* [""])) => truthy
          (failed? (ifs/create-directories! *fs* [123])) => truthy)))

(deftest filesystem-create-file
  (let [file "test-file"]
    (fact "filesystem-create-file creates file correctly"
          (ok? (tap> (ifs/->file *fs* file "test" {}))) => truthy
          (ifs/exists? *fs* file) => truthy)))

(comment
  (do
    (require '[dev :as dev])
    (#'dev/start-portal))
  (clojure.test/test-vars [#'filesystem-valid-directory?])
  (clojure.test/test-vars [#'filesystem-exists?])
  (clojure.test/test-vars [#'filesystem-create-directory])
  (clojure.test/test-vars [#'filesystem-create-directories])
  (clojure.test/test-vars [#'filesystem-create-file]))

