(ns aoc2024.day23b
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(defn parse-graph [lines]
  (->> lines
       (mapcat #(let [[a b] (str/split % #"-")]
                  [[a b] [b a]]))
       (reduce #(update %1 (first %2)
                        (fn [set] (conj (or set #{}) (second %2))))
               {})))

(defn bron-kerbosch-helper [g clique-candidate possible-extensions excluded]
  (if (and (empty? possible-extensions) (empty? excluded))
    [clique-candidate]
    (loop [cliques []
           possible-extensions possible-extensions
           excluded excluded]
      (if-let [v (first possible-extensions)]
        (recur (into cliques
                     (bron-kerbosch-helper
                      g
                      (conj clique-candidate v)
                      (set/intersection possible-extensions (g v))
                      (set/intersection excluded (g v))))
               (disj possible-extensions v)
               (conj excluded v))
        cliques))))

(defn bron-kerbosch [g]
  (bron-kerbosch-helper g #{} (set (keys g)) #{}))

(defn part-2 [lines]
  (->> lines
       parse-graph
       bron-kerbosch
       (apply max-key count)
       sort
       (str/join ",")))

(defn -main [& [file]]
  (let [lines (-> (or file "input") slurp str/split-lines)]
    (println (part-2 lines))))
