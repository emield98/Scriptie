(ns aoc2024.day16)

;; --- Inlined utils ---
(require '[clojure.string :as str])
(require '[clojure.set :as set])
(require '[clojure.data.priority-map :refer [priority-map-keyfn]])

(defn to-matrix [input]
  (->> (str/split-lines input)
       (mapv vec)))

(defn matrix->map [matrix]
  (into {}
        (for [row (range (count matrix))
              col (range (count (first matrix)))
              :let [coord [row col]]]
          [coord (get-in matrix coord)])))

(defn cardinal-neighbors [[row col]]
  (let [dirs [{:coord [(dec row) col] :dir :up}
              {:coord [(inc row) col] :dir :down}
              {:coord [row (dec col)] :dir :left}
              {:coord [row (inc col)] :dir :right}]]
    dirs))

(defn dissoc-by [pred m]
  (apply dissoc m (filter pred (keys m))))

(defn- merge-costs [[curr-cost curr-prevs :as curr] [new-cost new-prevs :as new]]
  (cond (= curr-cost new-cost) [curr-cost (set/union curr-prevs new-prevs)]
        (< new-cost curr-cost) new
        :else curr))

(defn dijkstra
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

;; --- Core Day 16 Logic ---
(def valid-dirs {:left #{:left :up :down}
                 :up #{:up :left :right}
                 :right #{:right :up :down}
                 :down #{:down :left :right}})

(defn- get-neighbors [walls [coord dir]]
  (apply merge
         (map
          (fn [nbr]
            (if-not (= dir (:dir nbr))
              (assoc {} [coord (:dir nbr)] 1000)
              (assoc {} [(:coord nbr) dir] 1)))
          (remove (comp walls :coord)
                  (filter (comp (valid-dirs dir) :dir)
                          (cardinal-neighbors coord))))))

(defn part-1 [input]
  (let [maze (matrix->map (to-matrix input))
        walls (set (keep #(when (= \# (val %)) (key %)) maze))
        start (some #(when (= \S (val %)) (key %)) maze)
        target (some #(when (= \E (val %)) (key %)) maze)
        graph (dijkstra [start :right]
                        target
                        (partial get-neighbors walls))]
    (ffirst (keep #(when (= target (ffirst %)) (last %)) graph))))

(defn -main [& [file]]
  (println (part-1 (slurp (or file "input")))))
