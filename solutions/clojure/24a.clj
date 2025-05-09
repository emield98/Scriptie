(ns aoc2024.day24
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [clojure.data.priority-map :refer [priority-map]])
  (:import [java.util ArrayDeque]))

;; ======= Parsecomb Implementation =======

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

(defn charset [st]
  (fn [buf]
    (cond
      (buf-eof? buf) {:error :eof :input buf}
      (st (buf-peek buf)) {:result (buf-peek buf) :rest (inc-buf buf)}
      :else {:error :wrong-char :expected st :input buf})))

(defn any-char [buf]
  (if (buf-eof? buf)
    {:error :eof :input buf}
    {:result (buf-peek buf) :rest (inc-buf buf)}))

(defn charset+
  [st]
  (fn [{:keys [str pos] :as buf}]
    (cond
      (buf-eof? buf) {:error :eof :input buf}
      (not (st (buf-peek buf))) {:error :wrong-char :expected st :input buf}
      :else
      (loop [idx (inc pos)]
        (if (or (>= idx (count str)) (not (st (get str idx))))
          {:result (buf-take (- idx pos) buf) :rest (adv-buf (- idx pos) buf)}
          (recur (inc idx)))))))

(def DIGITS #{\0 \1 \2 \3 \4 \5 \6 \7 \8 \9})
(def UCASE (into #{} "ABCDEFGHIJKLMNOPQRSTUVWXYZ"))
(def LCASE (into #{} "abcdefghijklmnopqrstuvwxyz"))
(def LETTERS (set/union UCASE LCASE))
(def ALPHANUMERIC (set/union LETTERS DIGITS))
(def WHITESPACE #{\space \newline \return \tab})

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

(defn some* [p]
  (fn [inp]
    (loop [inp inp
           res []]
      (let [r (p inp)]
        (if (error? r)
          {:result res :rest inp}
          (recur (:rest r) (if (:result r) (conj res (:result r)) res)))))))

(defn some+ [p]
  (->> (p-seq p
              (some* p))
       (p-map #(apply cons %))))

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

(defn maybe
  "Does not contain a :result if the parser fails"
  [p]
  (fn [inp]
    (let [res (p inp)]
      (if (error? res)
        {:rest inp}
        res))))


(def p-int
  "parses a (possibly negative) integer"
  (->> (p-seq (maybe (chr \-))
              (charset+ DIGITS))
       (p-map #(if (= \- (first %))
                  (- (Long/parseUnsignedLong (apply str (second %))))
                  (Long/parseUnsignedLong (apply str %))))))


(def ws "parse whitespace" (charset+ WHITESPACE))
(def skip-ws "parser that skips whitespace" (skip ws))
(def nl "parses a newline character" (chr \newline))
(def skip-nl "parser that skips a newline" (skip nl))
(defn skip-string "parser that skips string s" [s] (skip (string s)))

(defn label
  "Given a parser p, combines each key in ks with the corresponding element of
  the successful result of p into a map."
  [ks p]
  (p-map #(zipmap ks %) p))

;; ======= Searches Implementation =======

(defn nil-visit
  ([] nil)
  ([_ _] nil-visit))

(defn conj-visit [init-state]
  (fn
    ([] init-state)
    ([_prevs next] (conj-visit (conj init-state next)))))

(defn- dfs-helper [neighfunc src stop? visit visiting? prevs]
  (if (stop? src)
    [(visit prevs src) visiting? prevs]
    (let [neighs (neighfunc src)]
      (fn []
        (if-let [res
                 (->> neighs
                      (reduce
                        (fn [[visit visiting? prevs] neigh]
                          (cond
                            (visiting? neigh) (reduced nil) ; cycle detected
                            (prevs neigh) [visit visiting? prevs]
                            :else
                            (or (trampoline dfs-helper neighfunc neigh stop?
                                            visit visiting? (assoc prevs neigh src))
                                (reduced nil))))
                        [visit (conj visiting? src) prevs]))]
          (let [[visit visiting? prevs] res]
            [(visit prevs src) (disj visiting? src) prevs])
          nil)))))

(defn dfs
  ([neighfunc srcs stop?] (dfs neighfunc srcs stop? nil-visit))
  ([neighfunc srcs stop? visit]
   (if-let [res (->> srcs
                     (reduce
                       (fn [[visit visiting? prevs] src]
                         (or (trampoline dfs-helper neighfunc src stop? visit visiting? prevs)
                             (reduced nil)))
                       [visit #{} {}]))]
     res
     nil)))

;; ======= Main Program Logic =======

(def wire-name (charset+ (into #{} (concat LETTERS DIGITS))))
(def wire (->> (p-seq wire-name (skip-string ": ") p-int)
               (label [:name :val])))
(def gate-name (->> (p-or (string "AND") (string "XOR") (string "OR"))
                    (p-map #(case % "AND" bit-and "XOR" bit-xor "OR" bit-or))))
(def gate (->> (p-seq wire-name skip-ws gate-name skip-ws wire-name
                        (skip-string " -> ") wire-name)
               (p-map (fn [[a op b out]]
                          [(gensym "gate-") {:args [a b] :op op :out out}]))))

(def parse-input (->> (p-seq (list-+ wire nl)
                               skip-nl
                               skip-nl
                               (list-+ gate nl))
                      (p-map (fn [[wires gates]]
                                 {:wires wires
                                  :gates (into {} gates)
                                  :graph (reduce (fn [g [id {:keys [args out]}]]
                                                   (reduce (fn [g w]
                                                             (-> g
                                                                 (update w #(conj (or % #{}) id))
                                                                 (update id #(conj (or % #{}) out))))
                                                           g args))
                                                 {} gates)}))))

(defn eval-network [wires gates order]
  (reduce (fn [state node]
            (let [{:keys [op args out]} (gates node)]
              (assoc state out (apply op (map state args)))))
          (into {} (map (juxt :name :val) wires))
          order))

(defn parse-answer [prefix state]
  (->> state
       (filter (fn [[k _]] (str/starts-with? k prefix)))
       (sort-by first #(compare %2 %1))
       (map second)
       (apply str)
       (#(Long/parseLong % 2))))

(defn part-1 [input]
  (let [{:keys [wires gates graph]} (-> input string->stringbuf parse-input :result)
        [visit _] (dfs graph (map :name wires) (constantly false) (conj-visit []))
        order (->> (visit) (filter symbol?) reverse vec)
        state (eval-network wires gates order)]
    (parse-answer "z" state)))

(defn -main [& [file]]
  (println (part-1 (slurp (or file "input")))))
