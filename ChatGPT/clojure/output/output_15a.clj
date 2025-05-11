;; ChatGPT-generated solution will go here.
(ns output_15a
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(defn parse-input [lines]
  (let [[grid-lines move-lines] (split-with #(not (str/starts-with? % "<")) lines)
        grid (vec (map vec grid-lines))
        moves (->> move-lines
                   (apply str)
                   (remove #{\newline})
                   (map {\^ [-1 0] \v [1 0] \< [0 -1] \> [0 1]}))]
    {:grid grid :moves moves}))

(defn find-positions [grid ch]
  (for [r (range (count grid))
        c (range (count (first grid)))
        :when (= (get-in grid [r c]) ch)]
    [r c]))

(defn in-bounds? [grid [r c]]
  (and (<= 0 r) (< r (count grid))
       (<= 0 c) (< c (count (first grid)))))

(defn walk [state]
  (let [{:keys [grid moves]} state
        robot (first (find-positions grid \@))
        boxes (set (find-positions grid \O))]
    (loop [pos robot
           boxes boxes
           moves moves]
      (if (empty? moves)
        {:final-boxes boxes}
        (let [[dr dc] (first moves)
              next-pos [(+ dr (first pos)) (+ dc (second pos))]]
          (cond
            (not (in-bounds? grid next-pos)) (recur pos boxes (rest moves))
            (= (get-in grid next-pos) \#) (recur pos boxes (rest moves))
            (boxes next-pos)
            (let [beyond [(+ dr (first next-pos)) (+ dc (second next-pos))]]
              (if (and (in-bounds? grid beyond)
                       (not= (get-in grid beyond) \#)
                       (not (boxes beyond)))
                (recur next-pos
                       (-> boxes (disj next-pos) (conj beyond))
                       (rest moves))
                (recur pos boxes (rest moves))))
            :else (recur next-pos boxes (rest moves))))))))

(defn gps [[r c]] (+ (* 100 r) c))

(defn part [lines]
  (let [{:keys [grid moves]} (parse-input lines)
        {:keys [final-boxes]} (walk {:grid grid :moves moves})]
    (reduce + (map gps final-boxes))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
