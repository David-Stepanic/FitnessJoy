(ns fitness-joy.model.event
  (:require [malli.core :as m]))

(def Event
  [:map
   [:timestamp :int]
   [:heart-rate [:int {:min 30 :max 220}]]                  ; BPM
   [:pressure-upper [:int    {:min 60  :max 250}]]          ; mmHg
   [:pressure-lower [:int    {:min 30  :max 120}]]          ; mmHg
   [:blood-sugar [:double {:min 0 :max 30}]]                ; mmol/L
   [:hydration [:double {:min 0 :max 1}]]                   ; 0-1
   [:caffeine-mg [:int {:min 0 :max 1000}]]                  ; 0-1000 mg
   [:sleep-quality [:double {:min 0 :max 1}]]               ; 0-1
   [:stress-level [:double {:min 0 :max 1}]]                ; 0-1
   ])

(defn valid? [event]
  (m/validate Event event))

(defn explain [event]
  (m/explain Event event))



