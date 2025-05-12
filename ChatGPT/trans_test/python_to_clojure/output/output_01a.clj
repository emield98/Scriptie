(ns output_01a
  (:require [clojure.string :as str]))

(defn part [lines]
  (let [pairs (->> lines
                   (filter (complement str/blank?))
                   (map #(map parse-long (str/split % #"\s+"))))
        left (sort (map first pairs))
        right (sort (map second pairs))]
    (reduce + (map #(Math/abs ^long (- %1 %2)) left right))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
