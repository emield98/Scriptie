;; src/aoc2024/day14.clj
(ns aoc2024.day14
  (:require [clojure.string :as str])
  (:gen-class))

;; fixed grid dimensions from problem statement
(def WIDTH  101)
(def HEIGHT 103)

;; parse input lines into vector of maps {:pos [x y] :vel [vx vy]}
(defn parse-lines [content]
  (->> (str/split-lines content)
       (remove str/blank?)
       (map (fn [line]
              (let [[x y vx vy] (mapv parse-long (re-seq #"-?\d+" line))]
                {:pos [x y] :vel [vx vy]})))
       vec))

;; advance a single robot with wrap-around
(defn advance [{:keys [pos vel] :as robot}]
  (let [[x y]   pos
        [vx vy] vel
        nx      (mod (+ x vx) WIDTH)
        ny      (mod (+ y vy) HEIGHT)]
    (assoc robot :pos [nx ny])))

;; simulate N steps on sequence of robots
(defn simulate [robots steps]
  (nth (iterate (fn [rs] (mapv advance rs)) robots) steps))

;; compute quadrant index or nil if on mid-line
(defn quadrant [[x y]]
  (let [half-w (quot WIDTH 2)
        half-h (quot HEIGHT 2)]
    (when (and (not= x half-w)
               (not= y half-h))
      (+ (* 2 (quot y (inc half-h)))
         (quot x (inc half-w))))))

;; Part 1: after 100 seconds, multiply counts in each quadrant
(defn part-1 [content]
  (let [robots    (parse-lines content)
        after100  (simulate robots 100)
        groups    (->> after100
                       (group-by #(quadrant (:pos %))))
        counts    (->> groups
                       (remove (fn [[k _]] (nil? k)))
                       (map (comp count second)))]
    (reduce * counts)))

;; entry point
(defn -main [& args]
  (if-let [file (first args)]
    (let [content (slurp file)]
      (println (part-1 content)))))
