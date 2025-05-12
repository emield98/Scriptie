(ns output_11b
  (:require [clojure.string :as str]))

(defn transform-counts [counts]
  (reduce-kv
   (fn [next-counts num cnt]
     (cond
       (= num 0)
       (update next-counts 1 (fnil + 0) cnt)

       (even? (count (str num)))
       (let [s (str num)
             mid (quot (count s) 2)
             left (Long/parseLong (subs s 0 mid))
             right (Long/parseLong (subs s mid))]
         (-> next-counts
             (update left (fnil + 0) cnt)
             (update right (fnil + 0) cnt)))

       :else
       (update next-counts (* num 2024) (fnil + 0) cnt)))
   {}
   counts))

(defn part [lines]
  (let [initial (map #(Long/parseLong %) (str/split (str/trim (first lines)) #"\s+"))
        initial-counts (frequencies initial)
        final-counts (nth (iterate transform-counts initial-counts) 75)]
    (reduce + (vals final-counts))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
