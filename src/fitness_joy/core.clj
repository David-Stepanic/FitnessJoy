(ns fitness-joy.core
  (:require [fitness-joy.mock.generator :as mock]
            [fitness-joy.state.state :as state]))

(defn print-recommendations [recommendations]
  (if (empty? recommendations)
    (println "Everything looks good - no recommendations right now.")
    (doseq [msg recommendations]
      (println "-" msg))))

(defn simulate-event!
  "Generates one mock event, feeds it into the app state and prints the outcome."
  []
  (let [event (mock/random-event)]
    (state/ingest! event)
    (println "\nNew reading:" event)
    (println "Analysis:" (state/current-analysis))
    (println "Recommendations:")
    (print-recommendations (state/current-recommendations))))

(defn -main
  "Whoop+ live fitness coach - simulates hourly readings and reacts to them."
  [& args]
  (state/reset-state!)
  (println "Starting Whoop+ simulation...")
  (dotimes [_ 5]
    (simulate-event!))
  (println "\nTotal readings processed:" (state/history-count)))
