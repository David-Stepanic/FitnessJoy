(ns fitness-joy.model.event
  (:require [malli.core :as m]))

(def event
  [:map
   [:timestamp :int]
   [:heart-rate [:int {:min 30 :max 220}]]                  ; BPM
   [:blood-sugar [:double {:min 0 :max 30}]]                ; mmol/L
   [:hydration [:double {:min 0 :max 1}]]                   ; 0-1
   [:caffeine-mg [:int {:min 0 :max 400}]]                  ; 0-400 mg
   [:sleep-quality [:double {:min 0 :max 1}]]               ; 0-1
   [:stress-level [:double {:min 0 :max 1}]]                ; 0-1
   ])

