;; ChatGPT-generated solution will go here.
(ns output_14b
  (:require [clojure.string :as str]))

(defn parse-line [line]
  (let [[_ px py vx vy]
        (re-matches #"p=(-?\d+),(-?\d+)\s+v=(-?\d+),(-?\d+)" line)]
    [(Long/parseLong px)
     (Long/parseLong py)
     (Long/parseLong vx)
     (Long/parseLong vy)]))

(defn advance [robots t]
  (map (fn [[px py vx vy]]
         [(+ px (* t vx))
          (+ py (* t vy))])
       robots))

(defn bounding-box [positions]
  (let [xs (map first positions)
        ys (map second positions)]
    [[(apply min xs) (apply min ys)]
     [(apply max xs) (apply max ys)]]))

(defn area [[ [min-x min-y] [max-x max-y] ]]
  (* (- max-x min-x) (- max-y min-y)))

(defn part [lines]
  (let [robots (map parse-line lines)]
    (loop [t 0
           last-area Long/MAX_VALUE
           last-t nil]
      (let [positions (advance robots t)
            box (bounding-box positions)
            a (area box)]
        (if (> a last-area)
          last-t
          (recur (inc t) a t))))))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
