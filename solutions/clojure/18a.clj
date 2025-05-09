(ns aoc2024.day18
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [clojure.data.priority-map :refer [priority-map-keyfn]]))

;; --- Inlined utility functions ---
(defn to-lines [input]
  (str/split-lines input))

(defn parse-out-longs [line]
  (map #(Long/parseLong %)
       (re-seq #"[-+]?\d+" line)))

(defn cardinal-neighbors [[row col]]
  [{:coord [(dec row) col] :dir :up}
   {:coord [(inc row) col] :dir :down}
   {:coord [row (dec col)] :dir :left}
   {:coord [row (inc col)] :dir :right}])

(defn out-of-bounds [row-bound col-bound]
  (fn [[row col]]
    (or (>= row row-bound) (< row 0)
        (>= col col-bound) (< col 0))))

(defn dissoc-by [pred m]
  (apply dissoc m (filter pred (keys m))))

(defn- merge-costs [[curr-cost curr-prevs :as curr] [new-cost new-prevs :as new]]
  (cond (= curr-cost new-cost) [curr-cost (set/union curr-prevs new-prevs)]
        (< new-cost curr-cost) new
        :else curr))

(defn dijkstra
  "Dijkstra's algorithm for shortest paths"
  [start target nbrs-fn & {:keys [alt-targets] :or {alt-targets #{}}}]
  (loop [q (priority-map-keyfn first start [0 #{}])
         res {}]
    (let [[node [cost :as cost-and-prevs]] (peek q)]
      (cond (not (seq q)) res
            (or (= node target) (alt-targets node))
            (assoc res node cost-and-prevs)
            :else
            (let [new-costs (->> (nbrs-fn node)
                                 (dissoc-by #(res %))
                                 (#(update-vals % (partial + cost))))]
              (recur (merge-with merge-costs
                                 (pop q)
                                 (update-vals new-costs #(vector % (set [node]))))
                     (assoc res node cost-and-prevs)))))))

;; --- Core Day 18 Logic ---
(def TARGET [70 70])
(def NUM_BYTES 1024)
(def is-out-of-bounds? (out-of-bounds (inc (first TARGET)) (inc (last TARGET))))

(defn- get-neighbors-dijkstra [bytes coord]
  (apply merge
         (map #(assoc {} % 1)
              (keep #(when-not (or (is-out-of-bounds? (:coord %))
                                   (bytes (:coord %)))
                       (:coord %))
                    (cardinal-neighbors coord)))))

(defn part-1 [input]
  (let [bytes (->> input
                   to-lines
                   (take NUM_BYTES)
                   (map (comp vec reverse parse-out-longs))
                   (set))
        graph (dijkstra [0 0] TARGET (partial get-neighbors-dijkstra bytes))]
    (first (graph TARGET))))

(defn -main [& [file]]
  (println (part-1 (slurp (or file "input")))))
