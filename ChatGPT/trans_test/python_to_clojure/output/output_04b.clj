(ns output_04b
  (:require [clojure.string :as str]))

(defn part [lines]
  (let [grid (vec (filter (complement str/blank?) lines))
        rows (count grid)
        cols (count (first grid))
        patterns [[ [0 0] [1 1] [2 2] [0 2] [1 1] [2 0] ]
                  [ [0 2] [1 1] [2 0] [0 0] [1 1] [2 2] ]
                  [ [2 0] [1 1] [0 2] [0 0] [1 1] [2 2] ]
                  [ [2 2] [1 1] [0 0] [0 2] [1 1] [2 0] ]
                  [ [0 0] [1 1] [2 2] [2 0] [1 1] [0 2] ]
                  [ [2 0] [1 1] [0 2] [2 2] [1 1] [0 0] ]]
        valid-prefixes #{[\M \A \S] [\S \A \M]}]
    (loop [r 0, c 0, acc 0]
      (cond
        (> r (- rows 3)) acc
        (> c (- cols 3)) (recur (inc r) 0 acc)
        :else
        (let [found? (some (fn [pattern]
                             (let [chars (map (fn [[dr dc]]
                                                (get-in grid [(+ r dr) (+ c dc)]))
                                              pattern)
                                   [p1 p2] (split-at 3 chars)]
                               (and (valid-prefixes p1)
                                    (valid-prefixes p2))))
                           patterns)]
          (recur r (inc c) (if found? (inc acc) acc))))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
