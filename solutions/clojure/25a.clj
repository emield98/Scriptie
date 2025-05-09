(ns aoc2024.day25
  (:require [clojure.string :as str]))

;; --- Minimal vec2 + Grid setup ---
(defrecord vec2 [^long row ^long col])
(defn ->vec2 [r c] (vec2. r c))

(deftype Grid [arr ^long height ^long width]
  clojure.lang.Associative
  (containsKey [_ k] (let [r (:row k) c (:col k)] (and (>= r 0) (< r height) (>= c 0) (< c width))))
  (entryAt [_ k] (let [i (+ (:col k) (* width (:row k)))] (clojure.lang.MapEntry. k (.nth arr i))))
  (assoc [_ k v] (let [i (+ (:col k) (* width (:row k)))] (Grid. (.assoc arr i v) height width)))
  (valAt [_ k] (let [i (+ (:col k) (* width (:row k)))] (.nth arr i)))
  (valAt [this k default] (if (.containsKey this k) (.valAt this k) default))
  clojure.lang.Seqable
  (seq [_] (for [r (range height) c (range width)]
             (let [k (->vec2 r c)
                   i (+ c (* r width))]
               (clojure.lang.MapEntry. k (.nth arr i)))))
  clojure.lang.IFn
  (invoke [this k] (.valAt this k))
  (invoke [this k default] (.valAt this k default)))

(defn lines->grid [lines]
  (let [height (count lines)
        width (count (first lines))]
    (Grid. (into [] (apply concat lines)) height width)))

(defn all-coords-of [^Grid g pred]
  (->> g (filter (fn [[_ v]] (pred v))) (map first)))
(defn width [^Grid g] (.width g))

;; --- Problem-specific logic ---
(defn inp-kind [grid]
  (if (every? #(= \# (grid (->vec2 0 %))) (range (width grid)))
    :lock
    :key))

(defn parse-input [s]
  (->> (str/split s #"\n\n")
       (map #(lines->grid (str/split % #"\n")))
       (group-by inp-kind)))

(defn heights [chooser extremum grid]
  (->> (all-coords-of grid #(= % \#))
       (reduce
         (fn [h ^vec2 point]
           (update h (.col point) #(chooser (or % extremum) (.row point))))
         (sorted-map))))

(def MAX-HEIGHT 7)

(defn part-1 [s]
  (let [parsed (parse-input (str/trim s))
        locks (map (comp (fn [x] (update-vals x inc))
                         (partial heights max 0))
                   (:lock parsed))
        keys (map (comp (fn [x] (update-vals x (fn [y] (- MAX-HEIGHT y))))
                        (partial heights min Long/MAX_VALUE))
                  (:key parsed))]
    (->> (for [l locks
               k keys
               :when (every? #(<= % MAX-HEIGHT)
                             (map + (vals k) (vals l)))]
           1)
         count)))

(defn -main [& args]
  (let [filename (first args)
        input (slurp filename)]
    (println (part-1 input))))
