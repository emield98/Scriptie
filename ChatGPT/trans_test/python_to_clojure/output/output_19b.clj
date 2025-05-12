(ns output_19b
  (:require [clojure.string :as str]
            [clojure.core.memoize :as memo])
  (:gen-class))

(defn part [lines]
  (let [[patterns-section designs-section] (str/split (str/join "\n" lines) #"\n\n")
        towel-set (set (str/split patterns-section #", "))
        designs (str/split-lines designs-section)
        count-ways (memo/memo
                     (fn [design]
                       (if (empty? design)
                         1
                         (reduce +
                                 (for [pattern towel-set
                                       :when (str/starts-with? design pattern)]
                                   (count-ways (subs design (count pattern))))))))]
    (reduce + (map count-ways designs))))

(defn -main [& args]
  (let [filename (first args)
        lines (->> (slurp filename)
                   str/split-lines)]
    (println (part lines))))
