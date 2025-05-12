(ns output_03b
  (:require [clojure.string :as str]))

(defn part [lines]
  (let [data (str/join "\n" lines)
        pattern #"do\(\)|don't\(\)|\bmul\((\d{1,3}),(\d{1,3})\)"]
    (loop [matches (re-seq pattern data)
           enabled true
           total 0]
      (if (empty? matches)
        total
        (let [match (first matches)
              [_ full a b] (re-matches #"(?s)(do\(\)|don't\(\)|mul\((\d{1,3}),(\d{1,3})\))" match)]
          (cond
            (= full "do()") (recur (rest matches) true total)
            (= full "don't()") (recur (rest matches) false total)
            (and a b enabled) (recur (rest matches) enabled (+ total (* (parse-long a) (parse-long b))))
            :else (recur (rest matches) enabled total)))))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
