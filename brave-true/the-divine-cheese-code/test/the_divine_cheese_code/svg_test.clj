(ns the-divine-cheese-code.visualization.svg-test
 (:require [midje.sweet :refer :all]
           [the-divine-cheese-code.visualization.svg  :exclude [min max]]
           ))


(def ohio [{:location "Cleveland"
            :cheese-name "Croquis"
            :lat 41.50
            :lng -81.70}
           {:location "Middlefield"
            :cheese-name "Swiss"
            :lat 41.46
            :lng -81.06}])

;(fact "test svg/min and svg/max"
; (min [{:a 1 :b 3} {:a 5 :b 0}])  => {:a 1 :b 0}
 ; (min ohio) => {:lat 41.46 :lng -81.7}
 ; (max ohio) => {:lat 41.5 :lng -81.06})


(fact "example of 'merge-with'"
  (merge-with - {:lat 50 :lng 10} {:lat 5 :lng 5})
  => {:lat 45 :lng 5})



;(fact "latlng->point: should convert a location map to a string"
;  (latlng->point {}) => ","
;  (latlng->point {:lat 33 :lng -44}) => "33,-44")

;(fact "points should produce a string of locations given 
;       a sequence of maps of locations"
;  (points [{:lat 33  :lng -44} {:lat 45 :lng -88}])
;  => "33,-44 45,-88")
