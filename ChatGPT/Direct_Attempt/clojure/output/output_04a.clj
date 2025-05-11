;; ChatGPT-generated solution will go here.
(ns output_04a
  (:require [clojure.string :as str]))

(def directions [[0 1] [1 0] [1 1] [-1 1] ; right, down, down-right, up-right
                 [0 -1] [-1 0] [-1 -1] [1 -1]]) ; left, up, up-left, down-left

(def target [\X \M \A \S])

(defn in-bounds? [grid [r c]]
  (and (>= r 0) (< r (count grid))
       (>= c 0) (< c (count (first grid)))))

(defn match-word? [grid r c [dr dc]]
  (every? identity
          (map-indexed
           (fn [i ch]
             (let [nr (+ r (* i dr))
                   nc (+ c (* i dc))]
               (and (in-bounds? grid [nr nc])
                    (= ch (get-in grid [nr nc])))))
           target)))

(defn part [lines]
  (let [grid (mapv vec lines)]
    (count
     (for [r (range (count grid))
           c (range (count (first grid)))
           dir directions
           :when (match-word? grid r c dir)]
       [r c dir]))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
