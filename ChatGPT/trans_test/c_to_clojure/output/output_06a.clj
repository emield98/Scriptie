(ns output_02a
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(def directions [[-1 0] [0 1] [1 0] [0 -1]])

(defn find-start [grid]
  (first (for [i (range (count grid))
               j (range (count (nth grid i)))
               :when (= (get-in grid [i j]) \^)]
           [i j])))

(defn part [lines]
  (let [grid (vec (map vec lines))
        [start-x start-y] (find-start grid)
        grid (assoc-in grid [start-x start-y] \.)
        max-x (count grid)
        max-y (count (first grid))]
    (loop [x start-x
           y start-y
           dir 0
           visited #{}
           visited-count 0]
      (if (or (neg? x) (neg? y) (>= x max-x) (>= y max-y)
              (nil? (get-in grid [x y])))
        visited-count
        (let [visited (if (visited [x y]) visited (conj visited [x y]))
              visited-count (if (visited [x y]) visited-count (inc visited-count))
              [dx dy] (directions dir)
              next-x (+ x dx)
              next-y (+ y dy)
              next-cell (get-in grid [next-x next-y])]
          (if (= next-cell \#)
            (recur x y (mod (inc dir) 4) visited visited-count)
            (recur next-x next-y dir visited visited-count)))))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
