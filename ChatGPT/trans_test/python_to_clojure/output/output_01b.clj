(ns output_01b
  (:require [clojure.string :as str]))

(defn part [lines]
  (let [pairs (->> lines
                   (filter (complement str/blank?))
                   (map #(map parse-long (str/split % #"\s+"))))
        left (map first pairs)
        right (map second pairs)
        right-counts (frequencies right)]
    (reduce + (map #(let [cnt (get right-counts % 0)] (* % cnt)) left))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
