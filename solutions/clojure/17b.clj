(ns aoc2024.day17b
  (:require [clojure.string :as str]))

;; --- Inlined utils ---
(defn to-blocks [input]
  (str/split input #"\n\n"))

(defn parse-out-longs [line]
  (map #(Long/parseLong %)
       (re-seq #"[-+]?\d+" line)))

;; --- Instruction logic ---
(defn- combo [[ra rb rc] operand]
  (case operand 0 operand 1 operand 2 operand 3 operand 4 ra 5 rb 6 rc))

(defn- adv [[ra rb rc] operand]
  [(bit-shift-right ra (combo [ra rb rc] operand)) rb rc])

(defn- bxl [[ra rb rc] operand]
  [ra (bit-xor rb operand) rc])

(defn- bst [[ra rb rc] operand]
  [ra (bit-and (combo [ra rb rc] operand) 2r111) rc])

(defn- jnz [[ra] idx operand]
  (if (zero? ra) (inc idx) operand))

(defn- bxc [[ra rb rc] _]
  [ra (bit-xor rb rc) rc])

(defn- out [regs operand]
  (bit-and (combo regs operand) 2r111))

(defn- bdv [[ra rb rc] operand]
  [ra (bit-shift-right ra (combo [ra rb rc] operand)) rc])

(defn- cdv [[ra rb rc] operand]
  [ra rb (bit-shift-right ra (combo [ra rb rc] operand))])

(def instructions-map
  {0 adv 1 bxl 2 bst 4 bxc 6 bdv 7 cdv})

(defn- execute [regs instructions]
  (let [instructions (partition 2 (map int instructions))]
    (loop [regs regs, ins-ind 0, [[opcode operand] & rem] instructions, res []]
      (cond
        (nil? opcode) res
        (= opcode 3) (let [new-ind (jnz regs ins-ind operand)]
                      (recur regs new-ind (drop new-ind instructions) res))
        (= opcode 5) (recur regs (inc ins-ind) rem (conj res (out regs operand)))
        :else (recur ((instructions-map opcode) regs operand)
                     (inc ins-ind) rem res)))))

(defn- find-identity [instructions]
  (loop [i 0]
    (let [output (execute [i 0 0] instructions)
          expected (drop (- (count instructions) (count output)) instructions)]
      (if (= output instructions)
        i
        (if (= output expected)
          (recur (bit-shift-left i 3))
          (recur (inc i)))))))

(defn part-2 [input]
  (->> input
       to-blocks
       (map parse-out-longs)
       last
       find-identity))

(defn -main [& [file]]
  (println (part-2 (slurp (or file "input")))))
