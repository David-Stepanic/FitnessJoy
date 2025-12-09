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

(def users (hash-map :Marko "Beginner" :Lazar "Intermediate" :Milos "Advanced"))

(def user-names (map name (keys users)))

(def user-levels (vals users))

(defn add-user [first last age level hybrid]
                {:first-name first
                 :last-name last
                 :age age
                 :level level
                 :hybrid hybrid})

(def user1 (add-user "Lazar" "Hrebeljanovic" 24 "Advanced" "No"))
(def user2 (add-user "Milos" "Obilic" 29 "Intermediate" "Yes"))
(def user3 (add-user "Novak" "Djokovic" 38 "Advanced" "No"))
(def user4 (add-user "Jovan" "Jovanovic" 18 "Beginner" "No"))
(def user5 (add-user "Jovan" "Memedovic" 58 "Beginner" "Yes"))

(defn get-user [{:keys [first-name last-name age level hybrid]}]
                (println first-name last-name age))

(get-user user1)

; Print -> Lazar Hrebeljanovic 24





