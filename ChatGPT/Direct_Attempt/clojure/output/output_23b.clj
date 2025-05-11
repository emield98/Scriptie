;; ChatGPT-generated solution will go here.
(ns output_23b
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(defn build-graph [edges]
  (reduce (fn [g [a b]]
            (-> g
                (update a (fnil conj #{}) b)
                (update b (fnil conj #{}) a)))
          {}
          edges))

(defn bron-kerbosch
  [graph R P X]
  (lazy-seq
   (if (empty? P)
     (when (empty? X) [R])
     (mapcat (fn [v]
               (bron-kerbosch
                graph
                (conj R v)
                (set/intersection P (graph v))
                (set/intersection X (graph v))))
             P))))

(defn part [lines]
  (let [edges (map #(str/split % #"-") lines)
        graph (build-graph edges)
        nodes (set (keys graph))
        cliques (bron-kerbosch graph #{} nodes #{})
        largest (apply max-key count cliques)]
    (->> largest
         sort
         (str/join ","))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
