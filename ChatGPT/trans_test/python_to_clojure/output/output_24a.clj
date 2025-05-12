(ns output_24a
  (:require [clojure.string :as str])
  (:gen-class))

(defn parse-line [line]
  (if (str/includes? line ":")
    (let [[wire val] (str/split line #":")]
      {:type :value
       :wire (str/trim wire)
       :val (Integer/parseInt (str/trim val))})
    (let [[_ in1 op in2 out] (re-matches #"(\w+)\s+(AND|OR|XOR)\s+(\w+)\s+->\s+(\w+)" line)]
      {:type :gate
       :in1 in1
       :op op
       :in2 in2
       :out out})))

(defn op-fn [op]
  (case op
    "AND" bit-and
    "OR" bit-or
    "XOR" bit-xor))

(defn process-gates [initial-values gates]
  (loop [wire-values initial-values
         pending gates]
    (let [result (reduce
                   (fn [[vals next] {:keys [in1 in2 op out] :as gate}]
                     (if (and (contains? vals in1) (contains? vals in2))
                       (let [a (vals in1)
                             b (vals in2)
                             f (op-fn op)]
                         [(assoc vals out (f a b)) next])
                       [vals (conj next gate)]))
                   [wire-values []]
                   pending)
          [new-values remaining] result]
      (if (= (count remaining) (count pending))
        new-values
        (recur new-values remaining)))))

(defn part [lines]
  (let [parsed (map parse-line lines)
        init-values (into {} (for [{:keys [type wire val]} parsed
                                   :when (= type :value)]
                               [wire val]))
        gates (filter #(= (:type %) :gate) parsed)
        wire-values (process-gates init-values gates)
        z-wires (sort (filter #(str/starts-with? % "z") (keys wire-values)))
        binary-str (apply str (map #(str (wire-values %)) (reverse z-wires)))]
    (Integer/parseInt binary-str 2)))

(defn -main [& args]
  (let [filename (first args)
        lines (->> (slurp filename)
                   str/split-lines
                   (filter seq))]
    (println (part lines))))
