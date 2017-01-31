(ns clojure-noob.04-core-functions)


;; Treating Lists, Vectors, Sets and Maps as Sequences

(defn titleize
  [topic]
  (str topic " for the Brave and True"))

(map titleize ["Hamsters" "Ragnarok"])
;; => ("Hamsters for the Brave and True" "Ragnarok for the Brave and True")


(map titleize '("Empathy" "Decorating"))
;; => ("Empathy for the Brave and True" "Decorating for the Brave and True")

(map titleize #{"Elbows" "Soap Carying"})
;; => ("Soap Carying for the Brave and True" "Elbows for the Brave and True")


(map #(titleize (second %)) {:uncomfortable-thing "Winking"})
;; => ("Winking for the Brave and True")


;;; first, rest, cons

(+ 3 4)
;; => 7

;; The seq function
(seq '(1 2 3))
;; => (1 2 3)

(seq [1 2 3])
;; => (1 2 3)

(seq #{1 2 3})
;; => (1 3 2)

(seq {:name "Bill Compton" :occupation "Dead mopey guy"})
;; => ([:name "Bill Compton"] [:occupation "Dead mopey guy"])

;; convert the seq back into a map by using into to stick the result into an empty map
(into {} (seq {:a 1 :b 2 :c 3}))
;; => {:a 1, :b 2, :c 3}

;; seq is defined in terms of 'first', rest, cons
;; can use functions define in these terms: reduce, filter, distinct, group-by

;;; Seq Function Examples
;; map:
(map inc [1 2 3])
;; => (2 3 4)

;; map can be applied to multiple collections:
(map str ["a" "b" "c"] ["A" "B" "C"])  ; sort of zipish
;; => ("aA" "bB" "cC")

;; sorta like =>
(list (str "a" "A") (str "b" "B") (str "c" "C"))
;; => ("aA" "bB" "cC")


;; extended example:

(def human-consumption [8.1 7.3 6.6 5.0])
(def critter-consumtion [0.0 0.2 0.3 1.1])

(defn unify-diet-data
  [human critter]
  {:human human
   :critter critter})

(map unify-diet-data human-consumption critter-consumtion)
;; ({:human 8.1, :critter 0.0}
;;  {:human 7.3, :critter 0.2}
;;  {:human 6.6, :critter 0.3}
;;  {:human 5.0, :critter 1.1})


;; passing collections of functions:

(def sum #(reduce + %))
(def avg #(/ (sum %) (count %)))
(defn stats
  [numbers]
  (map #(% numbers) [sum count avg]))

(stats [3 4 10])
;; => (17 3 17/3)

(stats [80 1 44 13 6])
;; => (144 5 144/5)

;; mapping keywords over collections of maps;
(def identities
  [{:alias "Batman" :real "Bruce Wayne"}
   {:alias "Spider-Man" :real "Peter Parker"}
   {:alias "Santa" :real "Your mom"}
   {:alias "Easter Bunny" :real "Your dad"}])

(map :real identities)
;; => ("Bruce Wayne" "Peter Parker" "Your mom" "Your dad")

(map :alias identities)
;; => ("Batman" "Spider-Man" "Santa" "Easter Bunny")


;;; reduce
;; reduce can be used on a collection to build a result,
;; but it has other purposes

(reduce (fn [new-map [key val]]
          (assoc new-map key (inc val)))
        {}
        {:max 30 :min 11})
;; => {:max 31, :min 12}

;; assoc 'adds' a key and a value to a map:
(assoc {:a 1} :b 2)
;; => {:a 1, :b 2}


;; reduce can be use to filter out keys from a map based on value
(reduce (fn [new-map [key val]]
          (assoc new-map key (inc val)))
        {}
        {:max 30 :min 10})
;; => {:max 31, :min 11}


;; Another use for reduce is to filter out keys from a map based on their value
(reduce (fn [new-map [key val]]
          (if (> val 4)
            (assoc new-map key val)
            new-map))
        {}
        {:human 4.1
         :critter 3.9
         :fine-wine 8.0})
;; => {:human 4.1, :fine-wine 8.0}


;;; take, drop, take-while, and drop-while

(take 3 [1 2 3 4 5 6 7 9 9 10])
;; => (1 2 3)

(drop 3 [1 2 3 4 5 6 7 9 9 10])
;; => (4 5 6 7 9 9 10)


(def food-journal
  [{:month 1 :day 1 :human 5.3 :critter 2.3}
   {:month 1 :day 2 :human 5.1 :critter 2.0}
   {:month 2 :day 1 :human 4.9 :critter 2.1}
   {:month 2 :day 2 :human 5.0 :critter 2.5}
   {:month 3 :day 1 :human 4.2 :critter 3.3}
   {:month 3 :day 2 :human 4.0 :critter 3.8}
   {:month 4 :day 1 :human 3.7 :critter 3.9}
   {:month 4 :day 2 :human 3.7 :critter 3.6}])


;; just January and February
(take-while #(< (:month %) 3) food-journal)

;; ({:month 1, :day 1, :human 5.3, :critter 2.3}
;;  {:month 1, :day 2, :human 5.1, :critter 2.0}
;;  {:month 2, :day 1, :human 4.9, :critter 2.1}
;;  {:month 2, :day 2, :human 5.0, :critter 2.5})

;; everything after the beginning of March
(drop-while #(< (:month %) 3) food-journal)

;; ({:month 3, :day 1, :human 4.2, :critter 3.3}
;;  {:month 3, :day 2, :human 4.0, :critter 3.8}
;;  {:month 4, :day 1, :human 3.7, :critter 3.9}
;;  {:month 4, :day 2, :human 3.7, :critter 3.6})

;; data from February and March

(take-while #(< (:month %) 4)
            (drop-while #(< (:month %) 2) food-journal))

;; ({:month 2, :day 1, :human 4.9, :critter 2.1}
;;  {:month 2, :day 2, :human 5.0, :critter 2.5}
;;  {:month 3, :day 1, :human 4.2, :critter 3.3}
;;  {:month 3, :day 2, :human 4.0, :critter 3.8})


;;; filter and some

;; human consumption less than 5 liters

(filter #(< (:human %) 5) food-journal)

;; ({:month 2, :day 1, :human 4.9, :critter 2.1}
;;  {:month 3, :day 1, :human 4.2, :critter 3.3}
;;  {:month 3, :day 2, :human 4.0, :critter 3.8}
;;  {:month 4, :day 1, :human 3.7, :critter 3.9}
;;  {:month 4, :day 2, :human 3.7, :critter 3.6})


(filter #(< (:month %) 3) food-journal)

;; ({:month 1, :day 1, :human 5.3, :critter 2.3}
;;  {:month 1, :day 2, :human 5.1, :critter 2.0}
;;  {:month 2, :day 1, :human 4.9, :critter 2.1}
;;  {:month 2, :day 2, :human 5.0, :critter 2.5})


;; some function

(some #(> (:critter %) 5) food-journal)
;; => nil

(some #(> (:critter %) 3) food-journal)
;; => true

;; to return the element
(some #(and (> (:critter %) 3) %) food-journal)
;; => {:month 3, :day 1, :human 4.2, :critter 3.3}


;; sort and sort-by

(sort [3 1 2])
;; => (1 2 3)

(sort-by count ["aaa" "c" "bb"])
;; => ("c" "bb" "aaa")

;; concat

(concat [1 2] [3 4])
;; => (1 2 3 4)


;;; Lazy Seqs - efficiency

(def vampire-database
  {0 {:makes-blood-puns? false, :has-pulse? true  :name "McFishwich"}
   1 {:makes-blood-puns? false, :has-pulse? true  :name "McMackson"}
   2 {:makes-blood-puns? true,  :has-pulse? false :name "Damon Salvatore"}
   3 {:makes-blood-puns? true,  :has-pulse? true  :name "Mickey Mouse"}})

(defn vampire-related-details
  [social-security-number]
  (Thread/sleep 1000)
  (get vampire-database social-security-number))

(defn vampire?
  [record]
  (and (:makes-blood-puns? record)
       (not (:has-pulse? record))
       record))

(defn identify-vampire
  [social-security-numbers]
  (first (filter vampire?
                 (map vampire-related-details social-security-numbers))))

;; Timing a call:
(time (vampire-related-details 0))
;; => "Elapsed time: 1000.309815 msecs"
;; => {:makes-blood-puns? false, :has-pulse? true, :name "McFishwich"}

;; map is lazy so it returns almost instantly
(time (def mapped-details (map vampire-related-details (range 0 100000))))
;; => "Elapsed time: 0.230798 msecs"
;; => #'clojure-noob.04-core-functions/mapped-details

;; realized in 32 unit chunks
(time (first mapped-details))
;; => "Elapsed time: 32005.408109 msecs"
;; => {:makes-blood-puns? false, :has-pulse? true, :name "McFishwich"}


;; now almost instantaneous
(time (first mapped-details))
;; "Elapsed time: 0.273958 msecs"
;; => {:makes-blood-puns? false, :has-pulse? true, :name "McFishwich"}

;; now identify the suspect:
(time (identify-vampire (range 0 1000000)))
;; =>"Elapsed time: 32006.990619 msecs"
;; => {:makes-blood-puns? true, :has-pulse? false, :name "Damon Salvatore"}


;;; Infinite Sequences

;; repeat creates an infinite sequence
(concat (take 8 (repeat "na")) ["batman!"])
;; => ("na" "na" "na" "na" "na" "na" "na" "na" "batman!")

(take 3 (repeatedly (fn [] (rand-int 10))))
;; => (7 4 9)
;; => (3 2 8)

;; custom definition
(defn even-numbers
  ([] (even-numbers 0))
  ([n] (cons n (lazy-seq (even-numbers (+ n 2))))))

(take 10 (even-numbers))
;; => (0 2 4 6 8 10 12 14 16 18)

;; cons returns a new list
(cons 0 '(2 4 6))
;; => (0 2 4 6)


;;; The collection Abstraction: into, conj

;; the seq abstractions is about the whole data collection
;; the collection abstraction is about individual items

;; for instance count, empty?, and every? about the whole
(empty? [])
;; => true

(empty? ["no!"])
;; => false

;; into
;; convert a seq into a different value

(map identity {:sunlight-reaction "Glitter!"})
;; => ([:sunlight-reaction "Glitter!"])

(into {} (map identity {:sunlight-reaction "Glitter!"}))
;; => {:sunlight-reaction "Glitter!"}


(map identity [:garlic :sesame-oil :firied-eggs])
;; => (:garlic :sesame-oil :firied-eggs)

(into [] (map identity [:garlic :sesame-oil :firied-eggs]))
;; => [:garlic :sesame-oil :firied-eggs]


(map identity [:garlic-clove :garlic-clove])
;; => (:garlic-clove :garlic-clove)

(into #{} (map identity [:garlic-clove :garlic-clove]))
;; => #{:garlic-clove}

