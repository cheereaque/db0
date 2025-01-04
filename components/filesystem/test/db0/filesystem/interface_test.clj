(ns db0.filesystem.interface-test
  (:require [clojure.test :refer [deftest]]
            [db0.filesystem.interface :as filesystem]
            [fudje.sweet :refer :all]))

(deftest dummy-test
  (fact "fact1"
        (+ 1 1) => 2))