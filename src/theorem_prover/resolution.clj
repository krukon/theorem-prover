(ns theorem-prover.resolution
  (:require [clojure.set :refer [union]]
            [theorem-prover.cnf :refer [negate]]))

(defn apply-resolution-rule
  [alpha beta]
  (let [complementary (into #{} (filter #(get beta (negate %)) alpha))
        not-complementary (into #{} (map negate complementary))]
    (when-not (empty? complementary)
      (-> #{}
          (into (remove complementary alpha))
          (into (remove not-complementary beta))))))

(defn prove
  ([clauses]
   (prove (set clauses) #{}))
  ([clauses checked]
   (if (empty? clauses)
     true
     (let [clause (first clauses)
           resolved (->> checked
                         (map (partial apply-resolution-rule clause))
                         (remove nil?)
                         (remove checked))]
       (if (some empty? resolved)
         false
         (recur (into (disj clauses clause) resolved)
                (conj checked clause)))))))
