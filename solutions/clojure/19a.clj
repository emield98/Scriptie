(ns aoc2024.day19
  (:require [clojure.string :as str]))

;; --- Inlined utility functions ---
(defn to-blocks [input]
  (str/split input #"\n\n"))

(defn to-lines [input]
  (str/split-lines input))

(defn re-pos
  "Return a list of pairs of (index, string) for all matches of `re` in `s`"
  [re s]
  (loop [m (re-matcher re s), res ()]
    (if (.find m)
      (recur m (cons (list (.start m) (.group m)) res))
      (reverse res))))

(defn dfs
  "Depth First Search for paths from start to target using `nbrs-fn`"
  [start target nbrs-fn]
  (loop [q [[start []]], res {}]
    (let [[curr path] (peek q)]
      (cond
        (nil? curr) res
        (= curr target) (assoc res target (conj path target))
        (res curr) (recur (pop q) res)
        :else
        (recur (vec (concat (pop q)
                            (map #(vector % (conj path curr))
                                 (nbrs-fn curr))))
               (assoc res curr (conj path curr)))))))

;; --- Core logic for day19 ---
(defn- get-next-towels [towel-positions pattern-len [idx towel]]
  (let [next-towel-pos (+ idx (count towel))]
    (if (= next-towel-pos pattern-len)
      [[pattern-len ""]]
      (get towel-positions next-towel-pos []))))

(defn- is-pattern-possible? [towels pattern]
  (let [towel-positions (group-by first (mapcat #(re-pos (re-pattern %) pattern) towels))
        pattern-len (count pattern)
        target [pattern-len ""]]
    ((dfs [0 ""] target (partial get-next-towels towel-positions pattern-len)) target)))

(defn part-1 [input]
  (let [blocks (to-blocks input)
        towels (set (str/split (first blocks) #", "))
        patterns (to-lines (last blocks))]
    (count (filter (partial is-pattern-possible? towels) patterns))))

(defn -main [& [file]]
  (println (part-1 (slurp (or file "input")))))
