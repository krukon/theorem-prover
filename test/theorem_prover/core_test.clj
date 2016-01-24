(ns theorem-prover.core-test
  (:require [midje.sweet :refer :all]
            [theorem-prover.core :refer :all]))

(facts "about prove"
  (fact "proves stuff"
    (prove :a :a) => true
    (prove :a ['not :a]) => false
    (prove #{[:a '=> :b]
             [:b '=> :c]}
           [:a '=> :c]) => true
    (prove [:a '<=> :b]
           [:a '=> :b]) => true
    (prove #{[:a 'or :b]
             [['not :b] 'and :c]}
           :a) => true
    (prove #{[:a '=> :b]
             :a}
           :b) => true
    (prove #{:a ['not :b]}
           :c) => false))
