(ns output_02b
  (:require [clojure.string :as str]))

(defn is-safe? [report]
  (let [diffs (map - (rest report) report)]
    (or (every? #(<= 1 % 3) diffs)
        (every? #(<= -3 % -1) diffs))))

(defn is-safe-with-dampener? [report]
  (some true?
        (for [i (range (count report))]
          (let [reduced (concat (subvec report 0 i) (subvec report (inc i)))]
            (is-safe? reduced)))))

(defn part [lines]
  (count
    (filter identity
            (for [line lines
                  :when (not (str/blank? line))
                  :let [levels (vec (map parse-long (str/split line #"\s+")))]]
              (or (is-safe? levels)
                  (is-safe-with-dampener? levels))))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
