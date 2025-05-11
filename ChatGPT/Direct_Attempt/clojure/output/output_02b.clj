;; ChatGPT-generated solution will go here.
(ns output_02b
  (:require [clojure.string :as str]))

(defn safe-report? [nums]
  (let [deltas (map - (rest nums) nums)
        signs (set (map compare deltas (repeat 0)))]
    (and (or (= signs #{1}) (= signs #{-1}))
         (every? #(<= 1 (Math/abs %) 3) deltas))))

(defn dampened-safe? [nums]
  (some safe-report? (map #(vec (concat (subvec nums 0 %) (subvec nums (inc %)))) (range (count nums)))))

(defn part [lines]
  (->> lines
       (map #(vec (map parse-long (str/split % #"\s+"))))
       (filter #(or (safe-report? %) (dampened-safe? %)))
       count))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
