(ns output_23a
  (:require [clojure.string :as str]
            [clojure.set :as set])
  (:gen-class))

(defn parse-edges [lines]
  (map #(str/split % #"-") lines))

(defn build-graph [edges]
  (reduce (fn [g [a b]]
            (-> g
                (update a (fnil conj #{}) b)
                (update b (fnil conj #{}) a)))
          {}
          edges))

(defn fully-connected? [g a b c]
  (and (contains? (get g a) b)
       (contains? (get g a) c)
       (contains? (get g b) a)
       (contains? (get g b) c)
       (contains? (get g c) a)
       (contains? (get g c) b)))

(defn part [lines]
  (let [edges (parse-edges lines)
        graph (build-graph edges)
        nodes (keys graph)]
    (->> (for [a nodes
               b nodes
               c nodes
               :when (and (< (compare a b) 0)
                          (< (compare b c) 0))]
           [a b c])
         (filter (fn [[a b c]]
                   (and (fully-connected? graph a b c)
                        (or (str/starts-with? a "t")
                            (str/starts-with? b "t")
                            (str/starts-with? c "t")))))
         count)))

(defn -main [& args]
  (let [filename (first args)
        lines (->> (slurp filename)
                   str/split-lines
                   (filter seq))]
    (println (part lines))))
