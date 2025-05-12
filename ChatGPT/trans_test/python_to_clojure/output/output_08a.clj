(ns output_08a
  (:require [clojure.string :as str]))

(defn part [lines]
  (let [grid (vec (filter (complement str/blank?) lines))
        rows (count grid)
        cols (count (first grid))
        antennas (reduce
                  (fn [m [r c ch]]
                    (if (= ch \.)
                      m
                      (update m ch (fnil conj []) [r c])))
                  {}
                  (for [r (range rows)
                        c (range cols)
                        :let [ch (get-in grid [r c])]]
                    [r c ch]))
        antinodes (reduce
                   (fn [acc [_ positions]]
                     (let [n (count positions)]
                       (reduce
                        (fn [a i]
                          (let [[r1 c1] (nth positions i)]
                            (reduce
                             (fn [a j]
                               (if (= i j)
                                 a
                                 (let [[r2 c2] (nth positions j)
                                       mid-r (- (* 2 r2) r1)
                                       mid-c (- (* 2 c2) c1)]
                                   (if (and (<= 0 mid-r (dec rows)) (<= 0 mid-c (dec cols)))
                                     (conj a [mid-r mid-c])
                                     a))))
                             a
                             (range n))))
                        acc
                        (range n))))
                   #{}
                   antennas)]
    (count antinodes)))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
