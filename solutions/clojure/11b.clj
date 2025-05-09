(ns aoc2024.day11b
  (:require [clojure.string :as str]))

;; ## Transition Function (to handle stone transitions)
(defn transition [stone]
  (cond
    (zero? stone) [1]  ;; If stone is 0, transition to [1]
    (even? (count (str stone)))  ;; If number of digits is even
    (let [s (str stone)
          len (count s)
          pivot (quot len 2)]  ;; Split the stone into two parts
      (map parse-long [(subs s 0 pivot) (subs s pivot len)])) 
    :else [(* 2024 stone)]))  ;; Else multiply by 2024

;; ## Part-2 Logic
(defn blink [stones]
  (->> stones
       (mapcat (fn [[label num]] (map #(vector % num) (transition label))))  ;; Apply transitions
       (reduce #(update %1 (first %2) (fn [x] (+' (or x 0) (second %2)))) {})))  ;; Aggregate the results

(defn part-2 [s]
  (let [initial-stones (->> s
                            (mapcat #(re-seq #"\d+" %))  ;; Extract numbers from all lines
                            (map parse-long)  ;; Parse numbers into integers
                            (reduce #(update %1 %2 (fn [x] (inc (or x 0)))) {}))  ;; Initialize the stones
        bs (iterate blink initial-stones)]  ;; Iterate the blinking process
    (as-> bs $
      (nth $ 75)   ;; Get the state after 75 iterations
      (vals $)     ;; Get the values
      (reduce + $))))  ;; Sum the values

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part-2 lines))))
