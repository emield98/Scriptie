(ns aoc2024.day02b)

(defn parse-report [line]
  (->> (re-seq #"\d+" line)
       (mapv #(Long/parseLong %))))

(defn report-safe? [min-diff max-diff report]
  (let [diff1 (- (first report) (second report))]
    (->> (map - report (rest report))
         (every? #(and (> (* diff1 %) 0)
                       (<= min-diff (Math/abs %) max-diff))))))

(defn skip-nth [n col]
  (let [[a b] (split-at n col)]
    (concat a (rest b))))

(defn part-2 [lines]
  (->> lines
       (map parse-report)
       (filter #(some (fn [idx] (report-safe? 1 3 (skip-nth idx %)))
                      (range (count %))))
       count))

(defn -main [& args]
  (let [filename (first args)
        lines (clojure.string/split-lines (slurp filename))]
    (println (part-2 lines))))
