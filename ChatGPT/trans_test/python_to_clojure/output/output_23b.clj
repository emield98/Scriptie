(ns output_23b
  (:require [clojure.string :as str]
            [clojure.set :as set])
  (:gen-class))

(defn parse-edges [lines]
  (map #(str/split % #"-") lines))

(defn build-graph [edges]
  (reduce (fn [g [a b]]
            (-> g
                (update a (fnil conj #{}) b)
                (update b (fnil conj #{}) a)))
          {}
          edges))

(defn is-clique? [graph clique]
  (every? (fn [[a b]] (contains? (graph a) b))
          (for [a clique, b clique :when (not= a b)] [a b])))

(defn dfs [graph clique candidates max-clique]
  (let [new-max (if (> (count clique) (count @max-clique))
                  (do (reset! max-clique clique) clique)
                  @max-clique)]
    (doseq [[i node] (map-indexed vector candidates)]
      (let [new-clique (conj clique node)
            new-candidates (filter #(every? (fn [x] (contains? (graph %) x)) new-clique)
                                   (drop (inc i) candidates))]
        (dfs graph new-clique new-candidates max-clique)))))

(defn part [lines]
  (let [edges (parse-edges lines)
        graph (build-graph edges)
        nodes (vec (keys graph))
        max-clique (atom [])]
    (dfs graph [] nodes max-clique)
    (->> @max-clique sort (str/join ","))))

(defn -main [& args]
  (let [filename (first args)
        lines (->> (slurp filename)
                   str/split-lines
                   (filter seq))]
    (println (part lines))))
