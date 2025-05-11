;; ChatGPT-generated solution will go here.
(ns output_25a
  (:require [clojure.string :as str]))

(defn parse-schematics [lines]
  (let [groups (->> lines
                    (partition-by str/blank?)
                    (remove #(= '("") %)))]
    (map #(apply map vector %) groups))) ; transpose to get columns

(defn schematic-height [row-selector grid]
  (map (fn [col]
         (count (take-while row-selector col)))
       grid))

(defn lock-heights [grid]
  (schematic-height #(= \# %) grid))

(defn key-heights [grid]
  (schematic-height #(= \# %) (map reverse grid)))

(defn part [lines]
  (let [schematics (parse-schematics lines)
        [locks keys] (split-with #(= \# (ffirst %)) schematics)
        locks (map lock-heights locks)
        keys (map key-heights keys)
        total-height (count (first (first schematics)))
        valid-pairs (for [l locks
                          k keys
                          :when (every? #(< % total-height) (map + l k))]
                      [l k])]
    (count valid-pairs)))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
