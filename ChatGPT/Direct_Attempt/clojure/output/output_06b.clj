;; ChatGPT-generated solution will go here.
(ns output_06b
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(def deltas {\^ [-1 0], \> [0 1], \v [1 0], \< [0 -1]})
(def turn-right {\^ \>, \> \v, \v \<, \< \^})

(defn in-bounds? [grid [r c]]
  (and (<= 0 r) (< r (count grid))
       (<= 0 c) (< c (count (first grid)))))

(defn blocked? [grid [r c]]
  (or (not (in-bounds? grid [r c]))
      (= (get-in grid [r c]) \#)))

(defn move [[r c] dir]
  (let [[dr dc] (deltas dir)]
    [(+ r dr) (+ c dc)]))

(defn find-guard [grid]
  (first (for [r (range (count grid))
               c (range (count (first grid)))
               :let [ch (get-in grid [r c])]
               :when (contains? deltas ch)]
           [[r c] ch])))

(defn simulate-path [grid start-pos start-dir]
  (loop [pos start-pos
         dir start-dir
         seen #{}]
    (let [state [pos dir]]
      (cond
        (not (in-bounds? grid pos)) {:loop false}
        (seen state) {:loop true}
        :else
        (let [ahead (move pos dir)]
          (if (blocked? grid ahead)
            (recur pos (turn-right dir) (conj seen state))
            (recur ahead dir (conj seen state))))))))

(defn part [lines]
  (let [grid (mapv vec lines)
        [[start-pos start-dir]] (vector (find-guard grid))
        rows (count grid)
        cols (count (first grid))]
    (count
     (for [r (range rows)
           c (range cols)
           :let [ch (get-in grid [r c])]
           :when (and (not= [r c] start-pos)
                      (not= ch \#))
           :let [new-grid (assoc-in grid [r c] \#)
                 result (simulate-path new-grid start-pos start-dir)]
           :when (:loop result)]
       [r c]))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
