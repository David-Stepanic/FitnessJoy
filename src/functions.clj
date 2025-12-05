(ns functions)

;  Testing average function

 (apply / (reduce (fn [[sum cnt] e]
                       [(+ sum e) (inc cnt)])
                           [0 0]
                           [10 20 30]))

; Functions for project

(def exercises (hash-map :pull-ups 5 :dips 10 :push-ups 15 :squats 22))

(def values (vec (vals exercises)))

(defn average [coll]
  (/ (reduce + coll) (count coll)))

(average (values))

; Decide which part is stronger (pull or push)
; Not done yet!

; Determine training level

; Each pull-up is 2 point
; Each dip is 1.5 point
; Each push-up is 1 point
; Each squat is 1 point

(defn results []
  (println "Pull-ups max:")
  (let [a (Integer/parseInt(read-line))]
    (println "Dips max:")
    (let [b (Integer/parseInt(read-line))]
      (println "Push-up max:")
      (let [c (Integer/parseInt(read-line))]
        (println "Squat max:")
        (let [d (Integer/parseInt(read-line))]
          (+ (* a 2) (* b 1.5) c d))))))

(def score (results))

; Levels:

; Beginner 0-15 points
; Intermediate 16-30 points
; Advanced 31+ points

(cond
  (<= 0 score 15) "Beginner level"
  (<= 16 score 30) "Intermediate level"
  (>= score 31) "Advanced level")

