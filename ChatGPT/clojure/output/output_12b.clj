;; ChatGPT-generated solution will go here.
(ns output_12b
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(defn parse-grid [lines]
  (mapv vec lines))

(defn in-bounds? [r c rows cols]
  (and (<= 0 r) (< r rows)
       (<= 0 c) (< c cols)))

(defn neighbors [[r c]]
  [[(dec r) c] [(inc r) c] [r (dec c)] [r (inc c)]])

(defn flood-fill [grid visited start]
  (let [rows (count grid)
        cols (count (first grid))
        plant (get-in grid start)]
    (loop [queue (conj clojure.lang.PersistentQueue/EMPTY start)
           region #{}
           visited visited
           edge-segments #{}]
      (if (empty? queue)
        [region visited (count edge-segments)]
        (let [[r c :as pos] (peek queue)
              queue (pop queue)
              region (conj region pos)
              visited (conj visited pos)
              nbrs (neighbors pos)
              border-sides (for [[dr dc :as nbr] nbrs
                                 :let [nr (+ r (- (first nbr) r))
                                       nc (+ c (- (second nbr) c))
                                       outside? (not (in-bounds? nr nc rows cols))
                                       neighbor-val (if outside? nil (get-in grid [nr nc]))]
                                 :when (or outside? (not= neighbor-val plant))]
                             (cond
                               (= nbr [(dec r) c]) [r c :N]
                               (= nbr [(inc r) c]) [(inc r) c :N]
                               (= nbr [r (dec c)]) [r c :W]
                               (= nbr [r (inc c)]) [r (inc c) :W]))]
          (recur
            (into queue (filter #(and (in-bounds? (first %) (second %) rows cols)
                                      (= (get-in grid %) plant)
                                      (not (visited %)))
                                nbrs))
            region
            visited
            (into edge-segments border-sides))))))

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
        (let [[region new-visited sides] (flood-fill grid visited [r c])
              area (count region)
              price (* area sides)]
          (recur r (inc c) new-visited (+ total price))))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
