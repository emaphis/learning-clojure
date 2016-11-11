(ns clojure-noob.core-test
  (:require [clojure.test :refer :all]
            [clojure-noob.core :refer :all]))

(deftest a-test
  (testing "now I work"
    (is (= 1 1))))


(deftest addition-tests
  (is (= 5 (+ 3 2)))
  (is (= 10 (+ 5 5))))

(deftest subtraction-tests
  (is (= 1 (- 4 3))
      (= 3 (- 7 4))))

(testing "Arithmetic"
  (testing "with positive integers"
    (is (= 3 (+ 2 2)))
    (is (= 7 (+ 3 4))))
  (testing "with negative integer"
    (is (= -4 (+ -2 -2))
        (is (= -1 (+ 3 4))))))


