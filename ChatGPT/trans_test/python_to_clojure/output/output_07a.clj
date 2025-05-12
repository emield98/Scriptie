(ns output_07a
  (:require [clojure.string :as str]))

(defn eval-expr [nums ops]
  (reduce (fn [acc [op n]]
            (case op
              "+" (+ acc n)
              "*" (* acc n)))
          (first nums)
          (map vector ops (rest nums))))

(defn all-op-combinations [n]
  (if (zero? n)
    [[]]
    (for [op (list "+" "*")
          rest (all-op-combinations (dec n))]
      (cons op rest))))

(defn part [lines]
  (reduce
   (fn [total line]
     (if (str/blank? line)
       total
       (let [[lhs rhs] (str/split line #":")
             target (parse-long (str/trim lhs))
             nums (mapv parse-long (str/split (str/trim rhs) #"\s+"))
             ops-list (all-op-combinations (dec (count nums)))]
         (if (some #(= (eval-expr nums %) target) ops-list)
           (+ total target)
           total))))
   0
   lines))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
