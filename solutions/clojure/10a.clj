(ns aoc2024.day10
  (:require [clojure.string :as str])
  (:import [java.util ArrayDeque]))

;; --- vec2 and Grid setup ---
(defrecord vec2 [^long row ^long col]
  clojure.lang.Indexed
  (nth [_ i] (case i 0 row 1 col))
  (nth [_ i default] (case i 0 row 1 col default))
  Comparable
  (compareTo [_ other]
    (let [c (compare row (.row ^vec2 other))]
      (if (zero? c) (compare col (.col ^vec2 other)) c))))
(defn ->vec2 [r c] (vec2. r c))
(defn vec2+ [a b] (->vec2 (+ (:row a) (:row b)) (+ (:col a) (:col b))))
(defn vec2- [a b] (->vec2 (- (:row a) (:row b)) (- (:col a) (:col b))))

(def dirs4 [:up :right :down :left])
(defn dir->delta [d]
  (case d
    :up (->vec2 -1 0)
    :right (->vec2 0 1)
    :down (->vec2 1 0)
    :left (->vec2 0 -1)))
(defn neighbors4 [p]
  (map #(vec2+ p (dir->delta %)) dirs4))

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

(defn all-coords-of [^Grid g pred]
  (->> (seq g)
       (filter #(pred (val %)))
       (map key)
       vec))

;; --- searches.clj (BFS only) ---
(defn nil-visit ([] nil) ([_ _] nil-visit))
(defn conj-visit [init] (fn ([] init) ([_ x] (conj-visit (conj init x)))))
(defn bfs
  ([nbrs start stop?] (bfs nbrs start stop? nil-visit identity))
  ([nbrs start stop? visit] (bfs nbrs start stop? visit identity))
  ([nbrs start stop? visit node-keyfn]
   (loop [q (doto (ArrayDeque.) (.push start))
          prevs (transient {(node-keyfn start) start})
          visit visit]
     (if-let [cur (and (not (.isEmpty q)) (.pop q))]
       (if (stop? cur)
         {:prevs (persistent! prevs) :last cur :visit (visit prevs cur)}
         (let [neighs (->> (nbrs cur) (filterv #(not (contains? prevs (node-keyfn %)))))
               new-prevs (reduce #(assoc! %1 (node-keyfn %2) cur) prevs neighs)]
           (recur (reduce (fn [qq n] (doto qq (.add n))) q neighs)
                  new-prevs
                  (visit prevs cur))))
       {:prevs (persistent! prevs) :visit visit}))))

(defn visit-smarter [nbrs coords]
  (fn ([] coords)
    ([_ cur]
     (let [cur-count (coords cur)]
       (visit-smarter
        nbrs
        (reduce #(update %1 %2 (fn [c] (+ (or c 0) cur-count)))
                coords
                (nbrs cur)))))))

(defn call [f] (f))

;; --- Problem-specific logic ---
(defn parse-input [lines]
  (lines->grid (map #(map (fn [^Character c] (Character/digit c 10)) %) lines)))

(defn neighbors [^Grid grid pos]
  (let [cur-height (grid pos)]
    (if (= 9 cur-height)
      []
      (filterv #(and (contains? grid %) (= (inc cur-height) (grid %))) (neighbors4 pos)))))

(defn trail-score [^Grid grid neighfunc pos-indexer start]
  (->> (bfs neighfunc start (constantly false))
       :prevs
       keys
       (filter #(= 9 (grid (pos-indexer %))))
       count))

(defn part-1 [lines]
  (let [grid (parse-input lines)]
    (->> (all-coords-of grid zero?)
         (map #(trail-score grid (partial neighbors grid) identity %))
         (reduce +))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part-1 lines))))
