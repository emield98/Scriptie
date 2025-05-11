;; ChatGPT-generated solution will go here.
(ns output_07a
  (:require [clojure.string :as str]))

(defn parse-line [line]
  (let [[lhs rhs] (str/split line #": ")
        target (parse-long lhs)
        nums (map parse-long (str/split rhs #" "))]
    [target nums]))

(defn all-operator-combos [n]
  (if (zero? n)
    [[]]
    (for [op (list + *)
          rest (all-operator-combos (dec n))]
      (cons op rest))))

(defn eval-left-to-right [nums ops]
  (reduce (fn [acc [op n]] (op acc n))
          (first nums)
          (map vector ops (rest nums))))

(defn valid-equation? [target nums]
  (let [ops-count (dec (count nums))]
    (some #(= target (eval-left-to-right nums %))
          (all-operator-combos ops-count))))

(defn part [lines]
  (->> lines
       (map parse-line)
       (filter (fn [[target nums]] (valid-equation? target nums)))
       (map first)
       (reduce +)))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
