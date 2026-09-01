(ns fitness-joy.mock.generator)

(defn rand-between
  [lo hi]
  (/ (Math/round (* 10.0 (+ lo (* (rand) (- hi lo))))) 10.0))

(defn random-event []
  {:heart-rate     (+ 45 (rand-int 85))
   :blood-sugar    (rand-between 3.0 12.0)
   :hydration      (rand-between 0.1 1.0)
   :pressure-upper (+ 85 (rand-int 130))
   :pressure-lower (+ 50 (rand-int 100))
   :caffeine-mg    (rand-int 600)
   :sleep-quality  (rand-between 0.1 1.0)
   :stress-level   (rand-between 0.1 1.0)})