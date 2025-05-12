(ns output_14a
  (:require [clojure.string :as str]))

(defn parse-robot [line]
  (let [[pos vel] (str/split line #"\s+")
        [px py] (map parse-long (str/split (subs pos 2) #","))
        [vx vy] (map parse-long (str/split (subs vel 2) #","))]
    [px py vx vy]))

(defn part [lines]
  (let [width 101
        height 103
        time 100
        mid-x (quot width 2)
        mid-y (quot height 2)
        robots (map parse-robot (filter (complement str/blank?) lines))
        positions (reduce
                   (fn [m [px py vx vy]]
                     (let [x (mod (+ px (* vx time)) width)
                           y (mod (+ py (* vy time)) height)]
                       (update m [x y] (fnil inc 0))))
                   {}
                   robots)
        quadrant-counts (reduce
                         (fn [[q1 q2 q3 q4] [[x y] cnt]]
                           (cond
                             (or (= x mid-x) (= y mid-y)) [q1 q2 q3 q4]
                             (and (< x mid-x) (< y mid-y)) [(+ q1 cnt) q2 q3 q4]
                             (and (>= x mid-x) (< y mid-y)) [q1 (+ q2 cnt) q3 q4]
                             (and (< x mid-x) (>= y mid-y)) [q1 q2 (+ q3 cnt) q4]
                             :else [q1 q2 q3 (+ q4 cnt)]))
                         [0 0 0 0]
                         positions)
        safety-factor (reduce * quadrant-counts)]
    safety-factor))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
