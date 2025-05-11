;; ChatGPT-generated solution will go here.
(ns output_01a
  (:require [clojure.string :as str]))

(defn part [lines]
  (let [pairs (map #(mapv parse-long (str/split % #"\s+")) lines)
        [left right] (reduce (fn [[l r] [a b]] [(conj l a) (conj r b)]) [[] []] pairs)
        sorted-left (sort left)
        sorted-right (sort right)]
    (reduce + (map #(Math/abs (- %1 %2)) sorted-left sorted-right))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
