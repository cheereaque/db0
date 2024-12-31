(ns db0.api.core
  (:require [org.httpkit.server :as hk]))

(defonce server (atom nil))

(def app
  (fn [req]
    {:status 200
     :body "Hello World"}))

(defn stop-server
  []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn -main
  []
  (reset! server (hk/run-server #'app {:port 8080
                                       :join? true})))

(comment
  (stop-server)
  (-main))
