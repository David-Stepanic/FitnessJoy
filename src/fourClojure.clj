(ns fourClojure)

; 4clojure  26. fibonacci

; evo resenja

(take 5 (map second (iterate (fn [[n-2 n-1]]
                               [n-1 (+ n-2 n-1)])
                             [0 1])))

; => (1 1 2 3 5)

; moze da se resi i preko reduce i preko recur

(defn f [x-1 n]
  (if (< 0 n)
    (f (inc x-1) (dec n))
    x-1))

(defn f2 [x-1 n]
  (if (< 0 n)
    (recur (inc x-1) (dec n))
    x-1))

; recur stavljamo da oznacimo da tu moze da se optimiziju
; preko tail coll optimizacije

; e sad sta je loop recure

; loop je kao neko let sidro za recur, ono na sta ce da se recur vrati

(defn f3 [n]
  (loop [x-1 0 n n]
    (if (< 0 n)
      (recur (inc x-1) (dec n))
      x-1)))

; example 29

(defn upper [s]
  (apply str (reduce
               (fn [acc e]
                 (if (and (not= (compare (str/lower-case e) e) 0) ())
                   (conj acc e)
                   acc))
               []
               s)))

