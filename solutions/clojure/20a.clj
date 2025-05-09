(ns aoc2024.day20
  (:require
   [clojure.string :as str]
   [clojure.core.matrix :as m]))

(defn mat-find-all
  [mat a]
  (for [[r c] (m/index-seq mat)
        :when (= a (m/mget mat r c))]
    [r c]))

(defn read-lines
  [f]
  (->> f
       slurp
       (str/split-lines)))


(defn dims [grid]
  [(count grid) (count (first grid))])

(defn read-data [f]
  (->> f read-lines (mapv #(str/split % #""))))

(defn neighbours [grid [r c]]
  (filter (fn [[r' c']]
            (not= "#" (get-in grid [r' c'])))
          [[(dec r) c] [(inc r) c] [r (dec c)] [r (inc c)]]))

(defn traverse-track [grid]
  (let [start (first (mat-find-all grid "S"))
        end (first (mat-find-all grid "E"))]
    (loop [current start visited #{} path [] i 0]
      (if (= current end)
        (conj path current)
        (let [nn (neighbours grid current)
              next-rc (first (filter #(not (visited %)) nn))]
          (if (or (> i 10000) (nil? next-rc))
            path
            (recur next-rc (conj visited current) (conj path current) (inc i))))))))

(defn adjacent? [grid [r c] [dr dc]]
  (let [[rmax cmax] (dims grid)]
    (and (= "#" (get-in grid [(+ r dr) (+ c dc)]))
         (<= 0 (+ r dr dr) (dec rmax))
         (<= 0 (+ c dc dc) (dec cmax))
         (not= "#" (get-in grid [(+ r dr dr) (+ c dc dc)])))))

(defn adjacents [grid [r c]]
  (let [dirs [[-1 0] [0 -1] [0 1] [1 0]]]
    (->> dirs
         (filter (partial adjacent? grid [r c]))
         (map #(mapv + [r c] % %)))))

(defn path-saving [path current adjacent]
  (let [i (.indexOf path current)
        j (.indexOf path adjacent)]
    (- j i 2)))

(defn find-adjacents [grid path [r c]]
  (let [aa (adjacents grid [r c])]
    (map (partial path-saving path [r c]) aa)))

(defn part1 [n f]
  (let [grid (read-data f)
        path (traverse-track grid)]
    (->> path
         (map (partial find-adjacents grid path))
         flatten
         (filter #(>= % n))
         frequencies
         vals
         (reduce +))))

(defn -main [& [file n]]
  (let [n (Integer/parseInt (or n "100"))
        file (or file "input")]
    (println (part1 n file))))