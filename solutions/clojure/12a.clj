(ns aoc2024.day12
  (:require [clojure.string :as str]
            [clojure.set :as set])
  (:import [java.util ArrayDeque]))

;; --- vec2 + Grid setup ---
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
(defn move [dir steps ^vec2 pos]
  (vec2+ pos (case dir
               :up (->vec2 (- steps) 0)
               :down (->vec2 steps 0)
               :left (->vec2 0 (- steps))
               :right (->vec2 0 steps))))

(def DIRS4 [:up :right :down :left])
(defn neighbors4 [^vec2 p]
  (mapv #(move % 1 p) DIRS4))

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

;; --- BFS ---
(defn nil-visit ([] nil) ([_ _] nil-visit))
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

;; --- Core Day 12 logic ---
(defn neighbors [^Grid grid ^vec2 coord]
  (let [color (grid coord)]
    (->> (neighbors4 coord)
         (filterv #(and (contains? grid %) (= color (grid %)))))))

(defn num-convex-corners [dirs]
  (->> [#{:up :right} #{:up :left} #{:down :right} #{:down :left}]
       (filter #(set/subset? % dirs))
       count))

(defn num-concave-corners [^Grid grid dirs ^vec2 cur]
  (let [color (grid cur)
        inside-dirs (set/difference #{:up :right :down :left} dirs)]
    (->> [#{:up :right} #{:right :down} #{:down :left} #{:left :up}]
         (filter #(and (set/subset? % inside-dirs)
                       (not= color
                             (let [pos (->> cur (move (first %) 1) (move (second %) 1))]
                               (grid pos nil)))))
         count)))

(defn border-property-visit [^Grid grid ^long perimeter ^long n-corners]
  (fn
    ([] {:perimeter perimeter :n-corners n-corners})
    ([_prevs ^vec2 cur]
     (let [color (grid cur)
           ds (->> DIRS4
                   (into #{} (filter #(not= color (grid (move % 1 cur) nil)))))]
       (border-property-visit
        grid
        (+ perimeter (count ds))
        (+ n-corners
           (num-convex-corners ds)
           (num-concave-corners grid ds cur)))))))

(defn find-plots [^Grid grid]
  (loop [coords (persistent! (fold-grid-keys conj! (transient #{}) grid))
         plots []]
    (if (empty? coords)
      plots
      (let [{:keys [prevs visit]}
            (bfs (partial neighbors grid) (first coords) (constantly false)
                 (border-property-visit grid 0 0))
            visited (into #{} (keys prevs))
            {:keys [perimeter n-corners]} (visit)]
        (recur (set/difference coords visited)
               (conj plots {:type (grid (first coords))
                            :coords visited
                            :perimeter perimeter
                            :n-corners n-corners}))))))

(defn part-1 [lines]
  (let [grid (lines->grid lines)]
    (->> grid
         find-plots
         (reduce #(+ %1 (* (:perimeter %2)
                           (count (:coords %2)))) 0))))

(defn -main [& args]
  (if-let [filename (first args)]
    (let [lines (str/split-lines (slurp filename))]
      (println (part-1 lines)))))
