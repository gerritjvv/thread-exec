(ns thread-exec.core-test
  (:require [clojure.test :refer :all]
            [thread-exec.core :refer :all])
  (:use midje.sweet))

(facts "Test group select functions"
  
  (fact "test distance"
    (distance 2 [0 10]) => 3
    (distance 5 [0 10])  => 0
    (distance 10 [0 10])  => 5)
  
(fact "test nearest"
    (nearest-group 2 [[0 2] [2 3] [3 4]]) => [0 2])

(fact "Test select-group"
    (select-group 2 [[0 2] [2 5]] 1 10) => (select-group 2 [[0 2] [2 5]] 1 10)
    (select-group 10 [[0 2] [2 5]] 1 10) => [[5 14] [[0 2] [2 5] [5 14]]]))

    