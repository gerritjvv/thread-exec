(ns thread-exec.core
  (:require [clojure.tools.logging :refer [info error]])
  (:import 
    [java.util.concurrent ThreadFactory BlockingQueue ArrayBlockingQueue Callable ThreadPoolExecutor SynchronousQueue TimeUnit ExecutorService ThreadPoolExecutor$CallerRunsPolicy]))



(defn- create-exec-service [threads]
  (let [queue (ArrayBlockingQueue. (int 100))
			  exec  (doto (ThreadPoolExecutor. 0 threads 60 TimeUnit/SECONDS queue)
					     (.setRejectedExecutionHandler  (ThreadPoolExecutor$CallerRunsPolicy.)))]
    exec))
       

(defn abs [n]
  (Math/abs ^int  n))

(defn distance [t [a b]]
  "Returns the distance that t has from the centre of a to b, b must be bigger than a"
   (if (and (> t a) (< t b)) 0 (abs (- b t))))


(defn nearest [t p q]
  "Returns p or q of which have the smallest distance to t.
   p and q are vectors of [low high] and t is an single vlaue"
  (let [a (distance t p)
        b (distance t q)]
    (if (< a b) p q)))

(defn nearest-group [t groups]
  (reduce (partial nearest t) groups))

(defn max-value [groups]
  (reduce (fn [a1 [b1 b2]] (max a1 b1 b2)) 0 groups))


(defn select-group [t groups threshold max-groups]
  "Select the nearest group, if the distance between the nearest group is bigger than the threshold 
   a new group is created with [max-previous-value (+ max-previous-value distance threashold)
   otherwise the nearest group is used and its range is increased to include the new distance, i.e. its grown"
  (let [v (nearest-group t groups)
        d (distance t v)]
    (if (and (> d threshold) (< (count groups) max-groups))
      (let [m (max-value groups)
            v2 [m (+ m d threshold)]]
        [v2 (conj groups v2)])
      [v  groups])))             
 

(defn create-pool-manager [threshold max-groups start-group pool-create]
 {:pool-create pool-create 
  :max-groups max-groups :threshold threshold :pools (ref {}) :groups (ref [start-group]) :timings (ref {})})

(defn default-pool-manager [threshold max-groups start-group pool-size]
   (create-pool-manager threshold max-groups start-group #(create-exec-service pool-size)))

(defn get-layout [{:keys [pools timings groups threshold max-groups]} & args]
  "Outputs {group [topic topic ...] }"
  (let [layout (reduce #(assoc %1 %2 []) {} @groups)
        verbose (some #{:verbose} args)
        l-m		    (reduce (fn [m k] 
						              (let [avg (-> timings deref (get k) :avg)
						                    [g _] (select-group avg @groups threshold max-groups)]
						                (merge-with conj m {g k}))) layout (keys @timings))]
    (if verbose
      (into {} (map (fn [[k v]] [k [(str (get @pools k)) v]]) l-m))
      l-m)))
                       
(defn get-pool [{:keys [max-groups threshold pools groups pool-create]} t]
  "Gets a pool if within the groups, otherwise a new pool is created and returned"
  (let [[g groups2] (select-group t @groups threshold max-groups)]
    (dosync 
      (alter groups (fn [m] groups2))
      
	    (if-let [exec (get @pools g)]
	      exec
	      (get (alter pools (fn [m] (if (nil? (get m g))
                                          (assoc m g (pool-create))
                                          m)))
             g)))))


(defn average [t-seq]
  "t-seq must be sorted, if count > 4 we drop the first and last items to iliminate peeks"
  (let [s (if (> (count t-seq) 4) (-> t-seq rest drop-last) t-seq)]
    (/ (reduce + s) (count s))))

(defn update-timings! [{:keys [timings] :as p} topic t]
  "Updates the timings reference with the new t and average, keeping the history for the time sequence at 10"
   (dosync
      (commute timings
         (fn [timings]
           (let [m (if-let [m (get timings topic)] m {:t-seq [] :avg 0})
                 t-seq (conj (:t-seq m) t)
                 m2 (-> m (assoc :t-seq (vec (take-last 10 t-seq)))
                       (assoc :avg (average (sort t-seq))))]
             (assoc timings topic m2))))))
           
  

(defn ^Callable run-timed [pool-manager topic f]
  "Returns a function that will run f and update its run timings against the pool-manager, on exception the error is printed out"
  (fn []
    (let [start (System/currentTimeMillis)]
      (try 
        (f)
        (catch Exception e (error e e))
        (finally 
          (update-timings! pool-manager topic (- (System/currentTimeMillis) start)))))))


  
    
(defn get-topic-average [{:keys [timings]} topic]
  (if-let [m (get @timings topic)]
    (if (> (count (:t-seq m)) 3) 
      (:avg m)
      0)
    0))

 (defn ^ExecutorService get-exec [pool-manager topic]
   "Returns the thread pool as per the topic's average execution time"
   (get-pool pool-manager (get-topic-average pool-manager topic)))
 
 (defn submit [pool-manager topic f]
   "Main entry point of this library, it runs the function f in a execution pool,
    depending on the cumulative average execution time of the function.
    The function returns the ExecutorService that was used."
   (let [exec (get-exec pool-manager topic)]
    (.submit exec (run-timed pool-manager topic f))
    exec))

 
 (defn shutdown [{:keys [pools]} ^Long timeout]
   (doseq [[k ^ExecutorService exec] @pools]
     (.shutdown exec)
     (if (not (.awaitTermination exec timeout (TimeUnit/MILLISECONDS)))
       (.shutdownNow exec))))
   




