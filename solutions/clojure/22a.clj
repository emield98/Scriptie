(ns aoc2024.day22
  (:require [clojure.string :as str]))

;; --- Inlined utility function ---
(defn parse-out-longs [line]
  (map #(Long/parseLong %) (re-seq #"[-+]?\d+" line)))

;; --- Core logic for day22 ---
(defn- evolve-once [secret]
  (let [step-1 (mod (bit-xor (* secret 64) secret) 16777216)
        step-2 (mod (bit-xor (quot step-1 32) step-1) 16777216)]
    (mod (bit-xor (* step-2 2048) step-2) 16777216)))

(defn- evolve [times secret]
  (reduce (fn [s _] (evolve-once s)) secret (range times)))

(defn part-1 [input]
  (->> input
       parse-out-longs
       (map (partial evolve 2000))
       (apply +)))

(defn -main [& [file]]
  (println (part-1 (slurp (or file "input")))))
