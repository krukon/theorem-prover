(ns theorem-prover.core
  (:gen-class)
  (:require [theorem-prover.cnf :as cnf]
            [theorem-prover.resolution :as resolution]))

(defn conjunct
  ([sentences]
   (conjunct (rest sentences) (first sentences)))
  ([sentences result]
   (if (empty? sentences)
     result
     [(first sentences) 'and (conjunct (rest sentences) result)])))

(defn prove
  [alpha beta]
  (let [alpha (if (set? alpha) (conjunct alpha) alpha)]
    (-> [alpha 'and ['not beta]]
        cnf/sentence->cnf
        resolution/prove
        not)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "..."))
