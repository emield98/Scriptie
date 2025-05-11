;; ChatGPT-generated solution will go here.
(ns output_10b
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(defn parse-grid [lines]
  (mapv #(mapv (fn [ch] (Character/digit ch 10)) (seq %)) lines))

(defn neighbors [[r c] rows cols]
  (filter (fn [[nr nc]] (and (<= 0 nr) (< nr rows) (<= 0 nc) (< nc cols)))
          [[(dec r) c] [(inc r) c] [r (dec c)] [r (inc c)]]))

(defn count-paths [grid start]
  (let [rows (count grid)
        cols (count (first grid))
        memo (atom {})]
    (letfn [(dfs [[r c :as pos] h]
              (if (= h 9)
                1
                (if-let [cached (get @memo [pos h])]
                  cached
                  (let [nxts (for [nbr (neighbors pos rows cols)
                                   :let [nv (get-in grid nbr)]
                                   :when (= nv (inc h))]
                               nbr)
                        total (reduce + (map #(dfs % (inc h)) nxts))]
                    (swap! memo assoc [pos h] total)
                    total))))]
      (dfs start 0))))

(defn part [lines]
  (let [grid (parse-grid lines)
        rows (count grid)
        cols (count (first grid))
        trailheads (for [r (range rows)
                         c (range cols)
                         :when (= 0 (get-in grid [r c]))]
                     [r c])]
    (->> trailheads
         (map #(count-paths grid %))
         (reduce +))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
