(ns MatrixFeature
  (:require [criterium.core :refer :all]
            [uncomplicate.fluokitten.core :refer :all]))

(defn feature-vec [n]
  (map (partial cons 1)
       (for [x (range n)]
         (take 22 (repeatedly rand)))))

(defn dot-product [x y]
  (reduce + (map * x y)))

(defn transpose
  "returns the transposition of a `coll` of vectors"
  [coll]
  (apply map vector coll))

(defn matrix-mult
  [mat1 mat2]
  (let [row-mult (fn [mat row]
                   (map (partial dot-product row)
                        (transpose mat)))]
    (map (partial row-mult mat2)
         mat1)))

;(defn test-my-mult
;  [n afn]
;  (let [xs  (feature-vec n)
;        xst (transpose xs)]
;    (time (dorun (afn xst xs)))))

(def n 100)
(def mat1 (vec (repeatedly n #(vec (range n)))))
(def mat2 (vec (repeatedly n #(vec (range n)))))


(time
  (def result (matrix-mult mat1 mat2)))

(def s (lazy-seq [1 2 3]))

(def s1 (doall (map println s)))

; s je sekvenca

