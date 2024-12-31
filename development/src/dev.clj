(ns dev
  (:refer-clojure :exclude [test])
  (:require [clojure.tools.namespace.repl :refer [set-refresh-dirs]]
            [duct.core :as duct]
            [integrant.repl :as repl]))

(def ^:private default-config
  "db0/base.edn")

(def ^:private profiles
  [:duct.profile/dev
   :duct.profile/local])

(defn- get-config
  []
  (-> default-config
      (duct/resource)
      (duct/read-config)))

(defn- start
  []
  (repl/go))

(defn- stop
  []
  (repl/halt))

(defn- restart
  []
  (stop)
  (start))

(duct/load-hierarchy)

(set-refresh-dirs "development/src"
                  "src"
                  "test")

(repl/set-prep!
 #(duct/prep-config (get-config) profiles))

(comment
  (stop)
  (start)
  (restart))
