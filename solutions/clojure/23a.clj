(ns aoc2024.day23
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(defn parse-graph [lines]
  (->> lines
       (mapcat #(let [[a b] (str/split % #"-")]
                  [[a b] [b a]]))
       (reduce #(update %1 (first %2)
                        (fn [set] (conj (or set #{}) (second %2))))
               {})))

(defn dissoc-vert [g v]
  (as-> (g v) $
        (reduce #(update %1 %2 (fn [set] (disj set v))) g $)
        (dissoc $ v)))

(defn clique-3 [g]
  (loop [g g
         vs (sort-by (comp - count g) (keys g))
         triangles []]
    (if-let [v (first vs)]
      (let [neighs (g v)
            neighs2 (into #{}
                          (mapcat #(->> (set/intersection neighs (g %))
                                        (map (fn [n2] #{ % n2 }))))
                          neighs)]
        (recur (dissoc-vert g v)
               (rest vs)
               (into triangles
                     (map #(conj % v) neighs2))))
      triangles)))

(defn part-1 [lines]
  (->> lines
       parse-graph
       clique-3
       (filter #(some (fn [name] (str/starts-with? name "t")) %))
       count))

(defn -main [& [file]]
  (let [lines (-> (or file "input") slurp str/split-lines)]
    (println (part-1 lines))))
