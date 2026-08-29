(ns fitness-joy.decision.rules)

(def default-ranges
  {:blood-sugar {:low 3.9 :high 7.8 :unit "mmol/L"}
   :hydration {:low 0.5 :high 1.0 :unit "%"}
   :pressure-upper  {:low 90  :high 180  :unit "mmHg"}
   :pressure-lower  {:low 45  :high 100   :unit "mmHg"}
   :heart-rate {:low 50 :high 100 :unit "bpm"}
   :caffeine-mg {:low 0 :high 400 :unit "mg"}
   :sleep-quality {:low 0.2 :high 1.0 :unit "%"}
   :stress-level {:low 0.2 :high 1.0 :unit "%"}})

(defn check-parameter
  ([param-key value]
   (check-parameter param-key value default-ranges))

  ([param-key value ranges]
   (let [{:keys [low high]} (get ranges param-key)]
     (cond
       (nil? low) :unknown
       (< value low) :low
       (> value high) :high
       :else :ok))))

(defn analyze-event
  ([event]
   (analyze-event event default-ranges))

  ([event ranges]
   (reduce-kv
     (fn [acc k v]
       (let [status (check-parameter k v ranges)]
         (if (contains? #{:ok :unknown} status)
           acc
           (assoc acc k status))))
     {}
     event)))
