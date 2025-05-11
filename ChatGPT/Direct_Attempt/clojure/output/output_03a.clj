;; ChatGPT-generated solution will go here.
(ns output_03a
  (:require [clojure.string :as str]))

(defn part [lines]
  (let [input (apply str lines)
        pattern #"mul\((\d{1,3}),(\d{1,3})\)"]
    (->> (re-seq pattern input)
         (map (fn [[_ a b]] (* (parse-long a) (parse-long b))))
         (reduce +))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
