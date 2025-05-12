(ns output_04a
  (:require [clojure.string :as str]))

(defn part [lines]
  (let [grid (vec (filter (complement str/blank?) lines))
        word "XMAS"
        rows (count grid)
        cols (count (first grid))
        directions [[-1 -1] [-1 0] [-1 1]
                    [0 -1]         [0 1]
                    [1 -1] [1 0]   [1 1]]
        in-bounds? (fn [r c] (and (<= 0 r (dec rows)) (<= 0 c (dec cols))))]
    (reduce +
            (for [r (range rows)
                  c (range cols)
                  [dr dc] directions
                  :let [indices (map #(vector (+ r (* dr %)) (+ c (* dc %))) (range 4))]
                  :when (every? (fn [[i j]]
                                  (and (in-bounds? i j)
                                       (= (get-in grid [i j]) (nth word (.indexOf indices [i j])))))
                                indices)]
              1))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
