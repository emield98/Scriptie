;; ChatGPT-generated solution will go here.
(ns output_09a
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

(defn compact [blocks]
  (loop [blk (vec blocks)
         idx (dec (count blocks))]
    (if (neg? idx)
      blk
      (let [id (get blk idx)]
        (if (nil? id)
          (recur blk (dec idx))
          (let [free-left (first (keep-indexed #(when (nil? %2) %1) blk))
                updated (if (and free-left (< free-left idx))
                          (let [pos (loop [p idx]
                                      (if (and (<= free-left p) (= (get blk p) id))
                                        (recur (dec p))
                                        (inc p)))
                                count (count (filter #(= id %) blk))
                                new-blk (vec (concat
                                               (repeat count id)
                                               (repeat (- (count blk) count) nil)))]
                            new-blk)
                          blk)]
            (recur updated (dec idx)))))))

(defn checksum [blocks]
  (->> (map-indexed (fn [i b] (if (some? b) (* i b) 0)) blocks)
       (reduce +)))

(defn part [lines]
  (let [disk (parse-disk (first lines))
        compacted (compact disk)]
    (checksum compacted)))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
