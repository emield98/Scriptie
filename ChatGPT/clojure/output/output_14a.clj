;; ChatGPT-generated solution will go here.
(ns output_14a
  (:require [clojure.string :as str]))

(def width 101)
(def height 103)
(def time 100)

(defn parse-line [line]
  (let [[_ px py vx vy]
        (re-matches #"p=(-?\d+),(-?\d+)\s+v=(-?\d+),(-?\d+)" line)]
    [(Long/parseLong px)
     (Long/parseLong py)
     (Long/parseLong vx)
     (Long/parseLong vy)]))

(defn move-robot [[px py vx vy]]
  (let [x (mod (+ px (* time vx)) width)
        y (mod (+ py (* time vy)) height)]
    [x y]))

(defn part [lines]
  (let [robots (map parse-line lines)
        positions (map move-robot robots)
        mx (quot width 2)
        my (quot height 2)
        quadrants (frequencies
                    (for [[x y] positions
                          :when (not (or (= x mx) (= y my)))]
                      (cond
                        (and (< x mx) (< y my)) :tl
                        (and (> x mx) (< y my)) :tr
                        (and (< x mx) (> y my)) :bl
                        (and (> x mx) (> y my)) :br)))
        counts (map #(get quadrants % 0) [:tl :tr :bl :br])]
    (reduce * counts)))

(defn -main [& args]
  (let [filename (first args)
        lines (str/split-lines (slurp filename))]
    (println (part lines))))
