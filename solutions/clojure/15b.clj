(ns aoc2024.day15b
  (:require [clojure.string :as str]))

;; Inlined utility functions from aoc2024.utils
(defn to-blocks [input]
  (str/split input #"\n\n"))

(defn to-lines [input]
  (str/split-lines input))

(defn to-matrix [input]
  (->> input
       to-lines
       (mapv vec)))

(defn matrix->map [matrix]
  (into {}
        (for [row (range (count matrix))
              col (range (count (first matrix)))
              :let [coord [row col]]]
          [coord (get-in matrix coord)])))

;; Original logic from day15b.clj
(defn step-fn [move]
  (case move
    \> (fn [[r c]] [r (inc c)])
    \< (fn [[r c]] [r (dec c)])
    \^ (fn [[r c]] [(dec r) c])
    \v (fn [[r c]] [(inc r) c])))

(defn parse-warehouse [warehouse-str]
  (let [warehouse-matrix (to-matrix warehouse-str)
        warehouse-map (matrix->map warehouse-matrix)]
    {:width (count (first warehouse-matrix))
     :height (count warehouse-matrix)
     :robot-pos (ffirst (filter #(= \@ (val %)) warehouse-map))
     :blocks (set (keep #(when (= \O (val %)) (key %)) warehouse-map))
     :walls (set (keep #(when (= \# (val %)) (key %)) warehouse-map))}))

(defn find-block-chains [blocks start step]
  (loop [[pos & q] [start], seen #{}, chains []]
    (cond
      (nil? pos) chains
      (seen pos) (recur q seen chains)
      (blocks pos)
      (let [next (blocks pos)
            new-seen (conj seen pos next)]
        (recur (concat q [(step pos) (step next)])
               new-seen
               (conj chains [pos next])))
      :else (recur q seen chains))))

(defn push-block-chain [blocks chains step]
  (reduce (fn [bmap [a b]]
            (let [a' (step a)
                  b' (step b)]
              (assoc bmap a' b' b' a')))
          (apply dissoc blocks (mapcat identity chains))
          chains))

(defn push-big-block [{:keys [blocks walls] :as w} next step]
  (let [chains (find-block-chains blocks next step)
        target-positions (map step (mapcat identity chains))]
    (if (every? (complement walls) target-positions)
      (-> w
          (assoc :robot-pos next)
          (assoc :blocks (push-block-chain blocks chains step)))
      w)))

(defn move-once [{:keys [blocks walls robot-pos] :as w} move push-fn]
  (let [next ((step-fn move) robot-pos)]
    (cond
      (walls next) w
      (blocks next) (push-fn w next (step-fn move))
      :else (assoc w :robot-pos next))))

(defn simulate [{:keys [blocks] :as w} moves push-fn]
  (reduce (fn [state m] (move-once state m push-fn)) w moves))

(defn expand-block [[r c]]
  { [r (* 2 c)]         [r (inc (* 2 c))]
    [r (inc (* 2 c))]   [r (* 2 c)] })

(defn expand-warehouse [{:keys [robot-pos blocks walls width height]}]
  {:height height
   :width (* width 2)
   :robot-pos [ (first robot-pos) (* 2 (second robot-pos)) ]
   :blocks (apply merge (map expand-block blocks))
   :walls (set (mapcat (fn [[r c]]
                         [ [r (* 2 c)] [r (inc (* 2 c))] ])
                       walls))})

(defn part-2 [input]
  (let [[layout moves] (to-blocks input)
        warehouse (expand-warehouse (parse-warehouse layout))
        move-seq (->> (to-lines moves) (map str/trim) (apply str))]
    (->> (:blocks (simulate warehouse move-seq push-big-block))
         (map (partial sort-by last))
         distinct
         (map first)
         (map (fn [[r c]] (+ (* 100 r) c)))
         (reduce +))))

(defn -main [& [file]]
  (println (part-2 (slurp (or file "input")))))
