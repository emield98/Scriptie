(ns output_16a
  (:require [clojure.string :as str]
            [clojure.data.priority-map :refer [priority-map]]))

(def directions [[0 1] [1 0] [0 -1] [-1 0]])

(defn part [lines]
  (let [grid (vec (mapv vec (filter (complement str/blank?) lines)))
        rows (count grid)
        cols (count (first grid))
        [start end] (reduce
                     (fn [[s e] [r c ch]]
                       (cond
                         (= ch \S) [[r c] e]
                         (= ch \E) [s [r c]]
                         :else [s e]))
                     [nil nil]
                     (for [r (range rows)
                           c (range cols)
                           :let [ch (get-in grid [r c])]]
                       [r c ch]))
        init-queue (apply priority-map
                          (mapcat (fn [d] [[[start d] 1000]] (range 4)) (range 4)))
        visited (atom #{})]
    (loop [queue init-queue]
      (if (empty? queue)
        nil
        (let [[[ [r c] d] cost] (peek queue)
              queue (pop queue)]
          (if (@visited [r c d])
            (recur queue)
            (do
              (swap! visited conj [r c d])
              (if (= [r c] end)
                cost
                (let [dr (first (directions d))
                      dc (second (directions d))
                      nr (+ r dr)
                      nc (+ c dc)
                      queue (if (and (<= 0 nr (dec rows))
                                     (<= 0 nc (dec cols))
                                     (not= (get-in grid [nr nc]) \#))
                              (assoc queue [[ [nr nc] d]] (+ cost 1))
                              queue)
                      turns [ (mod (+ d 1) 4) (mod (+ d 3) 4) ] ; right and left turns
                      queue (reduce
                             (fn [q nd]
                               (if (contains? @visited [r c nd])
                                 q
                                 (assoc q [[ [r c] nd]] (+ cost 1000))))
                             queue
                             turns)]
                  (recur queue)))))))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
