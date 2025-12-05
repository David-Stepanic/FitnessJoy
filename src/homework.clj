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


(reduce (fn [[cnt zed sum] e]
          [(if (= (mod (+ sum e) 100) 0)
             (inc cnt)
             cnt)
           (if (< (+ sum e) 0)
             (inc zed)
             zed)
           (mod (+ sum e) 100)])
        [0 0 50]
        [-68 -30 48 -5 60 -55 -1 -99 14 -82])

(reduce + (take 2 *1))











