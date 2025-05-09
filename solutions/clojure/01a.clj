(ns aoc2024.day01
  (:require [clojure.string :as str]))

(defn part-1 [lines]
  (->> lines
       (map #(mapv parse-long (str/split % #" +")))
       (apply map (comp sort vector))
       (apply map (comp abs -))
       (reduce +)))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part-1 lines))))
