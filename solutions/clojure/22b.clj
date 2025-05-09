(ns aoc2024.day22b
  (:require [clojure.string :as str]))

(set! *warn-on-reflection* true)
(set! *unchecked-math* true)

(def dec-modulus ^long (dec 16777216))

(defn next-secret ^long [^long n]
  (let [a (-> n (bit-shift-left 6) (bit-xor n) (bit-and dec-modulus))
        b (-> a (bit-shift-right 5) (bit-xor a) (bit-and dec-modulus))
        c (-> b (bit-shift-left 11) (bit-xor b) (bit-and dec-modulus))]
    c))

(defn gen-prices [seed]
  (->> (iterate next-secret seed)
       (drop 1)
       (take 2001)
       (map #(mod % 10))
       vec))

(defn diffs [prices]
  (mapv - (subvec prices 1) (subvec prices 0 2000)))

(defn best-pattern [lines]
  (->> lines
       (mapv parse-long)
       (mapv (fn [seed]
               (let [prices (gen-prices seed)
                     ds (diffs prices)]
                 (loop [i 0 seen {}]
                   (if (> (+ i 3) (dec (count ds)))
                     seen
                     (let [pat (subvec ds i (+ i 4))]
                       (if (contains? seen pat)
                         (recur (inc i) seen)
                         (recur (inc i) (assoc seen pat (nth prices (+ i 4)))))))))))
       (apply merge-with +)
       (vals)
       (apply max)))

(defn -main [& [file]]
  (let [lines (-> (or file "input") slurp str/split-lines)]
    (println (best-pattern lines))))
