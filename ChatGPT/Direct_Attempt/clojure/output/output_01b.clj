;; ChatGPT-generated solution will go here.
(ns output_01b
  (:require [clojure.string :as str]))

(defn part [lines]
  (let [pairs (map #(mapv parse-long (str/split % #"\s+")) lines)
        [left right] (reduce (fn [[l r] [a b]] [(conj l a) (conj r b)]) [[] []] pairs)
        freq-right (frequencies right)]
    (reduce + (map #(let [cnt (get freq-right % 0)] (* % cnt)) left))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
