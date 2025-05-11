;; ChatGPT-generated solution will go here.
(ns output_12a
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(defn parse-grid [lines]
  (mapv vec lines))

(defn neighbors [[r c] rows cols]
  (filter (fn [[r' c']]
            (and (<= 0 r') (< r' rows)
                 (<= 0 c') (< c' cols)))
          [[(dec r) c] [(inc r) c] [r (dec c)] [r (inc c)]]))

(defn flood-fill [grid visited start]
  (let [rows (count grid)
        cols (count (first grid))
        ch (get-in grid start)]
    (loop [q (conj clojure.lang.PersistentQueue/EMPTY start)
           region #{}
           visited visited
           perimeter 0]
      (if (empty? q)
        [region visited perimeter]
        (let [[r c :as pos] (peek q)
              q (pop q)
              region (conj region pos)
              visited (conj visited pos)
              nbrs (neighbors pos rows cols)
              same (filter #(= ch (get-in grid %)) nbrs)
              different (filter #(not= ch (get-in grid %)) nbrs)
              out-of-bounds (- 4 (count nbrs))
              new-perim (+ perimeter (count different) out-of-bounds)
              to-visit (remove visited same)]
          (recur (into q to-visit) region visited new-perim))))))

(defn part [lines]
  (let [grid (parse-grid lines)
        rows (count grid)
        cols (count (first grid))]
    (loop [r 0, c 0, visited #{}, total 0]
      (cond
        (= r rows) total
        (= c cols) (recur (inc r) 0 visited total)
        (visited [r c]) (recur r (inc c) visited total)
        :else
        (let [[region new-visited perimeter] (flood-fill grid visited [r c])
              area (count region)
              price (* area perimeter)]
          (recur r (inc c) new-visited (+ total price))))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
