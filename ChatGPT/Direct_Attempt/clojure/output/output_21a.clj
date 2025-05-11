;; ChatGPT-generated solution will go here.
(ns output_21a
  (:require [clojure.string :as str]
            [clojure.data.priority-map :refer [priority-map]]))

(def keypad
  {;; final numeric keypad (digits and A)
   :numeric {\1 [0 2] \2 [0 3] \3 [0 4]
             \4 [1 2] \5 [1 3] \6 [1 4]
             \7 [2 2] \8 [2 3] \9 [2 4]
             \0 [3 3] \A [3 4]}
   ;; directional keypad used to control robot before typing numeric code
   :dir1 {[0 1] :^ [0 2] :A
          [1 0] :< [1 1] :v [1 2] :>}
   ;; outermost keypad used by the player
   :dir2 {[0 1] :^ [0 2] :A
          [1 0] :< [1 1] :v [1 2] :>}})

(def delta
  {:^ [-1 0]
   :v [1 0]
   :< [0 -1]
   :> [0 1]})

(defn invert-map [m]
  (into {} (map (fn [[k v]] [v k]) m)))

(defn bfs [grid start target-set]
  (let [q (atom (priority-map [start []] 0))
        visited (atom #{})]
    (loop []
      (if-let [[[pos path] _] (peek @q)]
        (do
          (swap! q pop)
