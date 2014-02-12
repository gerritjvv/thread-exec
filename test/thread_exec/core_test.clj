(ns thread-exec.core-test
  (:require [clojure.test :refer :all]
            [thread-exec.core :refer :all])
  (:use midje.sweet))

(facts "Test group select functions"
  
  (fact "test distance"
    (distance 2 [0 10]) => 0
    (distance 5 [0 10])  => 0
    (distance 15 [0 10])  => 5)
  
(fact "test nearest"
    (nearest-group 2 [[0 2] [2 3] [3 4]]) => [0 2])

(fact "Test select-group"
    (select-group 2 [[0 2] [2 5]] 1 10) => (select-group 2 [[0 2] [2 5]] 1 10)
    (select-group 10 [[0 2] [2 5]] 1 10) => [[5 11] [[0 2] [2 5] [5 11]]]))

(facts "Test execution"
  
  (fact "Test update timings"
    (let [pool-manager (default-pool-manager 100 4 [0 100] 8)]
      ((run-timed pool-manager :abc #(Thread/sleep 100)))
      ((run-timed pool-manager :abc #(Thread/sleep 100)))
      ((run-timed pool-manager :abc #(Thread/sleep 100)))
      
      
      (-> pool-manager :groups deref) => [[0 100]])
    
    )
  (fact "Test execute"
    (let [pool-manager (default-pool-manager 100 4 [0 100] 8)]
      (dotimes [i 10] (submit pool-manager :abc #(Thread/sleep 100)))
      (-> pool-manager :pools deref keys) => [[0 100]]
       
      (let [first-pool (submit pool-manager :abc #(Thread/sleep 100))]
        
	      (dotimes [i 10] (submit pool-manager :abc #(Thread/sleep 500)))
	    
	      (count (-> pool-manager :pools deref keys sort)) => 2
       
	      (not (= first-pool (submit pool-manager :abc #(Thread/sleep 500)))) => true
       
	      (dotimes [i 10] (submit pool-manager :abc #(Thread/sleep 40)))
	    
        (= first-pool (submit pool-manager :abc #(Thread/sleep 100))) => true))))

    