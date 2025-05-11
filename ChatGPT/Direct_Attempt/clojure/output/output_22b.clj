;; ChatGPT-generated solution will go here.
(ns output_22b
  (:require [clojure.string :as str]))

(defn mix [secret val]
  (bit-xor secret val))

(defn prune [secret]
  (mod secret 16777216))

(defn next-secret [secret]
  (-> secret
      (mix (* secret 64))
      prune
      (#(mix % (quot % 32)))
      prune
      (#(mix % (* % 2048)))
      prune))

(defn price-seq [start n]
  (->> (iterate next-secret start)
       (take (inc n))
       (map #(mod % 10))))

(defn price-deltas [prices]
  (map - (rest prices) prices))

(defn find-sell-price [changes prices pattern]
  (let [windowed (partition 4 1 changes)]
    (loop [i 0, win windowed]
      (cond
        (empty? win) nil
        (= (first win) pattern) (nth prices (+ i 4))
        :else (recur (inc i) (rest win))))))

(defn all-patterns []
  (for [a (range -9 10)
        b (range -9 10)
        c (range -9 10)
        d (range -9 10)]
    [a b c d]))

(defn part [lines]
  (let [starts (map #(Long/parseLong %) lines)
        price-data (map #(let [ps (price-seq % 2000)
                               ds (price-deltas ps)]
                           {:prices ps :deltas ds})
                        starts)
        patterns (all-patterns)]
    (->> patterns
         (map (fn [pat]
                (->> price-data
                     (map #(find-sell-price (:deltas %) (:prices %) pat))
                     (remove nil?)
                     (reduce + 0))))
         (apply max))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
