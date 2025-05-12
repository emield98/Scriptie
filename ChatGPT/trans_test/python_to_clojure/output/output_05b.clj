(ns output_05b
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [clojure.data.priority-map :refer [priority-map]]))

(defn parse-lines [lines]
  (let [nonblank (vec (filter (complement str/blank?) lines))
        [rule-lines update-lines] (split-with #(str/includes? % "|") nonblank)
        rules (mapv (fn [line]
                      (let [[a b] (str/split line #"\|")]
                        [(parse-long a) (parse-long b)]))
                    rule-lines)
        updates (mapv #(vec (map parse-long (str/split % #","))) update-lines)]
    [rules updates]))

(defn is-valid? [update rule-set]
  (let [pos (into {} (map-indexed (fn [i v] [v i]) update))]
    (every? (fn [[x y]] (< (pos x) (pos y))) rule-set)))

(defn topo-sort [nodes rule-set]
  (let [graph (reduce (fn [g [x y]] (update g x (fnil conj []) y)) {} rule-set)
        indegree (reduce (fn [m node] (assoc m node 0)) {} nodes)
        indegree (reduce (fn [m [_ y]] (update m y (fnil inc 0))) indegree rule-set)
        q (into (clojure.lang.PersistentQueue/EMPTY)
                (sort (filter #(zero? (get indegree %)) nodes)))]
    (loop [queue q
           result []
           in-degree indegree]
      (if (empty? queue)
        result
        (let [u (peek queue)
              queue (pop queue)
              result (conj result u)
              neighbors (sort (get graph u []))
              [queue in-degree] (reduce
                                 (fn [[q id] v]
                                   (let [new-id (update id v dec)]
                                     (if (zero? (new-id v))
                                       [(conj q v) new-id]
                                       [q new-id])))
                                 [queue in-degree]
                                 neighbors)]
          (recur queue result in-degree))))))

(defn part [lines]
  (let [[rules updates] (parse-lines lines)]
    (reduce
     (fn [total update]
       (let [subset-rules (filter (fn [[x y]] (and (some #{x} update) (some #{y} update))) rules)]
         (if (is-valid? update subset-rules)
           total
           (let [sorted-update (topo-sort update subset-rules)]
             (+ total (nth sorted-update (quot (count sorted-update) 2)))))))
     0
     updates)))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
