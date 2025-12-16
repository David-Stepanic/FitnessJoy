(ns fitness-joy.project-test
  (:require [midje.sweet :refer :all]
            [fitness-joy.project :refer :all]))

(fact "update-data should change numeric values in data"
      (let [original data
            updated  (update-data data)]

        (not= (:weekly_workouts original) (:weekly_workouts updated)) => true
        (not= (:skipped_workouts original) (:skipped_workouts updated)) => true

        (not= (get-in original [:heart_rate :avg])
              (get-in updated [:heart_rate :avg])) => true))

