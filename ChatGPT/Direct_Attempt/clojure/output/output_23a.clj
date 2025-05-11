;; ChatGPT-generated solution will go here.
(ns output_23a
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(defn build-graph [edges]
  (reduce (fn [g [a b]]
            (-> g
                (update a (fnil conj #{}) b)
                (update b (fnil conj #{}) a)))
          {}
          edges))

(defn find-triangles [graph]
  (let [nodes (keys graph)]
    (for [a nodes
          b (graph a)
          :when (str/compare a b) ; prevent duplicate sets
          c (graph b)
          :when (and (not= a c)
                     (contains? (graph a) c)
                     (compare b c))] ; ensure unique triangle
      (->> [a b c] sort vec))))

(defn part [lines]
  (let [edges (map #(str/split % #"-") lines)
        graph (build-graph edges)
        triangles (find-triangles graph)]
    (->> triangles
         (filter #(some (fn [n] (str/starts-with? n "t")) %))
         count)))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
