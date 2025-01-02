(ns dev
  (:refer-clojure :exclude [test])
  (:require [clojure.tools.namespace.repl :refer [set-refresh-dirs]]
            [duct.core :as duct]
            [integrant.core :as ig]
            [integrant.repl :as repl]
            [portal.api :as p]))

(defonce ^:private portal
  (atom nil))

(def ^:private default-config
  "db0/base.edn")

(def ^:private profiles
  [:duct.profile/dev
   :duct.profile/local])

(defn- start-portal
  []
  (when (nil? @portal)
    (let [portal* (p/open {:launcher :vscode})]
      (reset! portal portal*)
      (add-tap #'p/submit)
      portal*)))

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

(start-portal) ;; TODO: move portal to a separate development component

(comment
  ;; reload portal
  (do
    (reset! portal nil)
    (start-portal)))