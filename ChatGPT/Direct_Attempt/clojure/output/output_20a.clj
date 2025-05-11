;; ChatGPT-generated solution will go here.
(ns output_20a
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [clojure.data.priority-map :refer [priority-map]]))

(defn parse-map [lines]
  (let [grid (mapv vec lines)
        height (count grid)
        width (count (first grid))
        start (first (for [y (range height)
                           x (range width)
                           :when (= (get-in grid [y x]) \S)]
                       [y x]))
        end (first (for [y (range height)
                         x (range width)
                         :when (= (get-in grid [y x]) \E)]
                     [y x]))]
    {:grid grid :start start :end end :height height :width width}))

(defn in-bounds? [h w [y x]]
  (and (<= 0 y (dec h))
       (<= 0 x (dec w))))

(defn bfs [grid start end h w cheat?]
  (let [dirs [[0 1] [1 0] [0 -1] [-1 0]]
        visited (atom #{})
        q (atom (priority-map [start false] 0))
        dist (atom {[start false] 0})
        prev (atom {})]
    (while (seq @q)
      (let [[[pos cheating] d] (peek @q)]
        (swap! q pop)
        (when-not (contains? @visited [pos cheating])
          (swap! visited conj [pos cheating])
          (when (not= pos end)
            (doseq [[dy dx] dirs]
              (let [npos [(+ dy (first pos)) (+ dx (second pos))]
                    cell (when (in-bounds? h w npos) (get-in grid npos))]
                (when cell
                  (cond
                    (or (= cell \.) (= cell \E) (= cell \S))
                    (let [nd (+ d 1)]
                      (when (< nd (get @dist [npos cheating] Integer/MAX_VALUE))
                        (swap! dist assoc [npos cheating] nd)
                        (swap! prev assoc [npos cheating] [pos cheating])
                        (swap! q assoc [npos cheating] nd)))

                    (and (= cell \#) (not cheating))
                    (let [nd (+ d 1)]
                      (when (< nd (get @dist [npos true] Integer/MAX_VALUE))
                        (swap! dist assoc [npos true] nd)
                        (swap! prev assoc [npos true] [pos cheating])
                        (swap! q assoc [npos true] nd)))))))))
    {:distance @dist :prev @prev}))

(defn path-length [prev-map end]
  (loop [curr [end false] acc 0]
    (if-let [p (get prev-map curr)]
      (recur p (inc acc))
      acc)))

(defn part [lines]
  (let [{:keys [grid start end height width]} (parse-map lines)
        shortest (path-length (:prev (bfs grid start end height width false)) end)
        {:keys [distance]} (bfs grid start end height width true)]
    (->> distance
         (filter (fn [[[pos cheating] d]]
                   (and cheating (= pos end) (>= (- shortest d) 100))))
         count)))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
