(ns aoc2024.day01b
  (:require [clojure.string :as str]))


(defn part-2 [lines]
  (let [[a b] (->> lines
                   (map #(mapv parse-long (str/split % #" +")))
                   (apply map vector))]  ; get transposed lists
    (as-> b x
      (reduce (fn [table n] (update table n #(inc (or % 0)))) {} x)
      (map #(* (x % 0) %) a)
      (reduce + x))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part-2 lines))))
