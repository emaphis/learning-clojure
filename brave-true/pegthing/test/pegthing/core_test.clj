(ns pegthing.core-test
  (:require [clojure.test :refer :all]
            [pegthing.core :refer :all]
            [midje.sweet :refer :all]))

(deftest a-test
  (testing "I work!"
    (is (not (= 0 1)))))


(fact "I work too!!!"
  (+ 1 2) => 3)

(facts "about tri"
  (fact (take 1 tri) => '(1))
  (fact (take 3 tri) => '(1 3 6))
  (fact (take 5 tri) => '(1 3 6 10 15)))

(fact "testing 'triangular?"
  (triangular? 5) => falsey
  (triangular? 6) => truthy)

(fact "testing row-tri"
  (row-tri 1) => 1
  (row-tri 2) => 3
  (row-tri 3) => 6)

(fact "testing 'row-num'"
  (row-num 1) => 1
  (row-num 2) => 2
  (row-num 3) => 2
  (row-num 5) => 3)

(fact "is every position less than or equal  to the max postion"
  (in-bounds? 10 10)  => truthy
  (in-bounds? 10 11) => falsey
  (in-bounds? 10 9 3 0 5 2) => truthy
  (in-bounds? 10 9 3 10 5 2) => truthy
  (in-bounds? 10 9 3 11 5 2) => falsey)

(facts "test connect"
  (fact "good connection"
    (connect {} 15 1 2 4) => {1 {:connections {4 2}}
                              4 {:connections {1 2}}})
  (fact "out of bounds"
    (connect {} 15 7 11 16) => {}))

(fact "'assoc-in' return a new map with a given value
         at the specified nesting"
  (assoc-in {} [:level1 :level2 :home] "given")
  => {:level1 {:level2 {:home "given"}}}
  (assoc-in {} [1 :connections 4] 2)
  => {1 {:connections {4 2}}})

(fact "'get-in' lets you look up values in nested maps"
  (get-in {:level1 {:level2 {:home "given"}}} [:level1 :level2])
  => {:home "given"})


(facts "text making connections"
  (fact (connect-down-left {} 15 1)
    => {1 {:connections {4 2}}
        4 {:connections {1 2}}})
  (fact (connect-down-right {} 15 3)
    => {3  {:connections {10 6}}
        10 {:connections {3 6}}}))

(fact "test 'add-pos"
  (add-pos {} 15 1)
  => {1 {:connections {6 3, 4 2}, :pegged true}
      4 {:connections {1 2}}
      6 {:connections {1 3}}})

(fact (new-board 3)
  => {5 {:pegged true},
      3 {:pegged true},
      2 {:pegged true},
      6 {:pegged true, :connections {4 5, 1 3}},
      4 {:pegged true, :connections {6 5, 1 2}},
      1 {:connections {6 3, 4 2}, :pegged true},
      :rows 3} )

(facts "test pegged?, remove-peg, add-peg, move-peg"
  (let [board (new-board 3)]  ;; 1 >= pos >= 7
    (fact "pegged?"
      (pegged? board 3) => true
      (pegged? board 7) => nil)
    (fact "remove-peg"
      (pegged? (remove-peg board 3) 3) => false)
    (let [board-r (remove-peg board 3)]
      (pegged? (add-peg board-r 3) 3) => true
      (pegged? (move-peg board-r 4 3) 3) => true)))

;(def my-board (assoc-in (new-board 5) [4 :pegged] false))

(fact "valid moves"
  (valid-moves my-board 1)   => {4 2}
  (valid-moves my-board 6)   => {4 5}
  (valid-moves my-board 11)  => {4 7}
  (valid-moves my-board 13)  => {4 8}
  (valid-moves my-board 5)   => {}
  (valid-moves my-board 8)   => {})

(fact "test valid-move?"
  (valid-move? my-board 8 4) => nil
  (valid-move? my-board 1 4) => 2)

(fact "test 'make-move'"
  (let [board-m (make-move my-board 1 4)]
    (pegged? board-m 1) => false
    (pegged? board-m 2) => false
    (pegged? board-m 4) => true))
