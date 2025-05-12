(ns output_12a
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(def directions [[-1 0] [1 0] [0 -1] [0 1]])

(defn bfs [grid visited sr sc]
  (let [rows (count grid)
        cols (count (first grid))
        plant (get-in grid [sr sc])]
    (loop [queue (conj clojure.lang.PersistentQueue/EMPTY [sr sc])
           vis visited
           region #{[sr sc]}]
      (if (empty? queue)
        [region vis]
        (let [[r c] (peek queue)
              queue (pop queue)
              [vis queue region]
              (reduce
               (fn [[v q reg] [dr dc]]
                 (let [nr (+ r dr)
                       nc (+ c dc)]
                   (if (and (<= 0 nr (dec rows))
                            (<= 0 nc (dec cols))
                            (not (get-in v [nr nc]))
                            (= (get-in grid [nr nc]) plant))
                     [(assoc-in v [nr nc] true)
                      (conj q [nr nc])
                      (conj reg [nr nc])]
                     [v q reg])))
               [vis queue region]
               directions)]
          (recur queue vis region))))))

(defn calculate-perimeter [region-set rows cols]
  (reduce
   (fn [perim [r c]]
     (+ perim
        (count
         (filter
          (fn [[dr dc]]
            (let [nr (+ r dr)
                  nc (+ c dc)]
              (or (not (<= 0 nr (dec rows)))
                  (not (<= 0 nc (dec cols)))
                  (not (contains? region-set [nr nc])))))
          directions)))
   0
   region-set))

(defn part [lines]
  (let [grid (vec (mapv vec (filter (complement str/blank?) lines)))
        rows (count grid)
        cols (count (first grid))]
    (loop [r 0
           c 0
           visited (vec (repeat rows (vec (repeat cols false))))
           total-price 0]
      (cond
        (>= r rows) total-price
        (>= c cols) (recur (inc r) 0 visited total-price)
        (get-in visited [r c]) (recur r (inc c) visited total-price)
        :else
        (let [[region new-visited] (bfs grid (assoc-in visited [r c] true) r c)
              area (count region)
              perimeter (calculate-perimeter region rows cols)]
          (recur r (inc c) new-visited (+ total-price (* area perimeter))))))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
