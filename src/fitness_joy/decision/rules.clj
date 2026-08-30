(ns fitness-joy.decision.rules
  (:require [fitness-joy.model.range :as ranges]
            [fitness-joy.model.recommendation :as rec]))

(defn check-parameter
  ([param-key value]
   (check-parameter param-key value ranges/default-ranges))

  ([param-key value ranges]
   (let [{:keys [low high]} (get ranges param-key)]
     (cond
       (nil? low) :unknown
       (< value low) :low
       (> value high) :high
       :else :ok))))

(defn analyze-event
  ([event]
   (analyze-event event ranges/default-ranges))

  ([event ranges]
   (reduce-kv
     (fn [acc k v]
       (let [status (check-parameter k v ranges)]
         (if (contains? #{:ok :unknown} status)
           acc
           (assoc acc k status))))
     {}
     event)))

(defn recommend
  ([analysis]
   (recommend analysis rec/recommendations))

  ([analysis msgs]
   (keep (fn [[param status]] (get msgs [param status]))
         analysis)))

