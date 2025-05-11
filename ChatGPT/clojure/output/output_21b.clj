;; ChatGPT-generated solution will go here.
(ns output_21b
  (:require [clojure.string :as str]
            [clojure.data.priority-map :refer [priority-map]]))

(def dirs {\^ [-1 0] \v [1 0] \< [0 -1] \> [0 1]})

(def numeric-keypad
  {\7 [0 0] \8 [0 1] \9 [0 2]
   \4 [1 0] \5 [1 1] \6 [1 2]
   \1 [2 0] \2 [2 1] \3 [2 2]
   \0 [3 1] \A [3 2]})

(defn rev-keypad [kmap]
  (into {} (map (fn [[k v]] [v k]) kmap)))

(def rev-numeric-keypad (rev-keypad numeric-keypad))

(defn neighbors [pos grid]
  (for [[d [dr dc]] dirs
        :let [[r c] [(+ dr (first pos)) (+ dc (second pos))]]
        :when (contains? grid [r c])]
    [[r c] d]))

(defn bfs-path [start goal grid]
  (loop [q (priority-map [start []] 0)
         seen #{}]
    (let [[[pos path] _] (peek q)
          q (pop q)]
      (cond
        (= pos goal) path
        (seen pos) (recur q seen)
        :else
        (let [nbs (for [[p d] (neighbors pos grid)]
                    [[p (conj path d)] (inc (count path)))])]
          (recur (into q nbs) (conj seen pos)))))))

(defn press-seq [start-code keypad]
  (loop [cur (get keypad \A)
         acc []
         [c & cs] start-code]
    (if-not c acc
      (let [next (get keypad c)
            moves (bfs-path cur next keypad)]
        (recur next (into acc (conj moves \A)) cs)))))

(defn repeat-layer [code times keypad]
  (reduce (fn [acc _] (press-seq acc keypad)) code (range times)))

(defn part [lines]
  (let [codes (filter #(not (str/includes? % ",")) lines)
        digits (map seq codes)
        base (map #(press-seq % numeric-keypad) digits)
        deep (map #(repeat-layer % 25 (rev-keypad dirs)) base)
        lens (map count deep)
        nums (map #(Integer/parseInt (apply str (drop-last %))) digits)]
    (reduce + (map * lens nums))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
