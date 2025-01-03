(ns db0.filesystem.interface-test
  (:require [db0.filesystem.interface :as filesystem]
            [midje.sweet :as m]))

(m/facts "dummy-test"
         (+ 1 2) m/=> 2)
