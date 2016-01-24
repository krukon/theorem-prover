(ns theorem-prover.cnf-test
  (:require [midje.sweet :refer :all]
            [theorem-prover.cnf :refer :all]))

(facts "about negate"
  (fact "negates keywords"
    (negate :a) => ['not :a])
  (fact "removes heading not"
    (negate ['not :a]) => :a)
  (fact "negates complex sentences"
    (negate [:a 'and :b]) => [['not :a] 'or ['not :b]]
    (negate [:a 'or :b]) => [['not :a] 'and ['not :b]]
    (negate [[:a 'and :b] 'and [['not :c] 'or :d]]) => [[['not :a] 'or ['not :b]] 'or [:c 'and ['not :d]]]))

(facts "about remove-implications"
  (facts "changes implication to disjunction"
    (remove-implications [:a '=> :b]) => [['not :a] 'or :b])
  (fact "changes equivalence to a conjuntion of two disjunctions"
    (remove-implications [:a '<=> :b]) => [[['not :a] 'or :b] 'and [['not :b] 'or :a]])
  (fact "removes implications on deeper level"
    (remove-implications [[:a '=> :b] 'and [:c '=> :d]]) => [[['not :a] 'or :b] 'and [['not :c] 'or :d]]))

(facts "about move-negations-inwards"
  (fact "doesn't affect keywords"
    (move-negations-inwards :a) => :a
    (move-negations-inwards ['not :a]) => ['not :a])
  (fact "moves negation to a deeper level"
    (move-negations-inwards ['not [:a 'and :b]]) => [['not :a] 'or ['not :b]]))

(facts "about distribute"
  (fact "applies distribution law"
    (distribute [:a 'or [:b 'and :c]]) => [[:a 'or :b] 'and [:a 'or :c]]
    (distribute [[:a 'and :b] 'or  :c]) => [[:a 'or :c] 'and [:b 'or :c]]))

(facts "about sentence->cnf"
  (fact "returns a set of cnf clauses"
    (sentence->cnf :a) => (just [#{:a}])
    (sentence->cnf [:a 'and :b]) => (just [#{:a}
                                           #{:b}]
                                          :in-any-order)
    (sentence->cnf [:a '<=> :b]) => (just [#{['not :a] :b}
                                           #{['not :b] :a}]
                                          :in-any-order)
    (sentence->cnf [[:a '=> :b] 'or :c]) => (just [#{['not :a] :c :b}])
    (sentence->cnf [[:a '<=> :b] 'or [:a 'and [:b 'or :c]]])
    => (just [#{['not :b] :a}
              #{['not :a] :c :b}]
             :in-any-order)))
