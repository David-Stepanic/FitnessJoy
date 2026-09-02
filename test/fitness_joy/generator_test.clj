(ns fitness-joy.generator-test
  (:require [midje.sweet :refer :all]
            [fitness-joy.mock.generator :as gen]
            [fitness-joy.model.event :as event]))

(facts "random-event"
       (fact "always produces a valid event"
             (every? event/valid? (repeatedly 500 gen/random-event)) => true)

       (fact "contains all expected keys"
             (set (keys (gen/random-event)))
             => #{:heart-rate :blood-sugar :hydration
                  :pressure-upper :pressure-lower :caffeine-mg
                  :sleep-quality :stress-level})

       (fact "values fall within the expected bounds"
             (let [e (gen/random-event)]
               (:heart-rate e) => #(<= 45 % 130)
               (:blood-sugar e) => #(<= 3.0 % 12.0))))

(facts "rand-between"
       (fact "stays within the given bounds"
             (every? #(<= 0.0 % 1.0) (repeatedly 100 #(gen/rand-between 0.0 1.0)))
             => true))