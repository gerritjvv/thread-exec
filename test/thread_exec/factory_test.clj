(ns thread-exec.factory-test
  (:require [thread-load.api :as api]
            [thread-exec.core :as core])
  (:use midje.sweet))


(defn helper-test-factory-load [t]
  "Test that the send factory works as expected"
  )

(fact "Test factory function "
      (let [v (promise)
            f (api/thread-load-factory :latency-grouped [(fn [_ msg] (deliver v msg))] {})]
        (f :k :a)
        (let [v2 (deref v 10000 nil)]
          v2 => :a)
        (f)))
