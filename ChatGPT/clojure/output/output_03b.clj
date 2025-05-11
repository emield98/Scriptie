;; ChatGPT-generated solution will go here.
(ns output_03b
  (:require [clojure.string :as str]))

(defn part [lines]
  (let [input (apply str lines)
        pattern #"(do\(\)|don't\(\)|mul\((\d{1,3}),(\d{1,3})\))"]
    (loop [matches (re-seq pattern input)
           enabled true
           acc 0]
      (if (empty? matches)
        acc
        (let [[full _ a b] (first matches)]
          (cond
            (= full "do()") (recur (rest matches) true acc)
            (= full "don't()") (recur (rest matches) false acc)
            (and enabled a b) (recur (rest matches) enabled (+ acc (* (parse-long a) (parse-long b))))
            :else (recur (rest matches) enabled acc))))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
