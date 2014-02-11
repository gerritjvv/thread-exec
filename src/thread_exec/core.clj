(ns thread-exec.core)

(defn abs [n]
  (Math/abs ^int  n))

(defn distance [t [a b]]
  "Returns the distance that t has from the centre of a to b, b must be bigger than a"
  (abs (- (/ (abs (- b a)) 2) t)))


(defn nearest [t p q]
  "Returns p or q of which have the smallest distance to t.
   p and q are vectors of [low high] and t is an single vlaue"
  (let [a (distance t p)
        b (distance t q)]
    (if (= a b) p q)))

(defn nearest-group [t groups]
  (reduce (partial nearest t) groups))

(defn max-value [groups]
  (reduce (fn [[a1 a2] [b1 b2]] (max a1 a2 b1 b2)) groups))


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
    
