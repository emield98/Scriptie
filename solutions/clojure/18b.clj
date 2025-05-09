(ns aoc2024.day18b
  (:require [clojure.string :as str]))

;; --- Inlined utility functions ---
(defn to-lines [input]
  (str/split-lines input))

(defn parse-out-longs [line]
  (map #(Long/parseLong %) (re-seq #"[-+]?\d+" line)))

(defn out-of-bounds [row-bound col-bound]
  (fn [[row col]]
    (or (>= row row-bound) (< row 0)
        (>= col col-bound) (< col 0))))

(defn cardinal-neighbors [[row col]]
  [{:coord [(dec row) col] :dir :up}
   {:coord [(inc row) col] :dir :down}
   {:coord [row (dec col)] :dir :left}
   {:coord [row (inc col)] :dir :right}])

(defn dfs
  "Depth First Search: returns a map of reachable nodes with paths from start."
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

;; --- Core Day 18b Logic ---
(def TARGET [70 70])
(def NUM_BYTES 1024)
(def is-out-of-bounds? (out-of-bounds (inc (first TARGET)) (inc (last TARGET))))

(defn- get-neighbors-dfs [bytes coord]
  (keep #(when-not (or (is-out-of-bounds? (:coord %))
                       (bytes (:coord %)))
           (:coord %))
        (cardinal-neighbors coord)))

(defn- find-path [bytes start target]
  ((dfs start target #(get-neighbors-dfs bytes %)) target))

(defn- find-first-failing-byte [bytes]
  (loop [[byte & rem] (drop NUM_BYTES bytes)
         seen-bytes (set (take NUM_BYTES bytes))]
    (let [new-bytes (conj seen-bytes byte)]
      (if-let [path (find-path new-bytes [0 0] TARGET)]
        (recur rem new-bytes)
        byte))))

(defn part-2 [input]
  (let [bytes (->> input
                   to-lines
                   (map parse-out-longs)
                   (map vec))]
    (vec (find-first-failing-byte bytes))))

(defn -main [& [file]]
  (let [[r c] (part-2 (slurp (or file "input")))]
    (println (str "[" r ", " c "]"))))
