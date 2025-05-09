(ns aoc2024.day04b
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
            (+ (:col pos) (* n (:col delta)))))
  )
(defn flip-dir [dir]
  (case dir
    :up :down :down :up :left :right :right :left))

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

;; --- Core logic ---
(defn check-word [dirs ^String word ^Grid grid ^vec2 cur-pos]
  (->> (range (count word))
       (map (comp #(grid % nil)
                  #(reduce (fn [^vec2 pos dir] (move dir % pos)) cur-pos dirs)))
       (= (seq word))))

(defn cross-checker [d1 d2 ^String word ^Grid grid ^vec2 cur-pos]
  (let [half (quot (count word) 2)
        p1 (reduce (fn [^vec2 coord dir] (move (flip-dir dir) half coord)) cur-pos d1)
        p2 (reduce (fn [^vec2 coord dir] (move (flip-dir dir) half coord)) cur-pos d2)]
    (and (check-word d1 word grid p1)
         (check-word d2 word grid p2))))

(def MAS-CHECKERS
  (mapv #(partial cross-checker %1 %2 "MAS")
        [[:down :right] [:down :right] [:up :left]  [:up :left]]
        [[:down :left]  [:up :right]   [:up :right] [:down :left]]))

(defn part-2 [lines]
  (let [grid (lines->grid lines)]
    (fold-grid-keys
      (fn [acc pos]
        (+ acc (count (filter (fn [checker] (checker grid pos)) MAS-CHECKERS))))
      0 grid)))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part-2 lines))))