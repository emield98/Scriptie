(ns output_08b
  (:require [clojure.string :as str]))

(defn in-bounds? [r c rows cols]
  (and (<= 0 r (dec rows)) (<= 0 c (dec cols))))

(defn part [lines]
  (let [grid (vec (mapv vec (filter (complement str/blank?) lines)))
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
                     [r c ch]))]
    (count
     (reduce
      (fn [antinodes [_ positions]]
        (if (< (count positions) 2)
          antinodes
          (let [antinodes (into antinodes positions)
                n (count positions)]
            (reduce
             (fn [a i]
               (let [[r1 c1] (nth positions i)]
                 (reduce
                  (fn [a j]
                    (if (<= j i)
                      a
                      (let [[r2 c2] (nth positions j)
                            dr (- r2 r1)
                            dc (- c2 c1)]
                        (loop [a a, r (+ r2 dr), c (+ c2 dc)]
                          (if (in-bounds? r c rows cols)
                            (recur (conj a [r c]) (+ r dr) (+ c dc))
                            (loop [a a, r (- r1 dr), c (- c1 dc)]
                              (if (in-bounds? r c rows cols)
                                (recur (conj a [r c]) (- r dr) (- c dc))
                                a))))))
                  a
                  (range n))))
             antinodes
             (range n)))))
      #{}
      antennas))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
