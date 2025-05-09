(ns aoc2024.day13
  (:require [clojure.string :as str]
            [clojure.set :as set])
  (:gen-class))

;; === vec2 and grid utilities ===
(defrecord vec2 [^long row ^long col]
  clojure.lang.Indexed
  (nth [_ i] (case i 0 row 1 col))
  (nth [_ i default] (case i 0 row 1 col default))
  
  Comparable
  (compareTo [_ other]
    (let [c (compare row (.row ^vec2 other))]
      (case c
        0 (compare col (.col ^vec2 other))
        c))))

;; Constructor function for vec2
(defn create-vec2 [row col]
  (->vec2 row col))

(defn vec2+ [a b]
  (->vec2 (+ (.row ^vec2 a) (.row ^vec2 b))
          (+ (.col ^vec2 a) (.col ^vec2 b))))

;; === parsecomb utilities ===
(defn error? [result] (contains? result :error))
(defn success? [result] (contains? result :rest))

(defn return-result [x]
  (fn [inp] {:result x :rest inp}))

(defn string->stringbuf [s] {:str s :pos 0})

(defn adv-buf [n buf] (update buf :pos (partial + n)))
(defn inc-buf [buf] (update buf :pos inc))

(defn buf-peek [{:keys [str pos]}] (get str pos))
(defn buf-take [n {:keys [str pos]}] (subs str pos (+ n pos)))

(defn buf-eof? [{:keys [str pos]}] (>= pos (count str)))
(defn buf-remaining [{:keys [str pos]}] (- (count str) pos))

(defn chr [c]
  (fn [{:keys [str pos] :as buf}]
    (cond
      (buf-eof? buf) {:error :eof :input buf}
      (= (get str pos) c) {:result c :rest (inc-buf buf)}
      :else {:error :wrong-char :expected c :input buf})))

(defn string [s]
  (fn [buf]
    (cond
      (< (buf-remaining buf) (count s)) {:error :eof :input buf}
      (= (buf-take (count s) buf) s) {:result s :rest (adv-buf (count s) buf)}
      :else {:error :wrong-string :expected s :input buf})))

(defn skip [p]
  (fn [inp]
    (let [res (p inp)]
      (if (error? res)
        res
        (dissoc res :result)))))

(defn skip-string [s] (skip (string s)))

(defn maybe
  "Does not contain a :result if the parser fails"
  [p]
  (fn [inp]
    (let [res (p inp)]
      (if (error? res)
        {:rest inp}
        res))))

(defn charset [st]
  (fn [buf]
    (cond
      (buf-eof? buf) {:error :eof :input buf}
      (st (buf-peek buf)) {:result (buf-peek buf) :rest (inc-buf buf)}
      :else {:error :wrong-char :expected st :input buf})))

(def DIGITS #{\0 \1 \2 \3 \4 \5 \6 \7 \8 \9})

(defn charset+ [st]
  (fn [{:keys [str pos] :as buf}]
    (cond
      (buf-eof? buf) {:error :eof :input buf}
      (not (st (buf-peek buf))) {:error :wrong-char :expected st :input buf}
      :else
      (loop [idx (inc pos)]
        (if (or (>= idx (count str)) (not (st (get str idx))))
          {:result (buf-take (- idx pos) buf) :rest (adv-buf (- idx pos) buf)}
          (recur (inc idx)))))))

(defn p-seq [& ps]
  (fn [inp]
    (loop [ps ps
           result []
           inp inp]
      (if (empty? ps)
        (condp = (count result)
          0 {:rest inp}
          1 {:result (first result) :rest inp} ; flatten result if it only has one element
          {:result result :rest inp})
        (let [res ((first ps) inp)]
          (if (error? res)
            res
            (recur (rest ps)
                   (if (:result res) (conj result (:result res)) result)
                   (:rest res))))))))

(defn p-map [f p]
  (fn [inp]
    (let [res (p inp)]
      (if (error? res)
        res
        (update res :result f)))))

(defn p-repeat [n p]
  (fn [inp]
    (loop [inp inp
           n n
           res []]
      (if (zero? n)
        {:result res :rest inp}
        (let [r (p inp)]
          (if (error? r)
            r
            (recur (:rest r) (dec n) (conj res (:result r)))))))))

(defn some* [p]
  (fn [inp]
    (loop [inp inp
           res []]
      (let [r (p inp)]
        (if (error? r)
          {:result res :rest inp}
          (recur (:rest r) (if (:result r) (conj res (:result r)) res)))))))

(defn p-or [& ps]
  (fn [inp]
    (reduce #(let [res (%2 inp)]
               (if (error? res)
                 %1
                 (reduced res)))
            {:error :no-successful-parsers :input inp}
            ps)))

