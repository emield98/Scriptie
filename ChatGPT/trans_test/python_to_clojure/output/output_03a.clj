(ns output_03a
  (:require [clojure.string :as str]))

(defn part [lines]
  (let [data (str/join "\n" lines)
        matches (re-seq #"(?i)\bmul\((\d{1,3}),(\d{1,3})\)" data)]
    (reduce +
            (map (fn [[_ a b]]
                   (* (parse-long a) (parse-long b)))
                 matches))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
