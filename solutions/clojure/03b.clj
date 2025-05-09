(ns aoc2024.day03b)

(defn part-2 [s]
  (loop [sq (re-seq #"mul\((\d+),(\d+)\)|do\(\)|don't\(\)" s)
         state :do
         sum 0]
    (if-let [[op arg1 arg2] (first sq)]
      (cond
        (= "do()" op) (recur (rest sq) :do sum)
        (= "don't()" op) (recur (rest sq) :dont sum)
        (= :do state) (recur (rest sq) state (+ sum (* (parse-long arg1) (parse-long arg2))))
        :else (recur (rest sq) state sum))
      sum)))

(defn -main [& args]
  (let [filename (first args)
        content (slurp filename)]
    (println (part-2 content))))
