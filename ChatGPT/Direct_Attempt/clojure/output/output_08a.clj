;; ChatGPT-generated solution will go here.
(ns output_08a
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

(defn midpoint [[r1 c1] [r2 c2]]
  [(quot (+ r1 r2) 2) (quot (+ c1 c2) 2)])

(defn delta [[r1 c1] [r2 c2]]
  [(- r2 r1) (- c2 c1)])

(defn add-pos [[r c] [dr dc]]
  [(+ r dr) (+ c dc)])

(defn part [lines]
  (let [grid (mapv vec lines)
        antennas (parse-antennas grid)
        by-freq (group-by first antennas)
        antinode-positions
        (->> by-freq
             (mapcat (fn [[_ freqs]]
                       (let [positions (map second freqs)]
                         (for [[a b] (for [i (range (count positions))
                                           j (range (inc i) (count positions))]
                                       [(nth positions i) (nth positions j)])
                               :let [mid (midpoint a b)
                                     d (delta a b)
                                     a2 (add-pos mid d)
                                     b2 (add-pos mid (map - d))]
                               :when (= (add-pos a2 b2) (map + a b))]
                           (filter #(in-bounds? % grid) [a2 b2])))))
             (apply set/union))]
    (count antinode-positions)))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
