;; ChatGPT-generated solution will go here.
(ns output_22a
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

(defn nth-secret [start n]
  (nth (iterate next-secret start) n))

(defn part [lines]
  (->> lines
       (map #(Long/parseLong %))
       (map #(nth-secret % 2000))
       (reduce +)))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
