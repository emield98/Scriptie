;; ChatGPT-generated solution will go here.
(ns output_16b
  (:require [clojure.string :as str]
            [clojure.data.priority-map :refer [priority-map]]))

(defn parse-grid [lines]
  (vec (map vec lines)))

(def directions
  {:N [-1 0], :E [0 1], :S [1 0], :W [0 -1]})

(def rotate-left
  {:N :W, :W :S, :S :E, :E :N})

(def rotate-right
  {:N :E, :E :S, :S :W, :W :N})

(defn in-bounds? [grid [r c]]
  (and (<= 0 r (dec (count grid)))
       (<= 0 c (dec (count (first grid))))))

(defn get-tile [grid [r c]]
  (get-in grid [r c]))

(defn find-pos [grid ch]
  (first (for [r (range (count grid))
               c (range (count (first grid)))
               :when (= (get-in grid [r c]) ch)]
           [r c])))

(defn neighbors [grid [pos dir]]
  (for [[ndir cost] [[(rotate-left dir) 1000]
                     [dir 1]
                     [(rotate-right dir) 1000]]
        :let [[dr dc] (ndir directions)
              newpos [(+ dr (first pos)) (+ dc (second pos))]]
        :when (and (in-bounds? grid newpos)
                   (not= (get-tile grid newpos) \#))]
    [newpos ndir cost]))

(defn build-graph [grid start end]
  (let [q (priority-map [start :E] 0)
        seen {}
        paths {}]
    (loop [q q, seen seen, paths paths]
      (if (empty? q)
        [seen paths]
        (let [[[pos dir] cost] (peek q)
              q (pop q)]
          (if (and (contains? seen [pos dir])
                   (<= (seen [pos dir]) cost))
            (recur q seen paths)
            (let [nexts (neighbors grid [pos dir])
                  q' (reduce (fn [q [np nd c]]
                               (assoc q [np nd] (+ cost c)))
                             q nexts)
                  paths' (reduce (fn [p [np nd _]]
                                   (assoc p [np nd] [pos dir]))
                                 paths nexts)]
              (recur q' (assoc seen [pos dir] cost) (assoc paths' [pos dir] [pos dir])))))))))

(defn collect-paths [paths seen end]
  (let [min-cost (apply min (for [[[_ d] cost] seen
                                  :when (= (first (key paths)) end)]
                              cost))
        end-dirs (for [[[p d] cost] seen
                       :when (and (= p end) (= cost min-cost))]
                   [p d])]
    (loop [stack end-dirs
           visited #{}]
      (if (empty? stack)
        visited
        (let [[pos dir] (peek stack)
              stack (pop stack)
              prev (get paths [pos dir])]
          (if (or (nil? prev) (visited pos))
            (recur stack visited)
            (recur (conj stack prev) (conj visited pos))))))))

(defn part [lines]
  (let [grid (parse-grid lines)
        start (find-pos grid \S)
        end (find-pos grid \E)
        [seen paths] (build-graph grid start end)
        visited (collect-paths paths seen end)]
    (count visited)))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
