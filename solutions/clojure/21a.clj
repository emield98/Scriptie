(ns aoc2024.day21
  (:require [clojure.string :as str]))

;; --- Minimal vec2 implementation ---
(defrecord vec2 [^long row ^long col])
(defn ->vec2 [r c] (vec2. r c))

;; --- Constants: keypad layouts ---
(def numpad {\7 (->vec2 0 0), \8 (->vec2 0 1), \9 (->vec2 0 2)
             \4 (->vec2 1 0), \5 (->vec2 1 1), \6 (->vec2 1 2)
             \1 (->vec2 2 0), \2 (->vec2 2 1), \3 (->vec2 2 2)
             \0 (->vec2 3 1), \A (->vec2 3 2)})

(def dirpad {:up (->vec2 0 1), \A (->vec2 0 2), :left (->vec2 1 0)
             :down (->vec2 1 1), :right (->vec2 1 2)})

(def NUMPAD-SPACE (->vec2 3 0))
(def DIRPAD-SPACE (->vec2 0 0))

;; --- Press tracking initialization ---
(defn build-initial-button-presses [height]
  (-> (for [from (keys dirpad), to (keys dirpad)]
        [from to (inc height)])
      (zipmap (repeat 1))))

;; --- Movement + DP logic ---
(defn move-arm-incremental [coord-map space-coord from to]
  (let [a (coord-map from)
        b (coord-map to)
        dr (- (:row b) (:row a))
        dc (- (:col b) (:col a))
        dir1 (if (< dr 0) :up :down)
        dir2 (if (< dc 0) :left :right)]
    (->> [[(->vec2 (:row b) (:col a)) (concat (repeat (Math/abs dr) dir1) (repeat (Math/abs dc) dir2))]
          [(->vec2 (:row a) (:col b)) (concat (repeat (Math/abs dc) dir2) (repeat (Math/abs dr) dir1))]]
         (filter #(not= space-coord (first %)))
         (mapv second))))

(defn update-presses-for-height [coord-map space height n-moves-to-press]
  (->> (for [from (keys coord-map), to (keys coord-map)]
         (->> (move-arm-incremental coord-map space from to)
              (map #(->> (cons \A (concat % [\A]))
                         (partition 2 1)
                         (map (fn [[from* to*]] (n-moves-to-press [from* to* (inc height)])))
                         (reduce +)))
              (apply min)
              (vector [from to height])))
       (into n-moves-to-press)))

(defn dirpad-moves-dp [height code]
  (loop [height height, n-moves-to-press (build-initial-button-presses height)]
    (cond
      (== height -1)
      (->> (cons \A code)
           (partition 2 1)
           (map (fn [[from to]] (n-moves-to-press [from to 0])))
           (reduce +))

      (zero? height)
      (recur (dec height)
             (update-presses-for-height numpad NUMPAD-SPACE height n-moves-to-press))

      :else
      (recur (dec height)
             (update-presses-for-height dirpad DIRPAD-SPACE height n-moves-to-press)))))

(defn complexity [depth code]
  (let [presses (dirpad-moves-dp depth code)
        digits (subs code 0 (dec (count code)))
        value (Long/parseLong digits)]
    (* value presses)))

(defn part-1 [input]
  (->> (str/split-lines input)
       (map (partial complexity 2))
       (reduce +)))

(defn -main [& [file]]
  (let [input (slurp (or file "input"))]
    (println (part-1 input))))
