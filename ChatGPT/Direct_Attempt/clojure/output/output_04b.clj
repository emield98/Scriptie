;; ChatGPT-generated solution will go here.
(ns output_04b
  (:require [clojure.string :as str]))

(defn get-char [grid r c]
  (get-in grid [r c]))

(defn valid-xmas? [grid r c]
  (let [nw [(get-char grid (dec r) (dec c))
            (get-char grid r c)
            (get-char grid (inc r) (inc c))]
        ne [(get-char grid (dec r) (inc c))
            (get-char grid r c)
            (get-char grid (inc r) (dec c))]
        valid-mas? (fn [[a b c]]
                     (or (= [a b c] [\M \A \S])
                         (= [a b c] [\S \A \M])))]
    (and (every? some? nw)
         (every? some? ne)
         (valid-mas? nw)
         (valid-mas? ne))))

(defn part [lines]
  (let [grid (mapv vec lines)
        rows (count grid)
        cols (count (first grid))]
    (count
     (for [r (range 1 (dec rows))
           c (range 1 (dec cols))
           :when (valid-xmas? grid r c)]
       [r c]))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
