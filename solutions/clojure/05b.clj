(ns aoc2024.day05b
  (:require [clojure.string :as str]))

;; --- Fixed parser combinator engine ---
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
            (recur (rest ps) (if (:result res) (conj result (:result res)) result) (:rest res))))))))
(defn chr [c]
  (fn [{:keys [str pos] :as buf}]
    (cond
      (buf-eof? buf) {:error :eof :input buf}
      (= (get str pos) c) {:result c :rest (inc-buf buf)}
      :else {:error :wrong-char :expected c :input buf})))
(defn charset+ [st]
  (fn [{:keys [str pos] :as buf}]
    (if (or (buf-eof? buf) (not (st (buf-peek buf))))
      {:error :no-match :input buf}
      (loop [i pos]
        (if (and (< i (count str)) (st (get str i)))
          (recur (inc i))
          {:result (subs str pos i) :rest {:str str :pos i}})))))
(def DIGITS #{\0 \1 \2 \3 \4 \5 \6 \7 \8 \9})
(def p-int
  (->> (p-seq
         (fn [inp]
           (let [r ((chr \-) inp)]
             (if (error? r) {:rest inp} {:result \- :rest (:rest r)})))
         (charset+ DIGITS))
       (p-map #(if (= (first %) \-)
                 (- (Long/parseLong (second %)))
                 (Long/parseLong (apply str %))))))
(def p-nl (chr \newline))
(defn label [ks p] (p-map #(zipmap ks %) p))

(defn list-+ [elem sep]
  (fn [inp]
    (let [first-result (elem inp)]
      (if (error? first-result)
        first-result
        (loop [acc [(:result first-result)]
               rest-inp (:rest first-result)]
          (let [sep-res ((skip sep) rest-inp)]
            (if (error? sep-res)
              {:result acc :rest rest-inp}
              (let [next-res (elem (:rest sep-res))]
                (if (or (nil? next-res) (error? next-res))
                  {:result acc :rest rest-inp}
                  (recur (conj acc (:result next-res)) (:rest next-res)))))))))))

;; --- Problem-specific parsers ---
(def parse-edge (p-seq p-int (skip (chr \|)) p-int))
(def parse-rules
  (->> (list-+ parse-edge p-nl)
       (p-map #(reduce (fn [m [from to]]
                           (-> m
                               (update from (fn [x] (conj (or x #{} ) to)))
                               (update to (fn [x] (or x #{})))))
                         {} %))))
(def parse-pages (list-+ p-int (chr \,)))
(def parse-updates (list-+ parse-pages p-nl))
(def parse-input (->> (p-seq parse-rules (skip p-nl) (skip p-nl) parse-updates)
                      (label [:rules :updates])))

;; --- Core logic ---
(defn right-order? [rules [from to]]
  ((rules from) to))

(defn part-2 [s]
  (let [{:keys [rules updates]} (->> s string->stringbuf parse-input :result)]
    (->> updates
         (filter #(not (every? (partial right-order? rules)
                               (partition 2 1 %))))
         (map (comp #(nth % (quot (count %) 2))
                    #(sort (fn [a b] (boolean (right-order? rules [a b]))) %)))
         (reduce +))))

(defn -main [& args]
  (let [filename (first args)
        content (slurp filename)]
    (println (part-2 content))))
