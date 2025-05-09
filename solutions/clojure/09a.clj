(ns aoc2024.day09
  (:require [clojure.string :as s]))

;; Parser
(defn parse-input [s]
  (let [xs (->> (str s \0) (map (comp parse-long str)))]
    (->> xs
         (map #(hash-map :id (/ %1 2) :pos %2 :len %3)
              (range) (reductions + 0 xs))
         (partition 2)
         (apply map vector)
         (zipmap [:files :spaces]))))

;; Helpers
(defn move-blocks-to-start [{:keys [files spaces]}]
  (loop [files files
         spaces spaces
         res []]
    (cond
      (empty? files) (assert false "there should always be a starting file remaining")
      (or (empty? spaces)
          (> (:pos (first spaces)) (:pos (peek files)))) (concat res files)
      (zero? (:len (first spaces))) (recur files (rest spaces) res)
      (zero? (:len (peek files))) (recur (pop files) spaces res)
      :else
      (let [{s-id :id s-len :len s-pos :pos} (first spaces)
            {f-id :id f-len :len f-pos :pos} (peek files)
            to-consume (min s-len f-len)]
        (recur (conj (pop files) {:id f-id :len (- f-len to-consume) :pos f-pos})
               (cons {:id s-id :len (- s-len to-consume) :pos (+ s-pos to-consume)} (rest spaces))
               (conj res {:id f-id :len to-consume :pos s-pos}))))))

(defn triangle-sum [first last]
  (/ (* (inc (- last first)) (+ first last)) 2))

(defn checksum [blocks]
  (->> blocks
       (map #(* (:id %) (triangle-sum (:pos %) (+ (:pos %) (:len %) -1))))
       (reduce +)))

;; Part 1
(defn part-1 [s]
  (->> s
       s/trim
       parse-input
       move-blocks-to-start
       checksum))

(defn -main [& args]
  (let [filename (first args)
        content (slurp filename)]
    (println (part-1 content))))
