;; ChatGPT-generated solution will go here.
(ns output_17a
  (:require [clojure.string :as str]))

(defn parse-input [lines]
  (let [[a b c] (map #(Long/parseLong (second (str/split % #": ")))
                     (take 3 lines))
        prog (->> (drop 4 lines)
                  (mapcat #(str/split % #","))
                  (map #(Long/parseLong (str/trim %))))]
    {:A a :B b :C c :program prog}))

(defn combo-value [operand {:keys [A B C]}]
  (case operand
    4 A
    5 B
    6 C
    7 (throw (ex-info "Reserved operand" {:op operand}))
    operand))

(defn run-program [{:keys [A B C program]}]
  (loop [ip 0, reg {:A A :B B :C C}, out []]
    (if (>= ip (count program))
      (str/join "," out)
      (let [op (nth program ip)
            arg (nth program (inc ip))
            next-ip (+ ip 2)]
        (case op
          0 (recur next-ip
                   (assoc reg :A (quot (:A reg) (Math/pow 2 (combo-value arg reg))))
                   out)
          1 (recur next-ip
                   (assoc reg :B (bit-xor (:B reg) arg))
                   out)
          2 (recur next-ip
                   (assoc reg :B (mod (combo-value arg reg) 8))
                   out)
          3 (if (zero? (:A reg))
              (recur next-ip reg out)
              (recur arg reg out))
          4 (recur next-ip
                   (assoc reg :B (bit-xor (:B reg) (:C reg)))
                   out)
          5 (recur next-ip
                   reg
                   (conj out (mod (combo-value arg reg) 8)))
          6 (recur next-ip
                   (assoc reg :B (quot (:A reg) (Math/pow 2 (combo-value arg reg))))
                   out)
          7 (recur next-ip
                   (assoc reg :C (quot (:A reg) (Math/pow 2 (combo-value arg reg))))
                   out)
          (throw (ex-info "Unknown opcode" {:opcode op})))))))

(defn part [lines]
  (run-program (parse-input lines)))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
