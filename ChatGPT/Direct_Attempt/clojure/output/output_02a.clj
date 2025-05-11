;; ChatGPT-generated solution will go here.
(ns output_02a
  (:require [clojure.string :as str]))

(defn safe-report? [nums]
  (let [deltas (map - (rest nums) nums)
        signs (set (map compare deltas (repeat 0)))]
    (and (or (= signs #{1}) (= signs #{-1}))
         (every? #(<= 1 (Math/abs %) 3) deltas))))

(defn part [lines]
  (->> lines
       (map #(map parse-long (str/split % #"\s+")))
       (filter safe-report?)
       count))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
