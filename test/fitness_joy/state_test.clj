(ns fitness-joy.state-test
  (:require [midje.sweet :refer :all]
            [fitness-joy.state.state :as state]))

(def sample-event
  {:heart-rate     140
   :blood-sugar    5.0
   :hydration      0.8
   :pressure-upper 120
   :pressure-lower 75
   :caffeine-mg    100
   :sleep-quality  0.9
   :stress-level   0.3})

(facts "apply-event"
       (fact "sets the current event"
             (:current-event (state/apply-event state/initial-state sample-event))
             => sample-event)

       (fact "fills in the analysis"
             (:analysis (state/apply-event state/initial-state sample-event))
             => (contains {:heart-rate :high}))

       (fact "appends the event to history"
             (count (:history (state/apply-event state/initial-state sample-event)))
             => 1)

       (fact "history grows across multiple events"
             (-> state/initial-state
                 (state/apply-event sample-event)
                 (state/apply-event sample-event)
                 :history
                 count)
             => 2)

       (fact "does not mutate the input state"
             (state/apply-event state/initial-state sample-event)
             (:history state/initial-state) => []))

(facts "ingest!"
       (fact "updates the atom"
             (state/reset-state!)
             (state/ingest! sample-event)
             (state/history-count) => 1)

       (fact "reset restores the initial state"
             (state/ingest! sample-event)
             (state/reset-state!)
             (state/history-count) => 0))