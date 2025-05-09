(ns aoc2024.day02)

(defn parse-report [line]
  (->> (re-seq #"\d+" line)
       (mapv #(Long/parseLong %))))

(defn report-safe? [min-diff max-diff report]
  (let [diff1 (- (first report) (second report))]
    (->> (map - report (rest report))
         (every? #(and (> (* diff1 %) 0)
                       (<= min-diff (Math/abs %) max-diff))))))

(defn part-1 [lines]
  (->> lines
       (filter (comp (partial report-safe? 1 3) parse-report))
       count))

(defn -main [& args]
  (let [filename (first args)
        lines (clojure.string/split-lines (slurp filename))]
    (println (part-1 lines))))
