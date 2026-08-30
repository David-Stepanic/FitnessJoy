(ns fitness-joy.mock.generator
  (:require [fitness-joy.model.event :as event]))

(defn rand-between
  [lo hi]
  (+ lo (* (rand) (- hi lo))))

(defn random-event []
  {:timestamp      (System/currentTimeMillis)
   :heart-rate     (+ 45 (rand-int 85))
   :blood-sugar    (rand-between 3.0 12.0)
   :hydration      (rand-between 0.1 1.0)
   :pressure-upper (+ 85 (rand-int 130))
   :pressure-lower (+ 50 (rand-int 100))
   :caffeine-mg    (rand-int 600)
   :sleep-quality  (rand-between 0.1 1.0)
   :stress-level   (rand-between 0.1 1.0)})