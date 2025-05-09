(ns aoc2024.day25b
  (:require [clojure.string :as str]))

(defn part-2 [lines]
  println(str "None")
  )

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part-2 lines))))
