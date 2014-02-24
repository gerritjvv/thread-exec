# thread-exec


Intelligent thread execution and pooling that automatically create different pools to isolate slow running tasks from faster running tasks and avoid the system slowing down

The api is written in clojure but also has native java bindings.

[thread-exec "0.2.0-SNAPSHOT"]

For maven integration please read: https://clojars.org/thread-exec

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

## From Java

```java
import thread_exec.api;

Object pool = PoolManager.defaultPoolManager(100, 4, new int[]{0, 100}, 8);
PoolManager.submit(pool, "test", new Runnable(){public void run(){System.out.println("hi");}});

//to shutdown
PoolManager.shutdown(pool, 1000L);

//submit support callable and clojure.lang.IFn instances

```

## License

Copyright © 2014 gerritjvv@gmail.com

Distributed under the Eclipse Public License version 1.0
