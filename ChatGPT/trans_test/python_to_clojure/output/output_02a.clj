(ns output_02a
  (:require [clojure.string :as str]))

(defn is-safe? [report]
  (let [diffs (map - (rest report) report)]
    (or (every? #(<= 1 % 3) diffs)
        (every? #(<= -3 % -1) diffs))))

(defn part [lines]
  (count
    (filter identity
            (for [line lines
                  :when (not (str/blank? line))
                  :let [levels (map parse-long (str/split line #"\s+"))]]
              (is-safe? levels)))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
