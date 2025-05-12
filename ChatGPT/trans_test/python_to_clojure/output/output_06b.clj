(ns output_06b
  (:require [clojure.string :as str]))

(def directions [\^ \> \v \<])
(def deltas [[-1 0] [0 1] [1 0] [0 -1]])
(def turn-right {0 1, 1 2, 2 3, 3 0})

(defn find-start [grid]
  (first (for [r (range (count grid))
               c (range (count (first grid)))
               :let [ch (get-in grid [r c])]
               :when (some #{ch} directions)]
           [[r c] (.indexOf directions ch)])))

(defn simulate [grid start-pos dir-idx]
  (let [rows (count grid)
        cols (count (first grid))]
    (loop [pos start-pos
           dir dir-idx
           visited #{start-pos}
           seen #{}]
      (let [state [pos dir]]
        (cond
          (seen state) [true visited]
          :else
          (let [[r c] pos
                [dr dc] (deltas dir)
                nr (+ r dr)
                nc (+ c dc)]
            (cond
              (or (< nr 0) (>= nr rows) (< nc 0) (>= nc cols))
              [false visited]

              (= (get-in grid [nr nc]) \#)
              (recur pos (turn-right dir) visited (conj seen state))

              :else
              (recur [nr nc] dir (conj visited [nr nc]) (conj seen state)))))))))

(defn part [lines]
  (let [grid (vec (mapv vec (filter (complement str/blank?) lines)))
        [start-pos dir-idx] (find-start grid)
        [_ original-path] (simulate grid start-pos dir-idx)
        rows (count grid)
        cols (count (first grid))]
    (loop [r 0, c 0, cnt 0]
      (cond
        (>= r rows) cnt
        (>= c cols) (recur (inc r) 0 cnt)
        :else
        (let [ch (get-in grid [r c])]
          (if (and (not= [r c] start-pos) (= ch \.))
            (let [modified-grid (assoc-in grid [r c] \#)
                  [looped? _] (simulate modified-grid start-pos dir-idx)
                  cnt (if looped? (inc cnt) cnt)]
              (recur r (inc c) cnt))
            (recur r (inc c) cnt)))))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
