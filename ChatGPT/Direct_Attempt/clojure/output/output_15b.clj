;; ChatGPT-generated solution will go here.
(ns output_15b
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(defn widen-tile [ch]
  (case ch
    \# [\# \#]
    \O [\] \[] ;; represent wide box as closing and opening for visual order
    \. [\. \.]
    \@ [\@ \.]
    ;; fallback
    [ch ch]))

(defn widen-map [lines]
  (vec (mapcat (fn [line]
                 [(vec (mapcat widen-tile line))])
               lines)))

(defn parse-moves [lines]
  (->> lines
       (drop-while #(not (str/starts-with? % "<")))
       (apply str)
       (remove #{\newline})
       (map {\^ [-1 0] \v [1 0] \< [0 -1] \> [0 1]})))

(defn initial-state [grid]
  (let [rows (count grid)
        cols (count (first grid))]
    (loop [r 0, c 0]
      (cond
        (= r rows) nil
        (= c cols) (recur (inc r) 0)
        (= (get-in grid [r c]) \@)
        {:robot [r c]
         :boxes (set (for [i (range rows)
                           j (range 0 cols 2)
                           :when (and (= (get-in grid [i j]) \[)
                                      (= (get-in grid [i (inc j)]) \]))]
                      [i j]))
         :walls (set (for [i (range rows)
                           j (range cols)
                           :when (= (get-in grid [i j]) \#)]
                      [i j]))
         :rows rows
         :cols cols}
        :else (recur r (inc c))))))

(defn box-occupied? [boxes [r c]]
  (or (boxes [r c]) (boxes [r (dec c)]) (boxes [r (inc c)])))

(defn can-move-box? [state [r c] [dr dc]]
  (let [dst [(+ r dr) (+ c dc)]
        dst2 [(+ r dr) (+ c dc 1)]]
    (and (not (state :walls dst))
         (not (state :walls dst2))
         (not (box-occupied? (state :boxes) dst))
         (not (box-occupied? (state :boxes) dst2)))))

(defn move-box [boxes src dir]
  (let [[dr dc] dir
        dst [(+ (first src) dr) (+ (second src) dc)]]
    (-> boxes
        (disj src)
        (conj dst))))

(defn move-robot [state dir]
  (let [robot (:robot state)
        [dr dc] dir
        dest [(+ dr (first robot)) (+ dc (second robot))]
        box-in-way? (box-occupied? (:boxes state) dest)
        new-boxes (if box-in-way?
                    (let [src (if ((:boxes state) dest)
                                dest
                                [(first dest) (dec (second dest))])]
                      (if (can-move-box? state src dir)
                        (move-box (:boxes state) src dir)
                        nil))
                    (:boxes state))]
    (if (or (state :walls dest)
            (and box-in-way? (nil? new-boxes)))
      state
      (assoc state :robot dest :boxes new-boxes))))

(defn simulate [grid moves]
  (loop [state (initial-state grid)
         mv moves]
    (if (empty? mv)
      state
      (recur (move-robot state (first mv)) (rest mv)))))

(defn gps [[r c]]
  (+ (* 100 r) c))

(defn part [lines]
  (let [grid (widen-map lines)
        moves (parse-moves lines)
        final-state (simulate grid moves)]
    (reduce + (map gps (:boxes final-state)))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
