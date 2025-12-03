(ns functions)


 (apply / (reduce (fn [[sum cnt] e]
                       [(+ sum e) (inc cnt)])
                           [0 0]
                           [10 20 30]))