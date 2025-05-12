(ns output_18b
  (:require [clojure.string :as str]
            [clojure.set :as set])
  (:gen-class))

(def max-coord 70)

(def directions [[-1 0] [1 0] [0 -1] [0 1]])

(defn parse-coord [line]
  (mapv #(Integer/parseInt %) (str/split line #",")))

(defn is-reachable? [blocked]
  (let [start [0 0]
        end [max-coord max-coord]]
    (if (or (blocked start) (blocked end))
      false
      (loop [queue (conj clojure.lang.PersistentQueue/EMPTY start)
             visited #{start}]
        (if (empty? queue)
          false
          (let [[x y :as pos] (peek queue)
                queue (pop queue)]
            (if (= pos end)
              true
              (let [neighbors (for [[dx dy] directions
                                    :let [nx (+ x dx)
                                          ny (+ y dy)
                                          np [nx ny]]
                                    :when (and (<= 0 nx max-coord)
                                               (<= 0 ny max-coord)
                                               (not (blocked np))
                                               (not (visited np)))]
                                np)]
                (recur (into queue neighbors)
                       (into visited neighbors)))))))))

(defn part [lines]
  (loop [[line & rest] lines
         blocked #{}]
    (let [[x y :as coord] (parse-coord line)
          new-blocked (conj blocked coord)]
      (if (not (is-reachable? new-blocked))
        (str x "," y)
        (recur rest new-blocked)))))

(defn -main [& args]
  (let [filename (first args)
        lines (->> (slurp filename)
                   str/split-lines
                   (filter seq))]
    (println (part lines))))
