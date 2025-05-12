(ns output_13a
  (:require [clojure.string :as str]))

(defn extract-values [line]
  (let [[left right] (str/split line #",")
        x-part (if (str/includes? left "+")
                 (last (str/split left #"\+"))
                 (last (str/split left #"=")))
        y-part (if (str/includes? right "+")
                 (last (str/split right #"\+"))
                 (last (str/split right #"=")))]
    [(Long/parseLong (str/trim x-part))
     (Long/parseLong (str/trim y-part))]))

(defn parse-machines [lines]
  (->> lines
       (filter (complement str/blank?))
       (partition 3)
       (map (fn [[a b p]]
              [(vec (extract-values a))
               (vec (extract-values b))
               (vec (extract-values p))]))))

(defn part [lines]
  (let [machines (parse-machines lines)
        max-presses 100]
    (reduce
     (fn [total [[ax ay] [bx by] [px py]]]
       (loop [a 0 min-cost Long/MAX_VALUE]
         (if (> a max-presses)
           (if (< min-cost Long/MAX_VALUE) (+ total min-cost) total)
           (let [valid-cost (some
                             (fn [b]
                               (let [x (+ (* a ax) (* b bx))
                                     y (+ (* a ay) (* b by))]
                                 (when (and (= x px) (= y py))
                                   (+ (* a 3) (* b 1)))))
                             (range (inc max-presses)))]
             (recur (inc a) (if valid-cost (min min-cost valid-cost) min-cost))))))
     0
     machines)))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
