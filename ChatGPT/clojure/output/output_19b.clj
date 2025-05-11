;; ChatGPT-generated solution will go here.
(ns output_19b
  (:require [clojure.string :as str]))

(defn parse-input [lines]
  (let [[patterns-line _ & designs] lines
        patterns (str/split patterns-line #",\s*")]
    {:patterns patterns
     :designs designs}))

(defn count-ways [patterns design]
  (let [n (count design)
        pattern-set (set patterns)
        dp (long-array (inc n) 0)]
    (aset dp 0 1)
    (doseq [i (range 1 (inc n))]
      (doseq [j (range (max 0 (- i 10)) i)]
        (when (pattern-set (subs design j i))
          (aset dp i (+ (aget dp i) (aget dp j))))))
    (aget dp n)))

(defn part [lines]
  (let [{:keys [patterns designs]} (parse-input lines)]
    (reduce + (map #(count-ways patterns %) designs))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
