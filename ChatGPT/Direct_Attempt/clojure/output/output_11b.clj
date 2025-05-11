;; ChatGPT-generated solution will go here.
(ns output_11b
  (:require [clojure.string :as str]))

(defn parse-input [lines]
  (->> lines first (str/split #" ") (mapv bigint)))

(defn transform-stone [n]
  (cond
    (= n 0) [1N]
    (even? (count (str n)))
    (let [s (str n)
          mid (quot (count s) 2)
          l (subs s 0 mid)
          r (subs s mid)]
      (mapv bigint [l r]))
    :else [(* n 2024N)]))

(defn blink [stones]
  (mapcat transform-stone stones))

(defn part [lines]
  (let [initial (parse-input lines)
        final (nth (iterate blink initial) 75)]
    (count final)))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
