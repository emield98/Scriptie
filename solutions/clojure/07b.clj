(ns aoc2024.day07b
  (:require [clojure.string :as str]))

;; --- Minimal inlined parsecomb tools ---
(defn string->stringbuf [s] {:str s :pos 0})
(defn buf-eof? [{:keys [str pos]}] (>= pos (count str)))
(defn buf-peek [{:keys [str pos]}] (get str pos))
(defn buf-take [n {:keys [str pos]}] (subs str pos (+ n pos)))
(defn adv-buf [n buf] (update buf :pos (partial + n)))
(defn inc-buf [buf] (update buf :pos inc))
(defn error? [result] (contains? result :error))
(defn skip [p] (fn [inp] (let [res (p inp)] (if (error? res) res (dissoc res :result)))))
(defn p-map [f p] (fn [inp] (let [res (p inp)] (if (error? res) res (update res :result f)))))
(defn p-seq [& ps]
  (fn [inp]
    (loop [ps ps, result [], inp inp]
      (if (empty? ps)
        (condp = (count result)
          0 {:rest inp}
          1 {:result (first result) :rest inp}
          {:result result :rest inp})
        (let [res ((first ps) inp)]
          (if (error? res)
            res
            (recur (rest ps)
                   (if (:result res) (conj result (:result res)) result)
                   (:rest res))))))))
(defn chr [c]
  (fn [{:keys [str pos]}]
    (cond
      (buf-eof? {:str str :pos pos}) {:error :eof}
      (= (get str pos) c) {:result c :rest {:str str :pos (inc pos)}}
      :else {:error :wrong-char})))
(defn string [s]
  (fn [{:keys [str pos]}]
    (if (.startsWith (subs str pos) s)
      {:result s :rest {:str str :pos (+ pos (count s))}}
      {:error :wrong-string})))
(defn charset+ [st]
  (fn [{:keys [str pos]}]
    (if (not (st (get str pos)))
      {:error :no-match}
      (loop [i pos]
        (if (and (< i (count str)) (st (get str i)))
          (recur (inc i))
          {:result (subs str pos i) :rest {:str str :pos i}})))))
(def DIGITS #{\0 \1 \2 \3 \4 \5 \6 \7 \8 \9})
(def p-int
  (->> (p-seq
         (fn [inp]
           (let [r ((chr \-) inp)]
             (if (error? r)
               {:rest inp}
               {:result \- :rest (:rest r)})))
         (charset+ DIGITS))
       (p-map #(if (= (first %) \-)
                 (- (Long/parseLong (second %)))
                 (Long/parseLong (apply str %))))))
(def p-nl (chr \newline))
(defn list-+ [elem sep & {:keys [vectorize?]}]
  (p-map #(if vectorize? (into [(first %)] (second %)) (apply cons %))
         (p-seq elem
                (fn [inp]
                  (loop [inp inp res []]
                    (let [r ((p-seq (skip sep) elem) inp)]
                      (if (error? r)
                        {:result res :rest inp}
                        (recur (:rest r) (conj res (:result r))))))))))

(defn label [ks p]
  (p-map #(zipmap ks %) p))

;; --- Parsers ---
(def parse-numbers (->> (p-seq p-int (skip (string ": "))
                                (list-+ p-int (chr \space) :vectorize? true))
                        (label [:target :terms])))
(def parse-input (list-+ parse-numbers p-nl))

;; --- Helpers ---
(defn deconcat [target x]
  (let [st (str target)
        sx (str x)]
    (if (str/ends-with? st sx)
      (parse-long (subs st 0 (- (count st) (count sx))))
      nil)))

(defn inverse [op target x]
  (case op
    :+ (- target x)
    :* (let [d (quot target x)
             r (mod target x)]
         (if (zero? r) d nil))
    :|| (deconcat target x)))

(defn determine-ops [target terms ops]
  (if (= 1 (count terms))
    (if (= target (first terms))
      [[:1 target]]
      nil)
    (->> ops
         (some #(let [next (peek terms)]
                  (some-> (inverse % target next)
                          (determine-ops (pop terms) ops)
                          (conj [% next])))))))

;; --- Part 2 ---
(defn part-2 [s]
  (->> s
       string->stringbuf
       parse-input
       :result
       (filter (fn [{:keys [target terms]}]
                 (determine-ops target terms [:+ :* :||])))
       (map :target)
       (reduce +)))

(defn -main [& args]
  (let [filename (first args)
        content (slurp filename)]
    (println (part-2 content))))
