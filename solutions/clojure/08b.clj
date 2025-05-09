(ns aoc2024.day08b
  (:require [clojure.string :as str]
            [clojure.set :as set]))

;; --- Inlined vec2 and Grid logic ---
(defrecord vec2 [^long row ^long col]
  clojure.lang.Indexed
  (nth [_ i] (case i 0 row 1 col))
  (nth [_ i default] (case i 0 row 1 col default))

  Comparable
  (compareTo [_ other]
    (let [c (compare row (.row ^vec2 other))]
      (if (zero? c) (compare col (.col ^vec2 other)) c))))

(defn ->vec2 [row col] (vec2. row col))
(defn vec2+ [a b] (->vec2 (+ (:row a) (:row b)) (+ (:col a) (:col b))))
(defn vec2- [a b] (->vec2 (- (:row a) (:row b)) (- (:col a) (:col b))))

(defn gcd [a b]
  (if (zero? b) a (recur b (mod a b))))

(deftype Grid [^clojure.lang.IPersistentVector arr ^long height ^long width]
  clojure.lang.Counted
  (count [_] (.count arr))
  clojure.lang.Associative
  (containsKey [_ k]
    (and (>= (:row k) 0) (< (:row k) height)
         (>= (:col k) 0) (< (:col k) width)))
  (entryAt [_ k]
    (let [idx (+ (:col k) (* width (:row k)))]
      (clojure.lang.MapEntry. k (.nth arr idx))))
  (assoc [_ k v]
    (let [idx (+ (:col k) (* width (:row k)))]
      (Grid. (.assoc arr idx v) height width)))
  (valAt [_ k]
    (let [idx (+ (:col k) (* width (:row k)))]
      (.nth arr idx)))
  (valAt [this k default]
    (if (.containsKey this k) (.valAt this k) default))
  clojure.lang.Seqable
  (seq [_]
    (for [row (range height) col (range width)]
      (let [k (->vec2 row col)
            idx (+ col (* row width))]
        (clojure.lang.MapEntry. k (.nth arr idx)))))
  clojure.lang.IFn
  (invoke [this k] (.valAt this k))
  (invoke [this k default] (.valAt this k default)))

(defn lines->grid [lines]
  (let [height (count lines)
        width (count (first lines))]
    (assert (every? #(= width (count %)) lines))
    (Grid. (into [] (apply concat lines)) height width)))

(defn all-coords-of [^Grid g p]
  (persistent!
   (reduce (fn [acc [k v]] (if (p v) (conj! acc k) acc))
           (transient [])
           (seq g))))

;; --- Problem-specific logic for Day 8b ---
(defn collect-antennae [^Grid grid]
  (->> (all-coords-of grid #(not= \. %))
       (group-by grid)))

(defn coords-in-line [^Grid grid ^vec2 a ^vec2 b]
  (let [dr (- (:row b) (:row a))
        dc (- (:col b) (:col a))
        denom (if (or (zero? dr) (zero? dc))
                (max (Math/abs dr) (Math/abs dc))
                (gcd (Math/abs dr) (Math/abs dc)))
        delta (->vec2 (/ dr denom) (/ dc denom))]
    (->> a
         (iterate #(vec2+ % delta))
         (take-while #(contains? grid %)))))

(defn combinations [as]
  (->> as
       (iterate rest)
       (take-while #(seq (rest %)))
       (mapcat #(map (partial vector (first %)) (rest %)))))

(defn antinodes-of-all [^Grid grid ^vec2 a ^vec2 b]
  (into #{} (concat (coords-in-line grid a b)
                    (coords-in-line grid b a))))

(defn all-antinodes-all [^Grid grid as]
  (->> (combinations as)
       (map #(apply antinodes-of-all grid %))
       (reduce set/union #{})))

(defn part-2 [lines]
  (let [grid (lines->grid lines)
        ants (collect-antennae grid)]
    (->> (vals ants)
         (map #(all-antinodes-all grid %))
         (reduce set/union #{})
         count)))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part-2 lines))))
