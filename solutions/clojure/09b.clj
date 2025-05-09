(ns aoc2024.day09b
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
(defn move-files-to-start-faster [{:keys [files spaces]}]
  (loop [files files
         spaces (->> spaces
                     (group-by :len)
                     (map (fn [[len spaces]]
                            [len (into (sorted-set-by #(compare (:pos %1) (:pos %2))) spaces)]))
                     (into {}))
         res []]
    (if-let [file (peek files)]
      (if-let [possible-lens (seq (filter #(and (seq (spaces %))
                                                (< (:pos (first (spaces %))) (:pos file)))
                                          (range (:len file) 10)))]

        (let [earliest-len (apply min-key (comp :pos first spaces) possible-lens)
              {s-pos :pos s-len :len :as space} (first (spaces earliest-len))]

          (recur (pop files)
                 (-> spaces
                     (update s-len #(disj % space))
                     (update (- s-len (:len file))
                             #(conj % {:pos (+ s-pos (:len file)) :len (- s-len (:len file))})))
                 (conj res (assoc file :pos s-pos))))

        (recur (pop files) spaces (conj res file)))
      res)))

(defn triangle-sum [first last]
  (/ (* (inc (- last first)) (+ first last)) 2))

(defn checksum [blocks]
  (->> blocks
       (map #(* (:id %) (triangle-sum (:pos %) (+ (:pos %) (:len %) -1))))
       (reduce +)))

;; Part 2
(defn part-2 [s]
  (->> s
       s/trim
       parse-input
       move-files-to-start-faster
       checksum))

(defn -main [& args]
  (let [filename (first args)
        content (slurp filename)]
    (println (part-2 content))))
