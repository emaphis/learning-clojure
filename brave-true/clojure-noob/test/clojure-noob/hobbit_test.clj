(ns clojure-noob.core-test
  (:require [midje.sweet :refer :all]
            [clojure-noob.hobbit :refer :all]))

(fact "test 'needs-matching-part'"
  (needs-matching-part? {:name "left-eye"}) => truthy
  (needs-matching-part? {:name "neckbeard"}) => falsey)

(fact "test 'make-matching-part'"
  (make-matching-part {:name "left-eye" :size 1})
 => {:name "right-eye" :size 1})

(fact "test 'symmetrize-body-parts"
  (symmetrize-body-parts asym-hobbit-body-parts)
  =>  ;;the following is the return value
  [{:name "head", :size 3}
   {:name "left-eye", :size 1}
   {:name "right-eye", :size 1}
   {:name "left-ear", :size 1}
   {:name "right-ear", :size 1}
   {:name "mouth", :size 1}
   {:name "nose", :size 1}
   {:name "neck", :size 2}
   {:name "left-shoulder", :size 3}
   {:name "right-shoulder", :size 3}
   {:name "left-upper-arm", :size 3}
   {:name "right-upper-arm", :size 3}
   {:name "chest", :size 10}
   {:name "back", :size 10}
   {:name "left-forearm", :size 3}
   {:name "right-forearm", :size 3}
   {:name "abdomen", :size 6}
   {:name "left-kidney", :size 1}
   {:name "right-kidney", :size 1}
   {:name "left-hand", :size 2}
   {:name "right-hand", :size 2}
   {:name "left-knee", :size 2}
   {:name "right-knee", :size 2}
   {:name "left-thigh", :size 4}
   {:name "right-thigh", :size 4}
   {:name "left-lower-leg", :size 3}
   {:name "right-lower-leg", :size 3}
   {:name "left-achilles", :size 1}
   {:name "right-achilles", :size 1}
   {:name "left-foot", :size 2}
   {:name "right-foot", :size 2}])


(fact "symmetrize-body-parts should be equivalent to better-symmetrize-body-parts"
  (better-symmetrize-body-parts asym-hobbit-body-parts)
  =>
  (symmetrize-body-parts asym-hobbit-body-parts))

;;(hit asym-hobbit-body-parts)

