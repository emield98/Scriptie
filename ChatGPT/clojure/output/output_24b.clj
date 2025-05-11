(ns output_24b
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [clojure.math.combinatorics :as combo]))

(defn parse-input [lines]
  (let [[inits gates] (split-with #(str/includes? % ":") lines)
        initial (into {}
                      (map (fn [line]
                             (let [[k v] (str/split line #": ")]
                               [k (parse-long v)]))
                           inits))
        gate-list (map (fn [line]
                         (let [[lhs out] (str/split line #" -> ")
                               [a op b] (str/split lhs #" ")]
                           {:op op :a a :b b :out out}))
                       gates)]
    [initial gate-list]))

(defn ready? [state a b]
  (and (contains? state a) (contains? state b)))

(defn compute [op va vb]
  (case op
    "AND" (if (and (= va 1) (= vb 1)) 1 0)
    "OR" (if (or (= va 1) (= vb 1)) 1 0)
    "XOR" (if (not= va vb) 1 0)))

(defn evaluate [initial gates]
  (loop [state initial
         remaining gates]
    (let [[ready not-ready] (split-with #(ready? state (:a %) (:b %)) remaining)
          new-state (reduce (fn [s {:keys [a b op out]}]
                              (assoc s out (compute op (s a) (s b))))
                            state
                            ready)]
      (if (empty? not-ready)
        new-state
        (recur new-state not-ready)))))

(defn wires->binary [state prefix]
  (->> state
       (filter (fn [[k _]] (str/starts-with? k prefix)))
       (map (fn [[k v]] [(parse-long (subs k 2)) v]))
       (sort-by first)
       (mapv second)))

(defn bin->int [bits]
  (reduce (fn [acc b] (+ (* acc 2) b)) 0 bits))

(defn int->bin [n width]
  (reverse (map #(bit-and 1 (bit-shift-right n %)) (range width))))

(defn evaluate-with [gates x-bits y-bits]
  (let [x-init (into {} (map-indexed (fn [i b] [(format "x%02d" i) b]) x-bits))
        y-init (into {} (map-indexed (fn [i b] [(format "y%02d" i) b]) y-bits))
        state (evaluate (merge x-init y-init) gates)]
    (wires->binary state "z")))

(defn find-z-wires [gates]
  (->> gates
       (map :out)
       (filter #(str/starts-with? % "z"))
       sort
       vec))

(defn bit-length [wires]
  (->> wires
       (map #(parse-long (subs % 1)))
       (apply max)
       inc))

(defn swap-outs [gates a b]
  (map (fn [g]
         (cond
           (= (:out g) a) (assoc g :out b)
           (= (:out g) b) (assoc g :out a)
           :else g))
       gates))

(defn part [lines]
  (let [[_ gates] (parse-input lines)
        z-wires (find-z-wires gates)
        w (bit-length z-wires)
        correct? (fn [gs]
                   (let [x (rand-int (bit-shift-left 1 w))
                         y (rand-int (bit-shift-left 1 w))
                         x-bits (int->bin x w)
                         y-bits (int->bin y w)
                         out (evaluate-with gs x-bits y-bits)
                         expected (int->bin (+ x y) (count out))]
                     (= expected out)))
        all-outs (map :out gates)
        candidate-pairs (for [a all-outs, b all-outs
                              :when (and (not= a b) (str/starts-with? a "z") (str/starts-with? b "z"))]
                          [a b])
        valid-swaps (->> (combo/combinations candidate-pairs 4)
                         (filter (fn [pairs]
                                   (let [outs (flatten pairs)]
                                     (= 8 (count (set outs))))))
                         (filter (fn [swaps]
                                   (let [swapped (reduce (fn [gs [a b]] (swap-outs gs a b)) gates swaps)]
                                     (every? identity (repeatedly 10 #(correct? swapped)))))))
        answer (->> valid-swaps
                    first
                    flatten
                    sort
                    (str/join ","))]
    answer))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
