;; src/aoc2024/day14b.clj
(ns aoc2024.day14b
  (:require [clojure.string :as str]
            [clojure.math :as m])
  (:gen-class))

;; Fixed grid dimensions from problem statement
(def WIDTH  101)
(def HEIGHT 103)

;; Parse input lines into vector of maps {:pos [x y] :vel [vx vy]}
(defn parse-lines [content]
  (->> (str/split-lines content)
       (remove str/blank?)
       (map #(re-seq #"-?\d+" %))
       (filter #(= 4 (count %)))
       (mapv (fn [[x y vx vy]]
               {:pos [(Long/parseLong x) (Long/parseLong y)]
                :vel [(Long/parseLong vx) (Long/parseLong vy)]}))))

;; Advance a single robot with wrap-around
(defn advance [robot]
  (let [{[x y] :pos [vx vy] :vel} robot]
    (assoc robot :pos [(mod (+ x vx) WIDTH)
                       (mod (+ y vy) HEIGHT)])))

;; Compute combined Shannon entropy of X and Y position distributions
(defn entropies [robots]
  (let [n   (count robots)
        xs  (->> robots (map (comp first :pos)) frequencies vals)
        ys  (->> robots (map (comp second :pos)) frequencies vals)
        entropy (fn [counts]
                  (reduce (fn [acc c]
                            (- acc (* (/ c n) (m/log (/ c n)))))
                          0
                          counts))]
    (+ (entropy xs) (entropy ys))))

;; Part 2: find the fewest seconds until robots display the Easter egg (minimal entropy)
(defn part-2 [content]
  (let [robots   (parse-lines content)
        period   (* WIDTH HEIGHT)
        states   (take period (iterate (fn [rs] (mapv advance rs)) robots))
        ent-list (map-indexed (fn [i st] [i (entropies st)]) states)]
    (first (apply min-key second ent-list))))

;; Entry point for Part 2
(defn -main [& args]
  (if-let [file (first args)]
    (println (part-2 (slurp file)))))
