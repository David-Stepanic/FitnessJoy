(ns homework)

(require '[clojure.string :as str])

(str/split-lines (slurp "directions.txt"))

(read-string (str/replace *1 #"L|R" {"L" "-" "R" "+"}))

(map #(read-string %) *1)

(vec *1)

; Advent of Code Day 1 - part one

(first  (reduce (fn [[cnt sum] e]
                  [(if (= (mod (+ sum e) 100) 0)
                     (inc cnt)
                     cnt)
                   (+ sum e)])
                [0 50]
                [-68 -30 48 -5 60 -55 -1 -99 14 -82]))

; Advent of Code Day 1 - part two

; CLASS VERSION - didn't check starting from zero, and also algorithm
; was increasing counter even if safe stops on zero, which is wrong because
; the result includes only the cases where we pass zero during the rotations

(reduce (fn [[cnt move sum] e]
          [(if (= (mod (+ sum e) 100) 0)
             (inc cnt)
             cnt)
           (if (< (+ sum e) 0)
             (+ (abs  (- 1 (quot (+ sum e) 100))) move)
             (+ (quot (+ sum e) 100) move))
           (mod (+ sum e) 100)])
        [0 0 50]
        [-370])

; Solution

(reduce (fn [[cnt move sum] e]
          (println sum)
          [(if (= (mod (+ sum e) 100) 0)
             (inc cnt)
             cnt)
           (if (<= (+ sum e) 0)
             (+ (if (= (mod (+ sum e) 100) 0)
                  (abs (quot (abs(+ sum e)) 100))
                  (abs (- 1 (quot (+ sum e) 100))))
                (if (= sum 0)
                  (- move 1)
                  move))
             (+ (quot (+ sum e) 100)
                (if (= (mod (abs(+ sum e)) 100) 0)
                  (- move 1)
                  move)))
           (mod (+ sum e) 100)])
        [0 0 50]
        [-50])

(defn dot [x y]
  (reduce + (map * x y)))

(time (dotimes [_ 1000000]
        (dot [(float 1)] [(float 1)])))
;"Elapsed time: 369.6296 msecs"
;=> nil































