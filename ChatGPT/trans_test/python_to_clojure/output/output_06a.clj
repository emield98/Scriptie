(ns output_06a
  (:require [clojure.string :as str]))

(defn part [lines]
  (let [grid (vec (mapv vec (filter (complement str/blank?) lines)))
        rows (count grid)
        cols (count (first grid))
        directions {\^ [-1 0], \> [0 1], \v [1 0], \< [0 -1]}
        turn-right {\^ \>, \> \v, \v \<, \< \^}
        [start-pos start-dir] (first
                                (for [r (range rows)
                                      c (range cols)
                                      :let [ch (get-in grid [r c])]
                                      :when (contains? directions ch)]
                                  [[r c] ch]))]
    (loop [pos start-pos
           dir start-dir
           visited #{start-pos}]
      (let [[dr dc] (directions dir)
            [nr nc] [(+ (first pos) dr) (+ (second pos) dc)]]
        (cond
          (and (<= 0 nr (dec rows)) (<= 0 nc (dec cols)) (not= (get-in grid [nr nc]) \#))
          (recur [nr nc] dir (conj visited [nr nc]))

          :else
          (let [new-dir (turn-right dir)
                [ndr ndc] (directions new-dir)
                [next-r next-c] [(+ (first pos) ndr) (+ (second pos) ndc)]]
            (if (and (<= 0 next-r (dec rows)) (<= 0 next-c (dec cols)))
              (recur pos new-dir visited)
              (count visited))))))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
