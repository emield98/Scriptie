;; ChatGPT-generated solution will go here.
(ns output_13a
  (:require [clojure.string :as str]))

(defn parse-machine [chunk]
  (let [nums (map #(Long/parseLong (re-find #"-?\d+" %)) chunk)
        [[ax ay] [bx by] [px py]] (partition 2 nums)]
    {:a [ax ay], :b [bx by], :p [px py]}))

(defn parse-input [lines]
  (->> lines
       (partition-by str/blank?)
       (remove #(= (first %) ""))
       (map parse-machine)))

(defn try-machine [{:keys [a b p]}]
  (let [[ax ay] a
        [bx by] b
        [px py] p
        max-presses 100]
    (loop [x 0, best nil]
      (if (> x max-presses)
        best
        (let [rx (- px (* x ax))
              ry (- py (* x ay))]
          (if (and (zero? bx) (not (zero? rx)))
            (recur (inc x) best)
            (if (and (zero? by) (not (zero? ry)))
              (recur (inc x) best)
              (let [b-x (if (zero? bx) nil (/ rx bx))
                    b-y (if (zero? by) nil (/ ry by))]
                (if (and b-x b-y (= b-x b-y) (<= 0 b-x max-presses) (== (long b-x) b-x))
                  (let [y (long b-x)
                        cost (+ (* x 3) y)]
                    (recur (inc x) (if (or (nil? best) (< cost best)) cost best)))
                  (recur (inc x) best)))))))))

(defn part [lines]
  (let [machines (parse-input lines)
        token-costs (keep try-machine machines)]
    (reduce + token-costs)))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
