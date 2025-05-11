;; ChatGPT-generated solution will go here.
(ns output_18a
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [clojure.data.priority-map :refer [priority-map]]))

(defn parse-coord [line]
  (let [[x y] (str/split line #",")]
    [(Integer/parseInt x) (Integer/parseInt y)]))

(defn parse-input [lines]
  (->> lines
       (map str/trim)
       (filter seq)
       (map parse-coord)
       (take 1024)
       set))

(defn neighbors [[x y]]
  [[(inc x) y] [(dec x) y] [x (inc y)] [x (dec y)]])

(defn in-bounds? [[x y]]
  (and (<= 0 x 70) (<= 0 y 70)))

(defn bfs [start goal blocked]
  (loop [q (priority-map start 0)
         dist {start 0}]
    (if-let [[[pos cost] & rest] (seq q)]
      (if (= pos goal)
        cost
        (let [next (for [n (neighbors pos)
                         :when (and (in-bounds? n)
                                    (not (blocked n))
                                    (or (nil? (dist n))
                                        (< (inc cost) (dist n))))]
                     n)
              q' (reduce (fn [q n] (assoc q n (inc cost))) (dissoc q pos) next)
              d' (reduce (fn [d n] (assoc d n (inc cost))) dist next)]
          (recur q' d')))
      nil)))

(defn part [lines]
  (let [blocked (parse-input lines)]
    (bfs [0 0] [70 70] blocked)))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
