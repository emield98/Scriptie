(ns output_10a
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(def directions [[-1 0] [1 0] [0 -1] [0 1]])

(defn bfs [grid r c]
  (let [rows (count grid)
        cols (count (first grid))]
    (loop [queue (conj clojure.lang.PersistentQueue/EMPTY [r c 0])
           visited #{[r c]}
           nines #{}]
      (if (empty? queue)
        (count nines)
        (let [[x y h] (peek queue)
              queue (pop queue)]
          (if (= 9 (get-in grid [x y]))
            (recur queue visited (conj nines [x y]))
            (let [curr (get-in grid [x y])
                  next-positions (for [[dx dy] directions
                                       :let [nx (+ x dx)
                                             ny (+ y dy)]
                                       :when (and (<= 0 nx (dec rows))
                                                  (<= 0 ny (dec cols))
                                                  (not (visited [nx ny]))
                                                  (= (get-in grid [nx ny]) (inc curr)))]
                                   [nx ny (inc curr)])
                  new-visited (into visited (map #(vector (first %) (second %)) next-positions))
                  new-queue (into queue next-positions)]
              (recur new-queue new-visited nines))))))))

(defn part [lines]
  (let [grid (vec (mapv #(vec (map #(Integer/parseInt (str %)) (str/trim %))) (filter (complement str/blank?) lines)))
        rows (count grid)
        cols (count (first grid))]
    (reduce
     (fn [acc [r c]]
       (if (= 0 (get-in grid [r c]))
         (+ acc (bfs grid r c))
         acc))
     0
     (for [r (range rows) c (range cols)] [r c]))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
