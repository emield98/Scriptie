(ns output_19a
  (:require [clojure.string :as str]
            [clojure.core.memoize :as memo])
  (:gen-class))

(defn part [lines]
  (let [[patterns-section designs-section] (str/split (str/join "\n" lines) #"\n\n")
        towel-set (set (str/split patterns-section #", "))
        designs (str/split-lines designs-section)
        can-form (memo/memo
                   (fn [design]
                     (cond
                       (empty? design) true
                       :else (some #(and (str/starts-with? design %)
                                         (can-form (subs design (count %))))
                                   towel-set))))]
    (count (filter can-form designs))))

(defn -main [& args]
  (let [filename (first args)
        lines (->> (slurp filename)
                   str/split-lines)]
    (println (part lines))))
