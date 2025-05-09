(ns aoc2024.day03)

(def MULEXP #"mul\((\d+),(\d+)\)")

(defn extract-muls [s]
  (->> (re-seq MULEXP s)
       (map (comp #(map parse-long %) rest))))

(defn part-1 [s]
  (->> s
       extract-muls
       (map #(reduce * 1 %))
       (reduce +)))

(defn -main [& args]
  (let [filename (first args)
        content (slurp filename)]
    (println (part-1 content))))
