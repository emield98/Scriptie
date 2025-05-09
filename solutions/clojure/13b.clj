(ns aoc2024.day13b
  (:require [clojure.string :as str])
  (:gen-class))

;; ===== Utility Functions =====
(defn gcd [a b]
  (if (zero? b)
    (Math/abs ^long a)
    (recur b (mod a b))))

;; ===== vec2 Implementation =====
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

(defn ->vec2 [row col]
  (vec2. row col))

(defn vec2+ [a b] ^vec2
  (->vec2 (+ (.row ^vec2 a) (.row ^vec2 b))
          (+ (.col ^vec2 a) (.col ^vec2 b))))

;; ===== Parser Implementation =====
(defn error? [result] (contains? result :error))
(defn success? [result] (contains? result :rest))

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

(defn skip [p]
  (fn [inp]
    (let [res (p inp)]
      (if (error? res)
        res
        (dissoc res :result)))))

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

(defn skip-string [s] (skip (string s)))
(def nl (chr \newline))
(def skip-nl (skip nl))

(defn digit [buf]
  (cond
    (buf-eof? buf) {:error :eof :input buf}
    (Character/isDigit (buf-peek buf)) {:result (buf-peek buf) :rest (inc-buf buf)}
    :else {:error :wrong-char :expected "digit" :input buf}))

(def p-int
  (fn [buf]
    (let [sign-buf (if (= (buf-peek buf) \-)
                     (inc-buf buf)
                     buf)
          negative? (not= sign-buf buf)]
      (loop [digits []
             cur-buf sign-buf]
        (let [d (digit cur-buf)]
          (if (error? d)
            (if (empty? digits)
              {:error :not-a-number :input buf}
              {:result (if negative?
                         (- (Long/parseLong (apply str digits)))
                         (Long/parseLong (apply str digits)))
               :rest cur-buf})
            (recur (conj digits (:result d)) (:rest d))))))))

(defn label [ks p]
  (p-map #(zipmap ks %) p))

(defn list-+ [elem sep]
  (fn [inp]
    (let [first-res (elem inp)]
      (if (error? first-res)
        first-res
        (loop [result [(:result first-res)]
               cur-inp (:rest first-res)]
          (let [sep-res (sep cur-inp)]
            (if (error? sep-res)
              {:result result :rest cur-inp}
              (let [elem-res (elem (:rest sep-res))]
                (if (error? elem-res)
                  {:error :expected-element-after-separator :input (:rest sep-res)}
                  (recur (conj result (:result elem-res)) (:rest elem-res)))))))))))

;; ===== Challenge Implementation =====
(defn button-parser [id]
  (fn [inp]
    (let [prefix-res ((p-seq (string "Button ") (string id) (string ": X+")) inp)]
      (if (error? prefix-res)
        prefix-res
        (let [x-res (p-int (:rest prefix-res))]
          (if (error? x-res)
            x-res
            (let [comma-res ((string ", Y+") (:rest x-res))]
              (if (error? comma-res)
                comma-res
                (let [y-res (p-int (:rest comma-res))]
                  (if (error? y-res)
                    y-res
                    {:result (->vec2 (:result x-res) (:result y-res))
                     :rest (:rest y-res)}))))))))))

(defn prize-parser [inp]
  (let [prefix-res ((string "Prize: X=") inp)]
    (if (error? prefix-res)
      prefix-res
      (let [x-res (p-int (:rest prefix-res))]
        (if (error? x-res)
          x-res
          (let [comma-res ((string ", Y=") (:rest x-res))]
            (if (error? comma-res)
              comma-res
              (let [y-res (p-int (:rest comma-res))]
                (if (error? y-res)
                  y-res
                  {:result (->vec2 (:result x-res) (:result y-res))
                   :rest (:rest y-res)})))))))))

(defn claw-machine-parser [inp]
  (let [a-res ((button-parser "A") inp)]
    (if (error? a-res)
      a-res
      (let [nl1-res (nl (:rest a-res))]
        (if (error? nl1-res)
          nl1-res
          (let [b-res ((button-parser "B") (:rest nl1-res))]
            (if (error? b-res)
              b-res
              (let [nl2-res (nl (:rest b-res))]
                (if (error? nl2-res)
                  nl2-res
                  (let [prize-res (prize-parser (:rest nl2-res))]
                    (if (error? prize-res)
                      prize-res
                      {:result {:a (:result a-res)
                                :b (:result b-res)
                                :prize (:result prize-res)}
                       :rest (:rest prize-res)})))))))))))

(defn parse-input [inp]
  (let [first-res (claw-machine-parser inp)]
    (if (error? first-res)
      first-res
      (loop [result [(:result first-res)]
             cur-inp (:rest first-res)]
        (let [sep-res ((p-repeat 2 nl) cur-inp)]
          (if (error? sep-res)
            {:result result :rest cur-inp}
            (let [elem-res (claw-machine-parser (:rest sep-res))]
              (if (error? elem-res)
                {:result result :rest cur-inp}
                (recur (conj result (:result elem-res)) (:rest elem-res))))))))))

(def A-COST 3)
(def B-COST 1)

(defn make-inverse-mat [^vec2 a ^vec2 b]
  (let [ax (.row ^vec2 a)
        ay (.col ^vec2 a)
        bx (.row ^vec2 b)
        by (.col ^vec2 b)
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
        (or (and (integer? n-a) (integer? n-b) (>= n-a 0) (>= n-b 0) 
                 (+ (* n-a A-COST) (* n-b B-COST)))
            nil))
      (= (/ px ax) (/ py ay)) (solve-colinear px ax bx)
      :else nil)))

(defn reposition-prize [^long n ^vec2 prize] ^vec2
  (vec2+ prize (->vec2 n n)))

(defn part [shift s]
  (->> s
       string->stringbuf
       parse-input
       :result
       (keep (fn [{:keys [a b prize]}] (num-coins-needed a b (reposition-prize shift prize))))
       (reduce +)))

(defn part-2 [lines]
  (str (part 10000000000000 lines))) ; Convert the result to a string to avoid trailing `N`

(defn -main [& args]
  (let [filename (first args)
        lines (str/join "\n" (str/split-lines (slurp filename)))]
    (try
      (print (part-2 lines)) ; Use `print` to avoid adding a newline
      (catch Exception e
        (println "Error:" (.getMessage e))
        (.printStackTrace e)))))