(ns aoc2024.day04
  (:require [clojure.string :as str]))

;; --- Minimal vec2 + Grid tools ---
(defrecord vec2 [^long row ^long col])
(defn ->vec2 [r c] (vec2. r c))
(defn move [dir n ^vec2 pos]
  (let [delta (case dir
                :up    (->vec2 -1 0)
                :down  (->vec2 1 0)
                :left  (->vec2 0 -1)
                :right (->vec2 0 1))]
    (->vec2 (+ (:row pos) (* n (:row delta)))
            (+ (:col pos) (* n (:col delta))))))

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

(defn fold-grid-keys [f init ^Grid g]
  (let [h (.height g)
        w (.width g)]
    (loop [acc init row 0 col 0]
      (cond
        (>= row h) acc
        (>= col w) (recur acc (inc row) 0)
        :else (recur (f acc (->vec2 row col)) row (inc col))))))

;; --- Main logic ---
(defn check-word [dirs ^String word ^Grid grid ^vec2 cur-pos]
  (->> (range (count word))
       (map (comp #(grid % nil)
                  #(reduce (fn [^vec2 pos dir] (move dir % pos)) ^vec2 cur-pos dirs)))
       (= (seq word))))

(def DIAGONAL-CHECKERS
  (mapv #(partial check-word %)
        [[:up] [:right] [:down] [:left]
         [:up :left] [:up :right] [:down :left] [:down :right]]))

(defn part-1 [lines]
  (let [grid (lines->grid lines)]
    (fold-grid-keys
      (fn [acc pos]
        (+ acc (count (filter (fn [checker] (checker "XMAS" grid pos)) DIAGONAL-CHECKERS))))
      0 grid)))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part-1 lines))))