(ns aoc2024.day11
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

;; ## Part-1 Logic
(defn part-1 [s]
  (let [initial-stones (->> s
                            (mapcat #(re-seq #"\d+" %))  ;; Extract numbers from all lines
                            (map parse-long))  ;; Parse numbers into integers
        bs (iterate #(mapcat transition %) initial-stones)]  ;; Create an infinite sequence of transitions
    (-> bs (nth 25) count)))  ;; Count the number of stones after 25 iterations


;; ## Main function
(defn -main [& args]
  (let [filename (first args)  ;; Get the input file name
        lines (str/split-lines (slurp filename))]  ;; Read the input file
    (println (part-1 lines))))