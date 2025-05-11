;; ChatGPT-generated solution will go here.
(ns output_16a
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

(defn part [lines]
  (let [grid (parse-grid lines)
        start (find-pos grid \S)
        end   (find-pos grid \E)
        init-dir :E
        pq (priority-map [start init-dir] 0)
        seen #{}]
    (loop [q pq
           seen seen]
      (if (empty? q)
        nil
        (let [[[pos dir] cost] (peek q)
              q (pop q)]
          (if (= pos end)
            cost
            (let [nexts
                  (for [[ndir rot-cost] [[(rotate-left dir) 1000]
                                         [dir 1]
                                         [(rotate-right dir) 1000]]
                        :let [[dr dc] (directions ndir)
                              npos [(+ (first pos) dr) (+ (second pos) dc)]]
                        :when (and (in-bounds? grid npos)
                                   (not= (get-tile grid npos) \#))]
                    [npos ndir (+ cost rot-cost)])]
              (recur
               (reduce (fn [q [p d c]]
                         (if (seen [p d])
                           q
                           (assoc q [p d] c)))
                       q
                       nexts)
               (conj seen [pos dir]))))))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
