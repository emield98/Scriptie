;; ChatGPT-generated solution will go here.
(ns output_19a
  (:require [clojure.string :as str]))

(defn parse-input [lines]
  (let [[patterns-line _ & designs] lines
        patterns (str/split patterns-line #",\s*")]
    {:patterns patterns
     :designs designs}))

(defn can-form? [patterns design]
  (let [n (count design)
        pattern-set (set patterns)
        dp (boolean-array (inc n))]
    (aset dp 0 true)
    (doseq [i (range 1 (inc n))]
      (doseq [j (range (max 0 (- i 10)) i)]
        (when (and (aget dp j)
                   (pattern-set (subs design j i)))
          (aset dp i true))))
    (aget dp n)))

(defn part [lines]
  (let [{:keys [patterns designs]} (parse-input lines)]
    (count (filter #(can-form? patterns %) designs))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
