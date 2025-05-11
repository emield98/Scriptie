;; ChatGPT-generated solution will go here.
(ns output_09b
  (:require [clojure.string :as str]))

(defn parse-disk [s]
  (loop [chs (map #(Character/digit % 10) s)
         file-id 0
         acc []]
    (if (empty? chs)
      acc
      (let [[fsize & chs1] chs
            [ssize & chs2] chs1
            file (repeat fsize file-id)
            space (repeat ssize nil)]
        (recur chs2 (inc file-id) (into acc (concat file space)))))))

(defn find-free-spans [blocks]
  (loop [i 0, spans [], start nil]
    (if (= i (count blocks))
      (if start (conj spans [start i]) spans)
      (let [b (blocks i)]
        (cond
          (and (nil? b) (nil? start)) (recur (inc i) spans i)
          (and (nil? b) (some? start)) (recur (inc i) spans start)
          (and (some? start)) (recur (inc i) (conj spans [start i]) nil)
          :else (recur (inc i) spans nil))))))

(defn move-file [blocks id]
  (let [positions (keep-indexed #(when (= %2 id) %1) blocks)
        size (count positions)
        leftmost (first positions)
        free-spans (filter #(<= size (- (second %) (first %))) (find-free-spans blocks))
        target (first (filter #(<= (first %) leftmost) free-spans))]
    (if (nil? target)
      blocks
      (let [[start end] target
            blocks' (reduce #(assoc %1 %2 nil) blocks positions)
            new-blocks (reduce #(assoc %1 (+ start %2) id) blocks' (range size))]
        new-blocks))))

(defn compact-files [blocks]
  (let [max-id (apply max (filter some? (set blocks)))]
    (reduce (fn [b id] (move-file b id)) blocks (reverse (range (inc max-id))))))

(defn checksum [blocks]
  (->> (map-indexed (fn [i b] (if (some? b) (* i b) 0)) blocks)
       (reduce +)))

(defn part [lines]
  (let [disk (parse-disk (first lines))
        compacted (compact-files (vec disk))]
    (checksum compacted)))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
