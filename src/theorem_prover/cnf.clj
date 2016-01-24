(ns theorem-prover.cnf
  (:require [clojure.core.match :refer [match]]))

(defn negate
  [sentence]
  (match sentence
    (x :guard keyword?) ['not x]
    ['not x]            x
    [x 'and y]          [(negate x) 'or (negate y)]
    [x 'or y]           [(negate x) 'and (negate y)]))

(defn remove-implications
  [sentence]
  (match sentence
    ['not x]    ['not (remove-implications x)]
    [x 'and y]  [(remove-implications x) 'and (remove-implications y)]
    [x 'or y]   [(remove-implications x) 'or (remove-implications y)]
    [x '=> y]   [(negate (remove-implications x)) 'or (remove-implications y)]
    [x '<=> y]  [(remove-implications [x '=> y]) 'and (remove-implications [y '=> x])]
    x           x))

(defn move-negations-inwards
  [sentence]
  (match sentence
    ['not (x :guard keyword?)]  ['not x]
    ['not x]                    (move-negations-inwards (negate x))
    [x 'and y]                  [(move-negations-inwards x) 'and (move-negations-inwards y)]
    [x 'or y]                   [(move-negations-inwards x) 'or (move-negations-inwards y)]
    x                           x))

(defn distribute
  [sentence]
  (match sentence
    [x 'and y] [(distribute x) 'and (distribute y)]
    [x 'or [y 'and z]] (let [x (distribute x)
                             y (distribute y)
                             z (distribute z)]
                         (distribute [[x 'or y] 'and [x 'or z]]))
    [[x 'and y] 'or z] (let [x (distribute x)
                             y (distribute y)
                             z (distribute z)]
                         (distribute [[x 'or z] 'and [y 'or z]]))
    x x))

(defn clauses
  [sentence]
  (match sentence
    [x 'and y] (flatten [(clauses x) (clauses y)])
    [x 'or y] [(clojure.set/union (first (clauses x)) (first (clauses y)))]
    x [#{x}]))

(defn tautology?
  [clause]
  (some #(contains? clause (negate %)) clause))

(defn sentence->cnf
  [sentence]
  (-> sentence
      remove-implications
      move-negations-inwards
      distribute
      clauses
      ((partial remove tautology?))))
