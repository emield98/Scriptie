(ns output_17a
  (:require [clojure.string :as str]))

(defn parse-input [lines]
  (reduce
    (fn [[reg prog] line]
      (cond
        (str/starts-with? line "Register")
        (let [[_ name val] (str/split line #"\s+")]
          [(assoc reg (first name) (Long/parseLong val)) prog])

        (str/starts-with? line "Program:")
        [reg (->> (subs line (inc (.indexOf line ":")))
                  (str/trim)
                  (str/split #",")
                  (mapv #(Long/parseLong %)))]

        :else
        [reg prog]))
    [{'A 0 'B 0 'C 0} []]
    (filter (complement str/blank?) lines)))

(defn part [lines]
  (let [[init-reg program] (parse-input lines)
        n (count program)]
    (loop [ip 0, reg init-reg, out []]
      (if (>= ip n)
        out
        (let [opcode (program ip)
              operand (program (inc ip))
              combo-value (fn [op]
                            (cond
                              (<= 0 op 3) op
                              (= op 4) (reg 'A)
                              (= op 5) (reg 'B)
                              (= op 6) (reg 'C)
                              :else (throw (ex-info "Invalid combo operand" {:op op}))))
              a-val (reg 'A)]
          (case opcode
            0 ; adv
            (let [shift (bit-shift-left 1 (combo-value operand))]
              (recur (+ ip 2) (assoc reg 'A (quot a-val shift)) out))

            1 ; bxl
            (recur (+ ip 2) (update reg 'B bit-xor operand) out)

            2 ; bst
            (recur (+ ip 2) (assoc reg 'B (mod (combo-value operand) 8)) out)

            3 ; jnz
            (if (not= 0 a-val)
              (recur operand reg out)
              (recur (+ ip 2) reg out))

            4 ; bxc
            (recur (+ ip 2) (update reg 'B bit-xor (reg 'C)) out)

            5 ; out
            (recur (+ ip 2) reg (conj out (str (mod (combo-value operand) 8))))

            6 ; bdv
            (let [shift (bit-shift-left 1 (combo-value operand))]
              (recur (+ ip 2) (assoc reg 'B (quot a-val shift)) out))

            7 ; cdv
            (let [shift (bit-shift-left 1 (combo-value operand))]
              (recur (+ ip 2) (assoc reg 'C (quot a-val shift)) out))

            (throw (ex-info (str "Unknown opcode: " opcode) {}))))))))

(defn -main [& args]
  (let [filename (first args)
        lines   (str/split-lines (slurp filename))
        output  (part lines)]
    (println (str/join "," output))))
