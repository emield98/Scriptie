;; ChatGPT-generated solution will go here.
(ns output_10a
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(defn parse-grid [lines]
  (mapv #(mapv (fn [ch] (Character/digit ch 10)) (seq %)) lines))

(defn neighbors [[r c] rows cols]
  (filter (fn [[nr nc]] (and (<= 0 nr) (< nr rows) (<= 0 nc) (< nc cols)))
          [[(dec r) c] [(inc r) c] [r (dec c)] [r (inc c)]]))

(defn bfs [grid start]
  (let [rows (count grid)
        cols (count (first grid))]
    (loop [queue (conj clojure.lang.PersistentQueue/EMPTY [start 0])
           visited #{start}
           ends #{}]
      (if (empty? queue)
        (count ends)
        (let [[[pos h] & rest] queue
              val (get-in grid pos)
              next-positions
              (for [nbr (neighbors pos rows cols)
                    :let [nv (get-in grid nbr)]
                    :when (and (= nv (inc val)) (not (visited nbr)))]
                [nbr nv])
              new-ends (if (= val 9) (conj ends pos) ends)]
          (recur (into rest next-positions)
                 (into visited (map first next-positions))
                 new-ends))))))

(defn part [lines]
  (let [grid (parse-grid lines)
        rows (count grid)
        cols (count (first grid))
        trailheads (for [r (range rows)
                         c (range cols)
                         :when (= 0 (get-in grid [r c]))]
                     [r c])]
    (->> trailheads
         (map #(bfs grid %))
         (reduce +))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
