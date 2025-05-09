(ns aoc2024.day20b
  (:require [clojure.string :as str])
  (:import (clojure.lang PersistentQueue PersistentVector)))

(set! *warn-on-reflection* true)
(set! *unchecked-math* true)

(defrecord vec2 [^long row ^long col])
(defn ->vec2 [^long r ^long c] (vec2. r c))

(defn lines->grid [lines]
  (vec (mapv vec lines)))

(defn grid-height [grid] (count grid))
(defn grid-width [grid] (count (first grid)))

(defn grid-val [^PersistentVector grid ^vec2 v]
  (let [{:keys [row col]} v]
    (get-in grid [row col])))

(defn first-coord-of [grid pred]
  (let [h (grid-height grid)
        w (grid-width grid)]
    (first (for [r (range h)
                 c (range w)
                 :let [v (->vec2 r c)]
                 :when (pred (grid-val grid v))]
             v))))

(defn neighbors4 [^vec2 v]
  (let [{:keys [row col]} v]
    [(->vec2 (dec row) col)
     (->vec2 (inc row) col)
     (->vec2 row (dec col))
     (->vec2 row (inc col))]))

(defn plain-neighbors [grid ^vec2 v]
  (filter #(= \. (grid-val grid %)) (neighbors4 v)))

(defn bfs [start goal? neighbors]
  (loop [queue (conj PersistentQueue/EMPTY start)
         visited {start nil}]
    (if (empty? queue)
      {:prevs visited}
      (let [current (peek queue)]
        (if (goal? current)
          {:prevs visited}
          (let [nbrs (remove visited (neighbors current))]
            (recur (into (pop queue) nbrs)
                   (reduce #(assoc %1 %2 current) visited nbrs))))))))

(defn trace-path [prevs dst src]
  (loop [v dst acc []]
    (if (= v src)
      (conj acc v)
      (recur (prevs v) (conj acc v)))))

(def MAX-COL 1000)

(defn hash-vec2 [^vec2 v]
  (+ (:col v) (* MAX-COL (:row v))))

(defn ht-set [^longs arr ^vec2 v ^long val]
  (aset-long arr (hash-vec2 v) val)
  arr)

(defn ht-get [^longs arr ^vec2 v]
  (aget arr (hash-vec2 v)))

(defn circle-coords [^vec2 center ^long radius h w]
  (let [{:keys [row col]} center]
    (for [r (range (- row radius) (inc (+ row radius)))
          :let [d (Math/abs ^long (- r row))
                min-c (- col (- radius d))
                max-c (+ col (- radius d))] 
          c (range min-c (inc max-c))
          :when (and (>= r 0) (< r h) (>= c 0) (< c w))]
      (->vec2 r c))))

(defn num-cheats-from [min-save max-dist h w path-lens ^vec2 cur]
  (let [cur-len (ht-get path-lens cur)]
    (->> (circle-coords cur max-dist h w)
         (remove #(= -1 (ht-get path-lens %)))
         (keep (fn [v]
                 (let [target-len (ht-get path-lens v)
                       dr (Math/abs ^long (- (:row cur) (:row v)))
                       dc (Math/abs ^long (- (:col cur) (:col v)))
                       delta (- cur-len target-len dr dc)]
                   (when (>= delta min-save)
                     delta))))
         count)))

(defn part-2 [lines]
  (let [grid (lines->grid lines)
        start (first-coord-of grid #(= \S %))
        end   (first-coord-of grid #(= \E %))
        grid  (assoc-in (assoc-in grid [(:row start) (:col start)] \.)
                        [(:row end)   (:col end)] \.)
        path  (-> (bfs start #(= end %) (partial plain-neighbors grid))
                  :prevs
                  (trace-path end start))
        h (grid-height grid)
        w (grid-width grid)
        path-lens (reduce (fn [arr [v i]] (ht-set arr v i))
                          (long-array (* MAX-COL MAX-COL) -1)
                          (map vector path (range)))]
    (->> path
         (pmap #(num-cheats-from 100 20 h w path-lens %))
         (reduce +))))

(defn -main [& [file]]
  (let [lines (-> (or file "input") slurp str/split-lines)]
    (println (part-2 lines))
    (System/exit 0))) ; ensure script terminates cleanly
