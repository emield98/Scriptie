(ns output_11a
  (:require [clojure.string :as str]))

(defn transform [num]
  (cond
    (= num 0) [1]
    (even? (count (str num)))
    (let [s (str num)
          mid (quot (count s) 2)
          left (Long/parseLong (subs s 0 mid))
          right (Long/parseLong (subs s mid))]
      [left right])
    :else [(* num 2024)]))

(defn part [lines]
  (let [initial-stones (mapv #(Long/parseLong %) (str/split (str/trim (first lines)) #"\s+"))
        final-stones (loop [stones initial-stones, step 0]
                       (if (= step 25)
                         stones
                         (recur (mapcat transform stones) (inc step))))]
    (count final-stones)))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
