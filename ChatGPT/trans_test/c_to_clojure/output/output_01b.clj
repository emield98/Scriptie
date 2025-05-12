(ns output_01b
  (:require [clojure.string :as str]))

(defn parse-pair [line]
  (let [[a b] (map #(Integer/parseInt %) (str/split line #"\s+"))]
    [a b]))

(defn part [lines]
  (let [pairs (map parse-pair lines)
        lefts (map first pairs)
        rights (frequencies (map second pairs))]
    (reduce + (map (fn [x] (* x (get rights x 0))) lefts))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
