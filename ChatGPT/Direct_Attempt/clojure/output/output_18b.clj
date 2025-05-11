;; ChatGPT-generated solution will go here.
(ns output_18b
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [clojure.data.priority-map :refer [priority-map]]))

(defn parse-coord [line]
  (let [[x y] (str/split line #",")]
    [(Integer/parseInt x) (Integer/parseInt y)]))

(defn parse-input [lines]
  (map parse-coord (map str/trim (filter seq lines))))

(defn neighbors [[x y]]
  [[(inc x) y] [(dec x) y] [x (inc y)] [x (dec y)]])

(defn in-bounds? [[x y]]
  (and (<= 0 x 70) (<= 0 y 70)))

(defn bfs [start goal blocked]
  (loop [q (priority-map start 0)
         visited #{start}]
    (if-let [[[pos cost] & rest] (seq q)]
      (if (= pos goal)
        true
        (let [next (for [n (neighbors pos)
                         :when (and (in-bounds? n)
                                    (not (blocked n))
                                    (not (visited n)))]
                     n)
              q' (reduce (fn [q n] (assoc q n (inc cost))) (dissoc q pos) next)
              visited' (into visited next)]
          (recur q' visited')))
      false)))

(defn part [lines]
  (let [coords (parse-input lines)]
    (loop [fallen #{} remaining coords]
      (let [next (first remaining)
            fallen' (conj fallen next)]
        (if (not (bfs [0 0] [70 70] fallen'))
          (str (first next) "," (second next))
          (recur fallen' (rest remaining)))))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
