# thread-exec


Intelligent thread execution and pooling that automatically create different pools to isolate slow running tasks from faster running tasks and avoid the system slowing down

["thread-exec" 0.1.0-SNAPSHOT]

## Usage

```clojure
(require '[thread-exec.core :refer :all])

(def pool-manager (default-pool-manager 100 4 [0 100] 8))
;; parameters are [threshold max-groups start-group pool-size] 
;; new groups are created up to a maximum of max-groups if the average time a function takes
;; is larger than any group + threshold, each time a new group is created a new thread pool is assigned,
;; the thread pool is cached with a max limit of pool-size.
;; note that if the thread pool is maxed out the calling thread will block only for that thread pool, until 
;; threads become available.

(submit pool-manager :abc #(Thread/sleep 500))

(get-layout pool-manager)
;; {[0 100] []}

(shutdown pool-manager 1000)
;; shutdown calls shutdown on each pool waiting a maximum of t milliseconds before calling shutdownNow


```

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
