(ns output_22a
  (:require [clojure.string :as str])
  (:gen-class))

(def mod-val 16777216)

(defn next-secret [secret]
  (-> secret
      (bit-xor (* secret 64))
      (mod mod-val)
      (#(bit-xor % (quot % 32)))
      (mod mod-val)
      (#(bit-xor % (* % 2048)))
      (mod mod-val)))

(defn part [lines]
  (let [secrets (map #(Integer/parseInt %) lines)]
    (reduce +
            (for [secret secrets]
              (loop [s secret
                     i 0]
                (if (= i 2000)
                  s
                  (recur (next-secret s) (inc i))))))))

(defn -main [& args]
  (let [filename (first args)
        lines (->> (slurp filename)
                   str/split-lines
                   (filter seq))]
    (println (part lines))))
