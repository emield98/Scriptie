(ns aoc2024.day19b
  (:require [clojure.string :as str]))

;; --- Inlined utils ---
(defn to-blocks [input]
  (str/split input #"\n\n"))

(defn to-lines [input]
  (str/split-lines input))

;; --- Core logic for day19b ---
(def count-towel-combinations
  (memoize
   (fn [towels pattern]
     (if (empty? pattern)
       1
       (->> towels
            (keep #(when (str/starts-with? pattern %)
                     (subs pattern (count %))))
            (map (partial count-towel-combinations towels))
            (apply +))))))

(defn part-2 [input]
  (let [blocks (to-blocks input)
        towels (set (str/split (first blocks) #", "))
        patterns (to-lines (last blocks))]
    (->> patterns
         (map (partial count-towel-combinations towels))
         (reduce +))))

(defn -main [& [file]]
  (println (part-2 (slurp (or file "input")))))
