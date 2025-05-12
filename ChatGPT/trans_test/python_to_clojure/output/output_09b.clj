(ns output_09b
  (:require [clojure.string :as str]))

(defn parse-input [line]
  (loop [i 0
         pos 0
         fid 0
         blocks []
         file-ranges {}]
    (if (>= i (count line))
      [blocks file-ranges]
      (let [length (parse-long (str (nth line i)))
            is-file (even? i)
            new-blocks (if is-file
                         (concat blocks (repeat length (str fid)))
                         (concat blocks (repeat length ".")))
            new-ranges (if is-file
                         (assoc file-ranges fid [pos length])
                         file-ranges)]
        (recur (inc i)
               (+ pos length)
               (if is-file (inc fid) fid)
               new-blocks
               new-ranges)))))

(defn get-free-spans [blocks]
  (loop [i 0
         spans []
         total (count blocks)]
    (if (>= i total)
      spans
      (if (= (blocks i) ".")
        (let [start i
              end (loop [j i]
                    (if (and (< j total) (= (blocks j) "."))
                      (recur (inc j))
                      j))]
          (recur end (conj spans [start (- end start)]) total))
        (recur (inc i) spans total)))))

(defn move-files-left [blocks file-ranges]
  (let [blk (transient (vec blocks))]
    (reduce
     (fn [fr fid]
       (let [[start len] (fr fid)
             free-spans (get-free-spans blk)
             target (some (fn [[fs fl]]
                            (when (and (>= fl len) (< fs start)) fs))
                          free-spans)]
         (if target
           (do
             (dotimes [i len]
               (assoc! blk (+ target i) (str fid))
               (assoc! blk (+ start i) "."))
             (assoc fr fid [target len]))
           fr)))
     file-ranges
     (reverse (sort (keys file-ranges)))
     (persistent! blk))))

(defn part [lines]
  (let [line (first (filter (complement str/blank?) lines))
        [blocks file-ranges] (parse-input line)
        moved-ranges (move-files-left blocks file-ranges)
        final-blocks (reduce
                      (fn [blk [fid [start len]]]
                        (reduce
                         (fn [b i] (assoc b (+ start i) (str fid)))
                         blk
                         (range len)))
                      (vec (repeat (count blocks) "."))
                      moved-ranges)]
    (reduce
     (fn [sum [idx val]]
       (if (= val ".")
         sum
         (+ sum (* idx (parse-long val)))))
     0
     (map-indexed vector final-blocks))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
