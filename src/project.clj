(ns project
  (:require [clj-http.client :as client]))

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

; Function Assign level - Checks if selected level is correct for user's score

; Simulation fetching live fitness data from user's smartwatch

(defn fetch-json [url]
  (let [response (client/get url {:as :json})]
    (:body response)))

 (def data (fetch-json "https://mocki.io/v1/4350f790-594d-4936-b740-62c218fe6abc"))

(def numbers  (map #(/ % 10) (range 5 11)))

(map #(double %) numbers)
; => (0.5 0.6 0.7 0.8 0.9 1.0)

(def doubleNums (map #(double %) numbers))

(defn random-number []
  (nth doubleNums (rand-int (count doubleNums))))

(defn update-data [m]
  (reduce-kv
    (fn [acc k v]
      (assoc acc k
                (cond
                  (map? v) (update-data v)
                  (number? v) (int (* v (random-number)))
                  :else v)))
    {}
    m))

