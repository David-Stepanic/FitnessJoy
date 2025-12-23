(ns exercise
  (:require [criterium.core :refer :all]
            [uncomplicate.fluokitten.core :refer :all]))

(set! *unchecked-math* true)

(def n 1000000)

(def ^ints x
  (int-array n (range n)))

(def ^ints y
  (int-array n (range n)))
;
;(defn dot-areduce [xs ys]
;  (areduce ^doubles xs idx res 0.0
;           (+ res (* (aget ^doubles xs idx)
;                     (aget ^doubles ys idx)))))

(defn dot-int
  [^ints x ^ints y]
  (let [n (alength x)
        start (System/nanoTime)]
    (if (not= n (alength y))
      "Niste uneli nizove isthi dimenzija"
      (let [^long sum
            (loop [i 0
                   acc 0]
              (if (< i n)
                (recur (inc i)
                       (unchecked-add acc
                                      (unchecked-multiply
                                        (aget x i)
                                        (aget y i))))
                acc))
            end (System/nanoTime)]
        {:rezultat sum
         :trajanje-ms (/ (- end start) 1e6)}))))

; chatgtp: (nisam resio do kraja)

(defn matrix [n]
  (into-array
    (repeatedly n #(int-array (range n)))))

(defn transpose
  [^"[[I" m]
  (let [rows (alength m)
        cols (alength (aget m 0))]
    (into-array
      (for [j (range cols)]
        (int-array
          (for [i (range rows)]
            (aget m i j)))))))

(defn matrix-times
  [^"[[I" a ^"[[I" b]
  (let [rows-a (alength a)
        cols-a (alength (aget a 0))
        rows-b (alength b)
        cols-b (alength (aget b 0))]
    (when (not= cols-a rows-b)
      (throw (ex-info "Neispravne dimenzije matrica" {})))
    (let [bt (transpose b)]
      (into-array
        (for [i (range rows-a)]
          (int-array
            (for [j (range cols-b)]
              (:rezultat
                (dot-int (aget a i)
                         (aget bt j))))))))))

;test
(def m1 (matrix 3))
(def m2 (matrix 3))

(def r (matrix-times m1 m2))

(aget r 0 0)
;; => 5




; mnozenje matrica

(defn matrix [n]
  (into-array
    (repeatedly n #(int-array (range n)))))

(defn matriy [n]
  (into-array
    (repeatedly n #(int-array (range n)))))

(aget matrix 0 1)
;=> 1

(defn matrixTimes [x] [y]
  )




































































































