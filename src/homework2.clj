(ns homework2)

; 1) Total length of the sequences

(reduce (fn [acc e]
          (+ acc (count e)))
        0
        (concat ["David" "Ana" "Filip"] ["Milos" "Anja" "Marko"]))
; result => 27

; 2) Filtering a sequence to elements with length greater than three

(reduce (fn [acc e]
          (if (> (count e) 3)
            (conj acc e)
            acc))
        []
        ["David" "Ana" "Filip"])

; result => ["David" "Filip"]

; 3) Reduce as map

(reduce (fn [acc e]
          (conj acc (* 2 e)))
        []
        [1 2 3])
; result  => [2 4 6]

; 4) Reduce as filter

(reduce (fn [acc e]
          (if (> (count e) 3)
            (conj acc e)
            acc))
        []
        ["David" "Ana" "Milovan"])

; result => ["David" "Milovan"]






















