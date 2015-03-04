(ns clojure-noob.fun-in-depth
  (:require [midje.sweet :refer :all]))

;; Programming to Abstractions

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; one function on different data structures
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn titleize
  [topic]
  (str topic " for the Brave and True"))

(facts "test titleize"

  (fact "titleize a vector"
    (map titleize ["Hamsters" "Ragnarok"])
    => '("Hamsters for the Brave and True" "Ragnarok for the Brave and True"))

  (fact "titleize a list"
    (map titleize '("Empathy" "Decorating"))
    => '("Empathy for the Brave and True" "Decorating for the Brave and True"))

  (fact
    (map titleize #{"Elbows" "Soap Carving"})
    => (contains '("Soap Carving for the Brave and True" "Elbows for the Brave and True")
                 :in-any-order)))

(defn label-key-val
  [[key val]]
  (str "key: " key ", val: " val))

(fact
  (map label-key-val {:name "Edward"
                      :occupation "perennial high-schooler"})
  => (contains '("key: :name, val: Edward"
                 "key: :occupation, val: perennial high-schooler")
               :in-any-order))

(fact "transform a map by applying a function to each of the map's values:"
  (map (fn [[key val]] [key (inc val)])
     {:max 30 :min 10})
  => (contains '([:max 31] [:min 11])
               :in-any-order))

(fact "convert back 'into' a map"
  (into {}
      (map (fn [[key val]] [key (inc val)])
           {:max 30 :min 10}))
  => {:max 31, :min 11})


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; the sequence abstraction:
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; The ISeq interface - alows many data structurs to act as sequences

(fact "'identity' returns whatever is passed to it."
  (identity "Stefan Salvatore from Vampire Diaries")
  => "Stefan Salvatore from Vampire Diaries")


(fact "a non sequenctial collection is conveted to a sequence using 'map' and 'identity'"
  (map identity {:name "Bill Compton" :occupation "Dead mopey guy"})
  => (contains '([:name "Bill Compton"] [:occupation "Dead mopey guy"]) :in-any-order))

(fact "the seq function converts a map data structure into a sequence of vectors"
  (seq {:name "Bill Compton" :occupation "Dead mopey guy"})
  => (contains '([:name "Bill Compton"] [:occupation "Dead mopey guy"]) :in-any-order))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; sequence function examples

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; map
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(fact "'map' applies a function over a sequence"
  (map inc [1 2 3])
  => '(2 3 4))

(fact "'map' applied over multiple sequences"
  (map str ["a" "b" "c"] ["A" "B" "C"])
  => '("aA" "bB" "cC")
  (map + [1 2 3] [1 2 3])
  => '(2 4 6))

;; map human consumption to critter consumption
(def human-consumption   [8.1 7.3 6.6 5.0])
(def critter-consumption [0.0 0.2 0.3 1.1])
(defn unify-diet-data
  [human critter]
  {:human human
   :critter critter})

(fact "apply to data"
  (map unify-diet-data human-consumption critter-consumption)
  => '({:human 8.1, :critter 0.0}
       {:human 7.3, :critter 0.2}
       {:human 6.6, :critter 0.3}
       {:human 5.0, :critter 1.1}))

;; map a collection of functions:
(def sum #(reduce + %))
(def avg #(/ (sum %) (count %)))
(defn stats
  [numbers]
  (map #(% numbers) [sum count avg]))

(fact

  (stats [3 4 10])
  => '(17 3 17/3)

  (stats [80 1 44 13 6])
  => '(144 5 144/5))




;;;;;;;;;;;;;;;;;;;;;;;;;;
;; reduce
;;;;;;;;;;;;;;;;;;;;;;;;;

(fact "use reduce to update a map data structure"
  (reduce (fn [new-map [key val]]
            (assoc new-map key (inc val)))
          {}
          {:max 30 :min 10})
  => {:max 31, :min 11})

(fact "filter out keys from a map based on their value"
  (reduce (fn [new-map [key val]]
            (if (> val 4)
              (assoc new-map key val)
              new-map))
          {}
          {:human 4.1
           :critter 3.9})
  => {:human 4.1})


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; take, drop, take-while, drop-while
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(fact (take 3 [1 2 3 4 5 6 7 8 9 10])
  => '(1 2 3))
(fact (drop 3 [1 2 3 4 5 6 7 8 9 10])
  => '(4 5 6 7 8 9 10))

(def food-journal
  [{:month 1 :day 1 :human 5.3 :critter 2.3}
   {:month 1 :day 2 :human 5.1 :critter 2.0}
   {:month 2 :day 1 :human 4.9 :critter 2.1}
   {:month 2 :day 2 :human 5.0 :critter 2.5}
   {:month 3 :day 1 :human 4.2 :critter 3.3}
   {:month 3 :day 2 :human 4.0 :critter 3.8}
   {:month 4 :day 1 :human 3.7 :critter 3.9}
   {:month 4 :day 2 :human 3.7 :critter 3.6}])

(fact "retrieve January February"
  (take-while #(< (:month %) 3) food-journal)
  => '({:day 1, :human 5.3, :month 1, :critter 2.3}
       {:day 2, :human 5.1, :month 1, :critter 2.0}
       {:day 1, :human 4.9, :month 2, :critter 2.1}
       {:day 2, :human 5.0, :month 2, :critter 2.5}))

(fact "drop January and February"
  (drop-while #(< (:month %) 3) food-journal)
  => '({:day 1, :human 4.2, :month 3, :critter 3.3}
       {:day 2, :human 4.0, :month 3, :critter 3.8}
       {:day 1, :human 3.7, :month 4, :critter 3.9}
       {:day 2, :human 3.7, :month 4, :critter 3.6}))

(fact "drop January then keep until April"
  (take-while #(< (:month %) 4)
              (drop-while #(< (:month %) 2) food-journal))
  => '({:day 1, :human 4.9, :month 2, :critter 2.1}
       {:day 2, :human 5.0, :month 2, :critter 2.5}
       {:day 1, :human 4.2, :month 3, :critter 3.3}
       {:day 2, :human 4.0, :month 3, :critter 3.8}))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; filter, some
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(fact "journal entries for human less than 5 liters"
  (filter #(< (:human %) 5) food-journal)
  => '({:day 1, :human 4.9, :month 2, :critter 2.1}
       {:day 1, :human 4.2, :month 3, :critter 3.3}
       {:day 2, :human 4.0, :month 3, :critter 3.8}
       {:day 1, :human 3.7, :month 4, :critter 3.9}
       {:day 2, :human 3.7, :month 4, :critter 3.6}))

(fact "Just January and February - similar to 'take-while'
       take-while is better with sorted data"
  (filter #(< (:month %) 3) food-journal)
  => '({:day 1, :human 5.3, :month 1, :critter 2.3}
       {:day 2, :human 5.1, :month 1, :critter 2.0}
       {:day 1, :human 4.9, :month 2, :critter 2.1}
       {:day 2, :human 5.0, :month 2, :critter 2.5}))

(facts "'some' returns the first true value returned by a predicate"
  (fact
    (some #(> (:critter %) 5) food-journal)
    => nil)
  (fact
    (some #(> (:critter %) 3) food-journal)
    => true))

(fact "return the actual value that was true"
  (some #(and (> (:critter %) 3) %) food-journal)
  => {:month 3 :day 1 :human 4.2 :critter 3.3})


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; sort, sort-by
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(fact "sort in ascending order"
  (sort [3 2 1])
  => '(1 2 3))

(fact "sort uning a 'key-function'"
  (sort-by count ["aaa" "c" "bb"])
  => '("c" "bb" "aaa"))


;;;;;;;;;;;;;;;;;;;;;;;;;;
;; concat
;;;;;;;;;;;;;;;;;;;;;;;;;;

(fact "appends 'flattens' sequences"
  (concat [1 2] [3 4])
  => '(1 2 3 4))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; lazy sequence
;; 'realizing' when needed
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

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
       (not (:has-pulse? record))))

(defn identify-vampire
  [social-security-numbers]
  (first (filter vampire?
                 (map vampire-related-details social-security-numbers))))

;; don't run when compiling or testing
;;(fact
;;  (time (identify-vampire (range 0 10000)))
;; ;; "Elapsed time: 32007.469024 msecs"
;;  => {:makes-blood-puns? true, :has-pulse? false, :name "Damon Salvatore"})

;; doall realizes all elements of a lazy sequence


;;;;;;;;;;;;;;;;;;;;;;
;; Infinite equences
;;;;;;;;;;;;;;;;;;;;;;

(fact "'repeat' infinite repeated sequence"
  (concat (take 8 (repeat "na")) ["Batman!"])
  => '("na" "na" "na" "na" "na" "na" "na" "na" "Batman!"))

;; repeates the output of a fucntion
;(take 3 (repeatedly (fn [] (rand-int 10))))
;; => (9 7 1)

(defn even-numbers
  "lazy infinite sequence of even numbers"
  ([] (even-numbers 0))
  ([n] (cons n (lazy-seq (even-numbers (+ n 2))))))

(fact "exercise 'even-numbers"
  (take 10 (even-numbers))
  => '(0 2 4 6 8 10 12 14 16 18))

(fact "remember that 'cons' returns a new list with an element prepended to the given list"
  (cons 0 '(2 4 6))
  => '(0 2 4 6))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; the collection abstraction:
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; sequence is about operating on members
;; collection is about operating operating on the structure as a whole.
;;  count, empty?,every?

(facts "about empty? operating on collections"
  (fact
    (empty? [])
    => true)
  (fact
    (empty? ["no!"])
    => false))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; into
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(facts "about into"
  (fact "'map' returns a 'seq'"
    (map identity {:sunlight-reation "Glitter!"})
    => '([:sunlight-reation "Glitter!"]))
  (fact "convert 'seq' 'into' a map"
    (into {} (map identity {:sunlight-reation "Glitter!"}))
    => {:sunlight-reation "Glitter!"}))

(facts "convert back to vector"
  (fact "'map returns a 'seq'"
    (map identity [:garlic :sesame-oil :fried-eggs])
    => '(:garlic :sesame-oil :fried-eggs))

  (fact  "'into' a vector"
    (into [] (map identity [:garlic :sesame-oil :fried-eggs]))
    => [:garlic :sesame-oil :fried-eggs]))

(fact "convert back to list"
  (map identity [:garlic-clove :garlic-clove])
  => '(:garlic-clove :garlic-clove))

(fact "set contain only unique values"
  (into #{} (map identity [:garlic-clove :garlic-clove]))
  => #{:garlic-clove})

(facts "the first argument of 'into' doesn't have to be empty"
  (fact
    (into {:favorite-emotion "glommy"} [[:sunlight-reaction "Glitter!"]])
    => {:favorite-emotion "glommy", :sunlight-reaction "Glitter!"})
  (fact
    (into ["cherry"] '("pine" "spruce"))
    => ["cherry" "pine" "spruce"]))

(fact "both arguments can be the same type:"
  (into {:favorite-animal "kitty"} {:least-favorite-smell "dog"
                                  :relationship-with-teenager "creepy"})
  =>
  {:favorite-animal "kitty"
   :relationship-with-teenager "creepy"
   :least-favorite-smell "dog"})


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; conj - add elements to a collection.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(facts "about conj"

  (fact "not so good - added the entire vector [1] onto [0]"
    (conj [0] [1])
    => [0 [1]])

  (fact "compare to:"
    (into [0] [1])
    => [0 1])

  (fact "Here's what we want"
    (conj [0] 1)
    => [0 1])

  (fact "we can supply multiple items"
    (conj [0] 1 2 3 4)
    => [0 1 2 3 4])

  (fact "we can add maps"
    (conj {:time "midnight"} [:place "ye olde cemetarum"])
    => {:place "ye olde cemetarum", :time "midnight"}))

(defn my-conj
  "conj defined in terms of into"
  [target & additions]
  (into target additions))

(facts "about my-conj"
  (fact "single value"
    (my-conj [0] 1)
    => [0 1])
  (fact "multiple values"
    (my-conj [0] 1 2 3)
    => [0 1 2 3]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; higher level fuctions
;; apply and partial

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; 'apply' - explodes a seqable collection to pass to a function that expects a 'rest' param
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn my-into
  "into defined in terms of 'conj' using 'apply"
  [collection additions]
  (apply conj collection additions))

(facts "about 'my-into'"
  (fact
    (my-into [0] [1 2 3])
    => [0 1 2 3])
  (fact "equivalent to"
    (conj [0] 1 2 3)
    => [0 1 2 3]))

(facts "'max' takes a rest argument"
  (fact "pass only one argument at a time"
    (max [0 1 2])
    => [0 1 2])
  (fact  "lets' explode the arguemnt:"
    (apply max [0 1 2])
    => 2))


;;;;;;;;;;;;;;;;
;; partial - takes a function an any number of arguements and returns a new function
;;   then when the new function is called it call the original function with the orignal
;;   arguements along with new arguements

(def add10 (partial + 10))
(fact (add10 3) => 13)
(fact (add10 5) => 15)


(def add-missing-element
  (partial conj ["water" "earth" "air"]))

(fact
  (add-missing-element "unobtainium" "adamantium")
  => ["water" "earth" "air" "unobtainium" "adamantium"])


(defn my-partial
  [partialized-fn & args]
  (fn [& more-args]
    (apply partialized-fn (into more-args (reverse args)))))

(def add20 (my-partial + 20))  ;; sets add20 to an anonymous function
                               ;; returned by 'my-partial'

(fact (add20 3)  => 23)

;; the anonymous function as defined by 'my-partial':
(fn [& more-args]
  (apply + (into [20] more-args)))  ; sweet!


;; another example:
(defn lousy-logger
  [log-level message]
  (condp = log-level
    :warn (clojure.string/lower-case message)
    :emergency (clojure.string/upper-case message)))

(def warn (partial lousy-logger :warn))

(fact
  (warn "Red light ahead")
  => "red light ahead")


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; complement -
;; Why, thank you!
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn identify-humans-1
  [social-security-numbers]
  (filter #(not (vampire? %))
          (map vampire-related-details social-security-numbers)))

(def not-vampire? (complement vampire?))

(defn identify-humans
  [social-security-numbers]
  (filter not-vampire?
          (map vampire-related-details social-security-numbers)))


(defn my-complement
  [pred?]
  (fn [& args]
    (not (apply pred? args))))

(def my-pos? (my-complement neg?))

(fact (my-pos? 1) => true)
(fact (my-pos? -1) => false)


;; see the 'FWPD' example in it's own project.
