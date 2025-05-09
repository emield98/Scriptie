;; src/aoc2024/day15.clj
(ns aoc2024.day15
  {:clj-kondo/config '{:linters {:unresolved-symbol {:level :off}}}}
  (:require [clojure.string :as str])
  (:gen-class))

;; Direction vectors
(def dirmap
  {\> [0 1]
   \< [0 -1]
   \^ [-1 0]
   \v [1 0]})

;; Parse input into walls, boxes, robot, and commands
(defn parse-input [s]
  (let [[grid-str cmds-str] (str/split s #"\n\n")
        lines  (str/split-lines grid-str)
        coords (for [r (range (count lines))
                     :let [row (nth lines r)]
                     c (range (count row))]
                [[r c] (nth row c)])
        walls  (set (keep (fn [[coord ch]] (when (= ch \#) coord)) coords))
        boxes  (set (keep (fn [[coord ch]] (when (= ch \O) coord)) coords))
        robot  (first  (keep (fn [[coord ch]] (when (= ch \@) coord)) coords))
        cmds   (apply str (str/split-lines cmds-str))]
    {:walls    walls
     :boxes    boxes
     :robot    robot
     :commands cmds}))

;; Determine contiguous boxes in direction dr,dc from pos
(defn pushable-blocks [boxes walls pos [dr dc]]
  (let [start   [(+ (first pos) dr) (+ (second pos) dc)]
        line    (->> (iterate (fn [[r c]] [(+ r dr) (+ c dc)]) start)
                     (take-while boxes)
                     vec)]
    (cond
      (and (empty? line) (walls start)) nil
      (empty? line) []
      :else
      (let [last-box  (peek line)
            beyond    [(+ (first last-box) dr) (+ (second last-box) dc)]]
        (when-not (walls beyond) line)))))

;; Simulate robot and boxes for all commands
(defn simulate [state]
  (let [{:keys [walls boxes robot commands]} state]
    (loop [pos  robot
           bs   boxes
           cmds (seq commands)]
      (if-let [c (first cmds)]
        (let [[dr dc]  (dirmap c)
              next-pos [(+ (first pos) dr) (+ (second pos) dc)]
              blocks   (pushable-blocks bs walls pos [dr dc])]
          (cond
            (nil? blocks)
            ;; blocked: skip movement
            (recur pos bs (next cmds))

            (empty? blocks)
            ;; move into empty space
            (recur next-pos bs (next cmds))

            :else
            ;; push boxes
            (let [moved   (mapv (fn [[r c]] [(+ r dr) (+ c dc)]) blocks)
                  removed (apply disj bs blocks)
                  new-bs  (into removed moved)]
              (recur next-pos new-bs (next cmds)))))
        ;; no commands left: return final boxes
        {:boxes bs}))))

;; GPS coordinate of box at [r c] of box at [r c]
(defn gps-coordinate [[r c]]
  (+ (* r 100) c))

(defn part-1 [input-str]
  (->> input-str
       parse-input
       simulate
       :boxes
       (map gps-coordinate)
       (reduce +)))

(defn -main [& args]
  (if-let [f (first args)]
    (println (part-1 (slurp f)))))
