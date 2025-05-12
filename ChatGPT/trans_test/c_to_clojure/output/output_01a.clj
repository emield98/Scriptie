(ns output_01a
  (:require [clojure.string :as str]))

(defn parse-pair [line]
  (let [[a b] (map #(Integer/parseInt %) (str/split line #"\s+"))]
    [a b]))

(defn part [lines]
  (let [pairs (map parse-pair lines)
        lefts (sort (map first pairs))
        rights (sort (map second pairs))]
    (reduce + (map #(Math/abs (- %1 %2)) lefts rights))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
