(ns fitness-joy.rules-test
  (:require [midje.sweet :refer :all]
            [fitness-joy.decision.rules :as rules]))

(def test-ranges
  {:blood-sugar {:low 3.9 :high 7.8 :unit "mmol/L"}
   :heart-rate  {:low 50  :high 100 :unit "bpm"}})

(facts "check-parameter"
       (fact "value below the lower bound is :low"
             (rules/check-parameter :blood-sugar 3.0 test-ranges) => :low)

       (fact "value above the upper bound is :high"
             (rules/check-parameter :blood-sugar 9.2 test-ranges) => :high)

       (fact "value within range is :ok"
             (rules/check-parameter :blood-sugar 5.5 test-ranges) => :ok)

       (fact "boundary values are :ok"
             (rules/check-parameter :blood-sugar 3.9 test-ranges) => :ok
             (rules/check-parameter :blood-sugar 7.8 test-ranges) => :ok)

       (fact "falls back to default ranges when none are given"
             (rules/check-parameter :heart-rate 130) => :high))

(facts "analyze-event"
       (fact "returns only out-of-range parameters"
             (rules/analyze-event {:blood-sugar 9.2 :heart-rate 70} test-ranges)
             => {:blood-sugar :high})

       (fact "returns an empty map when everything is fine"
             (rules/analyze-event {:blood-sugar 5.0 :heart-rate 70} test-ranges)
             => {})

       (fact "catches multiple issues at once"
             (rules/analyze-event {:blood-sugar 2.0 :heart-rate 140} test-ranges)
             => {:blood-sugar :low :heart-rate :high}))

(def test-msgs
  {[:blood-sugar :high] "sugar high"
   [:heart-rate :high]  "hr high"})

(facts "recommend"
       (fact "returns a message for each issue"
             (set (rules/recommend {:blood-sugar :high} test-msgs))
             => #{"sugar high"})

       (fact "empty analysis yields no messages"
             (rules/recommend {} test-msgs) => empty?)

       (fact "skips combinations without a defined message"
             (rules/recommend {:blood-sugar :low} test-msgs) => empty?)

       (fact "multiple issues yield multiple messages"
             (set (rules/recommend {:blood-sugar :high :heart-rate :high} test-msgs))
             => #{"sugar high" "hr high"}))