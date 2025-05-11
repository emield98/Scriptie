;; ChatGPT-generated solution will go here.
(ns output_20b
  (:require [clojure.string :as str]
            [clojure.data.priority-map :refer [priority-map]]))

(defn parse-map [lines]
  (let [grid (mapv vec lines)
        h (count grid)
        w (count (first grid))
        start (first (for [y (range h) x (range w) :when (= \S (get-in grid [y x]))] [y x]))
        end (first (for [y (range h) x (range w) :when (= \E (get-in grid [y x]))] [y x]))]
    {:grid grid :start start :end end :h h :w w}))

(defn in-bounds? [h w [y x]]
  (and (<= 0 y (dec h)) (<= 0 x (dec w))))

(defn neighbors [pos h w]
  (let [[y x] pos
        deltas [[0 1] [1 0] [0 -1] [-1 0]]]
    (filter #(in-bounds? h w %) (map (fn [[dy dx]] [(+ y dy) (+ x dx)]) deltas))))

(defn dijkstra [grid start h w wall-cost]
  (let [pq (atom (priority-map start 0))
        dist (atom {start 0})]
    (while (seq @pq)
      (let [[pos d] (peek @pq)]
        (swap! pq pop)
        (doseq [n (neighbors pos h w)]
          (let [c (get-in grid n)
                step (if (#{\. \S \E} c) 1 wall-cost)
                nd (+ d step)]
            (when (< nd (get @dist n Integer/MAX_VALUE))
              (swap! dist assoc n nd)
              (swap! pq assoc n nd)))))
    @dist))

(defn bfs-wall-walks [grid from h w max-steps]
  (let [q (atom (into clojure.lang.PersistentQueue/EMPTY [[from 0 []]]))
        seen (atom #{})
        results (atom [])]
    (while (seq @q)
      (let [[pos steps path] (peek @q)]
        (swap! q pop)
        (when (and (not (contains? @seen pos)) (<= steps max-steps))
          (swap! seen conj pos)
          (when (#{\. \S \E} (get-in grid pos))
            (swap! results conj {:start from :end pos :time steps}))
          (doseq [n (neighbors pos h w)]
            (when (= \# (get-in grid n))
              (swap! q conj [n (inc steps) (conj path n)])))))
    @results))

(defn part [lines]
  (let [{:keys [grid start end h w]} (parse-map lines)
        normal-dist (dijkstra grid start h w 1000000)
        best (get normal-dist end)
        cheat-paths (->> (for [y (range h) x (range w)
                               :when (= \. (get-in grid [y x]))]
                           [y x])
                         (mapcat #(bfs-wall-walks grid % h w 20))
                         (filter #(not= (:start %) (:end %)))
                         (distinct))
        counts (->> cheat-paths
                    (map #(let [start (:start %)
                                end (:end %)
                                t (:time %)
                                baseline (get normal-dist end Integer/MAX_VALUE)
                                saved (- baseline t)]
                            (when (>= saved 100) [start end])))
                    (filter some?)
                    set
                    count)]
    counts))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
