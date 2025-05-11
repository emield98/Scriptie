;; ChatGPT-generated solution will go here.
(ns output_06a
  (:require [clojure.string :as str]))

(def deltas {\^ [-1 0], \> [0 1], \v [1 0], \< [0 -1]})
(def turn-right {\^ \>, \> \v, \v \<, \< \^})

(defn in-bounds? [grid [r c]]
  (and (<= 0 r) (< r (count grid))
       (<= 0 c) (< c (count (first grid)))))

(defn blocked? [grid [r c]]
  (or (not (in-bounds? grid [r c]))
      (= (get-in grid [r c]) \#)))

(defn find-guard [grid]
  (first (for [r (range (count grid))
               c (range (count (first grid)))
               :let [ch (get-in grid [r c])]
               :when (contains? deltas ch)]
           [[r c] ch])))

(defn move [[r c] dir]
  (let [[dr dc] (get deltas dir)]
    [(+ r dr) (+ c dc)]))

(defn part [lines]
  (let [grid (mapv vec lines)
        [[start-pos start-dir]] (vector (find-guard grid))]
    (loop [pos start-pos
           dir start-dir
           visited #{start-pos}]
      (let [next-pos (move pos dir)]
        (if (not (in-bounds? grid next-pos))
          (count visited)
          (if (blocked? grid next-pos)
            (recur pos (turn-right dir) visited)
            (recur next-pos dir (conj visited next-pos))))))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
