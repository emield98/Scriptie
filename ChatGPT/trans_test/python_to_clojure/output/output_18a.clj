(ns output_18a
  (:require [clojure.string :as str]
            [clojure.set :as set])
  (:gen-class))

(defn part [lines]
  (let [max-coord 70
        parse-line (fn [line] (mapv #(Integer/parseInt %) (str/split line #",")))
        blocked (into #{} (map parse-line (take 1024 lines)))
        start [0 0]
        end [max-coord max-coord]
        directions [[-1 0] [1 0] [0 -1] [0 1]]]
    (loop [queue (clojure.lang.PersistentQueue/EMPTY)
           queue (conj queue [0 0 0])
           visited #{start}]
      (if (empty? queue)
        "No path found"
        (let [[x y steps] (peek queue)
              queue (pop queue)]
          (if (= [x y] end)
            steps
            (let [neighbors (for [[dx dy] directions
                                  :let [nx (+ x dx)
                                        ny (+ y dy)
                                        pos [nx ny]]
                                  :when (and (<= 0 nx max-coord)
                                             (<= 0 ny max-coord)
                                             (not (blocked pos))
                                             (not (visited pos)))]
                              [nx ny (inc steps)])
                  new-visited (into visited (map #(vector (nth % 0) (nth % 1)) neighbors))
                  new-queue (reduce conj queue neighbors)]
              (recur new-queue new-visited))))))))

(defn -main [& args]
  (let [filename (first args)
        lines (->> (slurp filename)
                   str/split-lines
                   (filter seq))]
    (println (part lines))))
