;; ChatGPT-generated solution will go here.
(ns output_08b
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(defn parse-antennas [lines]
  (let [rows (count lines)
        cols (count (first lines))]
    (for [r (range rows)
          c (range cols)
          :let [ch (get-in lines [r c])]
          :when (not= ch \.)]
      [ch [r c]])))

(defn in-bounds? [[r c] lines]
  (and (<= 0 r) (< r (count lines))
       (<= 0 c) (< c (count (first lines)))))

(defn collinear? [[x1 y1] [x2 y2] [x3 y3]]
  (zero? (- (* (- y2 y1) (- x3 x1)) (* (- y3 y1) (- x2 x1)))))

(defn part [lines]
  (let [grid (mapv vec lines)
        antennas (parse-antennas grid)
        by-freq (group-by first antennas)
        antinodes
        (reduce
         (fn [s [_ freq-ants]]
           (let [positions (map second freq-ants)
                 base (set (if (> (count positions) 1) positions []))
                 linesets
                 (for [[a b] (for [i (range (count positions))
                                   j (range (inc i) (count positions))]
                               [(nth positions i) (nth positions j)])]
                   (set
                    (filter #(in-bounds? % grid)
                            (filter #(collinear? a b %) positions))))]
             (apply set/union s base linesets)))
         #{}
         by-freq)]
    (count antinodes)))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
