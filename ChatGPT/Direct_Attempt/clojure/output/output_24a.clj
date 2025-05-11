;; ChatGPT-generated solution will go here.
(ns output_24a
  (:require [clojure.string :as str]
            [clojure.set :as set]))

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

(defn part [lines]
  (let [[initial gates] (parse-input lines)
        state (evaluate initial gates)
        z-wires (->> state
                     (filter (fn [[k _]] (str/starts-with? k "z")))
                     (sort-by (fn [[k _]]
                                (parse-long (subs k 2)))))
        bits (mapv second z-wires)]
    (reduce (fn [acc b] (+ (* acc 2) b)) 0 bits)))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
