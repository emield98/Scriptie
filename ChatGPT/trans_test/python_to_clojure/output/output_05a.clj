(ns output_05a
  (:require [clojure.string :as str]))

(defn part [lines]
  (let [nonblank (filter (complement str/blank?) lines)
        [rule-lines update-lines] (split-with #(str/includes? % "|") nonblank)
        rules (set (map (fn [line]
                          (let [[a b] (str/split line #"\|")]
                            [(parse-long a) (parse-long b)]))
                        rule-lines))
        updates (map #(vec (map parse-long (str/split % #","))) update-lines)]
    (reduce
     (fn [total update]
       (let [index-map (into {} (map-indexed (fn [i v] [v i]) update))
             valid? (every? (fn [[a b]]
                              (or (not (and (contains? index-map a) (contains? index-map b)))
                                  (< (index-map a) (index-map b))))
                            rules)]
         (if valid?
           (+ total (nth update (quot (count update) 2)))
           total)))
     0
     updates)))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
