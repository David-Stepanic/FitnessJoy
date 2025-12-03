(ns functions)

(defn -main []
   (println (apply / (reduce (fn [[sum cnt] e]
                               [(+ sum e) (inc cnt)])
                             [0 0]
                             [24 23 30]))))