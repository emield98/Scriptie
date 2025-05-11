;; ChatGPT-generated solution will go here.
(ns output_05b
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(defn parse-input [lines]
  (let [[rule-lines update-lines] (split-with #(str/includes? % "|") lines)
        rules (->> rule-lines
                   (map #(str/split % #"\|"))
                   (map (fn [[a b]] [(parse-long a) (parse-long b)])))
        updates (map #(mapv parse-long (str/split % #",")) (remove str/blank? update-lines))]
    [rules updates]))

(defn valid-update? [rules update]
  (let [idx (zipmap update (range))]
    (every? (fn [[a b]]
              (if (and (idx a) (idx b))
                (< (idx a) (idx b))
                true))
            rules)))

(defn topological-sort [nodes edges]
  (let [adj (reduce (fn [m [a b]] (update m a (fnil conj #{}) b)) {} edges)
        incoming (reduce (fn [m [a b]] (update m b (fnil conj #{}) a)) {} edges)
        no-incoming (into (sorted-set) (filter #(not (contains? incoming %)) nodes))]
    (loop [result [] queue no-incoming in incoming]
      (if (empty? queue)
        (if (= (count result) (count nodes)) result nil)
        (let [n (first queue)
              queue-rest (disj queue n)
              children (get adj n #{})
              [in' queue'] (reduce (fn [[in' q] m]
                                     (let [preds (disj (get in' m #{}) n)]
                                       (if (empty? preds)
                                         [(dissoc in' m) (conj q m)]
                                         [(assoc in' m preds) q])))
                                   [in queue-rest]
                                   children)]
          (recur (conj result n) queue' in'))))))

(defn middle-page [update]
  (nth update (quot (count update) 2)))

(defn part [lines]
  (let [[rules updates] (parse-input lines)]
    (->> updates
         (remove #(valid-update? rules %))
         (map (fn [update]
                (let [relevant-rules (filter (fn [[a b]] (and (some #{a} update) (some #{b} update))) rules)
                      sorted (topological-sort (set update) relevant-rules)]
                  (middle-page sorted))))
         (reduce +))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
