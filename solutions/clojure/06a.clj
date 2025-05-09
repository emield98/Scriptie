(ns aoc2024.day06
  (:require [clojure.string :as str]))

;; --- Minimal vec2 + Grid setup ---
(defrecord vec2 [^long row ^long col])
(defn ->vec2 [r c] (vec2. r c))
(defn move [dir n ^vec2 pos]
  (let [delta (case dir
                :up    (->vec2 -1 0)
                :down  (->vec2 1 0)
                :left  (->vec2 0 -1)
                :right (->vec2 0 1))]
    (->vec2 (+ (:row pos) (* n (:row delta)))
            (+ (:col pos) (* n (:col delta)))))
  )
(defn turn-right [dir]
  (case dir
    :up :right
    :right :down
    :down :left
    :left :up))

(deftype Grid [arr ^long height ^long width]
  clojure.lang.Associative
  (containsKey [_ k] (let [r (:row k) c (:col k)] (and (>= r 0) (< r height) (>= c 0) (< c width))))
  (entryAt [_ k] (let [i (+ (:col k) (* width (:row k)))] (clojure.lang.MapEntry. k (.nth arr i))))
  (assoc [_ k v] (let [i (+ (:col k) (* width (:row k)))] (Grid. (.assoc arr i v) height width)))
  (valAt [_ k] (let [i (+ (:col k) (* width (:row k)))] (.nth arr i)))
  (valAt [this k default] (if (.containsKey this k) (.valAt this k) default))
  clojure.lang.Seqable
  (seq [_] (for [r (range height) c (range width)]
             (let [k (->vec2 r c)
                   i (+ c (* r width))]
               (clojure.lang.MapEntry. k (.nth arr i)))))
  clojure.lang.IFn
  (invoke [this k] (.valAt this k))
  (invoke [this k default] (.valAt this k default)))

(defn lines->grid [lines]
  (let [height (count lines)
        width (count (first lines))]
    (Grid. (into [] (apply concat lines)) height width)))

(defn first-coord-of [^Grid grid pred]
  (some (fn [[k v]] (when (pred v) k)) grid))

;; --- Core logic ---
(defn neighbor [^Grid grid [^vec2 coord dir]]
  (let [next-coord (move dir 1 coord)]
    (cond
      (not (contains? grid next-coord)) nil
      (= \# (grid next-coord)) [coord (turn-right dir)]
      :else [next-coord dir])))

(defn nodes-visited [^Grid grid ^vec2 start]
  (loop [node [start :up]
         visited? {start :up}]
    (let [neigh (neighbor grid node)]
      (if neigh
        (recur neigh (update visited? (first neigh) #(or % (second neigh))))
        visited?))))

(defn part-1 [lines]
  (let [grid (lines->grid lines)
        start (first-coord-of grid #(= % \^))]
    (count (nodes-visited grid start))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part-1 lines))))
