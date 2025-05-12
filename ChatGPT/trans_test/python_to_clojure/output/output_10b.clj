(ns output_10b
  (:require [clojure.string :as str]))

(def directions [[-1 0] [1 0] [0 -1] [0 1]])

(defn part [lines]
  (let [grid (vec (mapv #(vec (map #(Integer/parseInt (str %)) (str/trim %))) (filter (complement str/blank?) lines)))
        rows (count grid)
        cols (count (first grid))
        memo (atom {})]
    (letfn [(dfs [r c]
              (if-let [cached (@memo [r c])]
                cached
                (let [val (get-in grid [r c])
                      result (if (= val 9)
                               1
                               (reduce
                                (fn [acc [dr dc]]
                                  (let [nr (+ r dr) nc (+ c dc)]
                                    (if (and (<= 0 nr (dec rows))
                                             (<= 0 nc (dec cols))
                                             (= (get-in grid [nr nc]) (inc val)))
                                      (+ acc (dfs nr nc))
                                      acc)))
                                0
                                directions))]
                  (swap! memo assoc [r c] result)
                  result)))]
      (reduce
       (fn [acc [r c]]
         (if (= 0 (get-in grid [r c]))
           (+ acc (dfs r c))
           acc))
       0
       (for [r (range rows) c (range cols)] [r c])))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
