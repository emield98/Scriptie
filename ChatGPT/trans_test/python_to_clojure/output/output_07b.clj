(ns output_07b
  (:require [clojure.string :as str]))

(defn concat-num [a b]
  (parse-long (str a b)))

(defn eval-expr [nums ops]
  (loop [result (first nums)
         nums (rest nums)
         ops ops]
    (if (empty? ops)
      result
      (let [op (first ops)
            n (first nums)
            new-result (case op
                         "+" (+ result n)
                         "*" (* result n)
                         "||" (concat-num result n))]
        (recur new-result (rest nums) (rest ops))))))

(defn all-op-combinations [n]
  (if (zero? n)
    [[]]
    (for [op ["+" "*" "||"]
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
