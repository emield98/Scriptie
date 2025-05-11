;; ChatGPT-generated solution will go here.
(ns output_13b
  (:require [clojure.string :as str]))

(defn parse-machine [chunk]
  (let [nums (map #(Long/parseLong (re-find #"-?\d+" %)) chunk)
        [[ax ay] [bx by] [px py]] (partition 2 nums)
        offset 10000000000000]
    {:a [ax ay], :b [bx by], :p [(+ px offset) (+ py offset)]}))

(defn parse-input [lines]
  (->> lines
       (partition-by str/blank?)
       (remove #(= (first %) ""))
       (map parse-machine)))

(defn extended-gcd [a b]
  (loop [r a, r' b, s 1N, s' 0N, t 0N, t' 1N]
    (if (zero? r')
      [r s t]
      (let [q (quot r r')]
        (recur r' (- r (* q r')) s' (- s (* q s')) t' (- t (* q t')))))))

(defn solve-machine [{:keys [a b p]}]
  (let [[ax ay] (map bigint a)
        [bx by] (map bigint b)
        [px py] (map bigint p)
        dx (- px)
        dy (- py)
        det (- (* ax by) (* ay bx))]
    (if (zero? det)
      nil
      (let [inv-m [[by (- ay)] [(- bx) ax]]
            [[x0] [y0]] (map #(quot (+ (* (first %) dx) (* (second %) dy)) det) inv-m)]
        (when (and (<= 0 x0) (<= 0 y0) (== (+ (* ax x0) (* bx y0)) px) (== (+ (* ay x0) (* by y0)) py))
          (+ (* 3 x0) y0))))))

(defn part [lines]
  (let [machines (parse-input lines)
        costs (keep solve-machine machines)]
    (reduce + costs)))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
