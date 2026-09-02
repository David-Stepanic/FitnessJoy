(ns fitness-joy.state.state
  (:require [fitness-joy.decision.rules :as rules]))

(def initial-state
  {:current-event   nil
   :analysis        {}
   :recommendations []
   :history         []})

(def app-state (atom initial-state))

(defn apply-event
  [state event]
  (let [analysis (rules/analyze-event event)
        msgs     (vec (rules/recommend analysis))]
    (-> state
        (assoc :current-event event)
        (assoc :analysis analysis)
        (assoc :recommendations msgs)
        (update :history conj event))))

(defn ingest!
  [event]
  (swap! app-state apply-event event))

(defn reset-state!
  []
  (reset! app-state initial-state))

(defn snapshot
  []
  @app-state)

(defn current-recommendations []
  (:recommendations @app-state))

(defn current-analysis []
  (:analysis @app-state))

(defn history []
  (:history @app-state))

(defn history-count []
  (count (:history @app-state)))

(defn healthy?
  []
  (empty? (:analysis @app-state)))