(defn list-+ [elem sep]
  (->>
   (p-seq elem
          (some* (p-seq (skip sep)
                        elem)))
   (p-map #(apply cons %))))

(def p-int
  "parses a (possibly negative) integer"
  (->> (p-seq (maybe (chr \-))
              (charset+ DIGITS))
       (p-map #(if (= \- (first %))
                 (- (Long/parseUnsignedLong (apply str (second %))))
                 (Long/parseUnsignedLong (apply str %))))))

(defn maybe
  "Does not contain a :result if the parser fails"
  [p]
  (fn [inp]
    (let [res (p inp)]
      (if (error? res)
        {:rest inp}
        res))))

(def nl "parses a newline character" (chr \newline))
(def skip-nl "parser that skips a newline" (skip nl))

(defn label [ks p]
  (p-map #(zipmap ks %) p))

;; === Math utilities ===
(defn gcd [a b]
  (if (zero? b)
    a
    (recur b (mod a b))))

;; === Main solution ===
(defn button-parser [id]
  (->> (p-seq (skip-string "Button ") (skip-string id) (skip-string ": X+")
              p-int (skip-string ", Y+") p-int)
       (p-map (partial apply create-vec2))))

(def claw-machine-parser (->> (p-seq (button-parser "A") skip-nl (button-parser "B") skip-nl
                                    (->> (p-seq (skip-string "Prize: X=") p-int
                                                (skip-string ", Y=") p-int)
                                         (p-map (partial apply create-vec2))))
                           (label [:a :b :prize])))

(def parse-input (list-+ claw-machine-parser (p-repeat 2 nl)))

(def A-COST 3)
(def B-COST 1)

(defn neighbors [^vec2 a ^vec2 b ^vec2 prize ^vec2 coord]
  (->> [[(vec2+ a coord) A-COST] [(vec2+ b coord) B-COST]]
       (filter (fn [[^vec2 p _]]
                 (and (<= (.row p) (.row prize))
                      (<= (.col p) (.col prize)))))))

(defn make-inverse-mat [^vec2 a ^vec2 b]
  (let [ax (.row a)
        ay (.col a)
        bx (.row b)
        by (.col b)
        det (- (* ax by) (* bx ay))]
    (if (zero? det)
      nil
      ; row-major
      [[(/ by det) (/ (- bx) det)]
       [(/ (- ay) det) (/ ax det)]])))

(defn calc-na-nb [[[a b] [c d]] [^long prize-x ^long prize-y]]
  [(+ (* a prize-x) (* b prize-y)) (+ (* c prize-x) (* d prize-y))])

(defn solve-colinear [^long p ^long a ^long b]
  (if (not (zero? (rem p (gcd a b))))
    nil
    (loop [ma (- p (mod p a))
           min-cost Long/MAX_VALUE]
      (if (<= ma 0)
        min-cost
        (let [remaining (- p ma)]
          (if (zero? (mod remaining b))
            (recur (- ma a) (min min-cost (+ (* A-COST (/ ma a)) (* B-COST (/ remaining b)))))
            (recur (- ma a) min-cost)))))))

(defn num-coins-needed [[^long ax ^long ay :as a] [^long bx _by :as b] [^long px ^long py :as prize]]
  (let [mat (make-inverse-mat a b)]
    (cond
      (some? mat)
      (let [[n-a n-b] (calc-na-nb mat prize)]
        (or (and (integer? n-a) (integer? n-b) (+ (* n-a A-COST) (* n-b B-COST)))
            nil))
      (= (/ px ax) (/ py ay)) (solve-colinear px ax bx)
      :else nil)))

(defn reposition-prize [^long n ^vec2 prize]
  (vec2+ prize (create-vec2 n n)))

(defn part [shift s]
  (->> s
       string->stringbuf
       parse-input
       :result
       (keep (fn [{:keys [a b prize]}] (num-coins-needed a b (reposition-prize shift prize))))
       (reduce +)))

(defn part-1 [lines]
  (str (part 0 lines))) ; Convert the result to a string to avoid trailing `N`

(defn -main [& args]
  (if-let [filename (first args)]
    (let [lines (str/join "\n" (str/split-lines (slurp filename)))] ; Join lines into a single string
      (try
        (print (part-1 lines)) ; Use `print` to avoid adding a newline
        (catch Exception e
          (println "Error:" (.getMessage e)))))))