(ns clojure-programming.chp03
  (:require [midje.sweet :refer :all]))

"Collections and Data Structures  chpt: 03  page: 83"

(facts "Each collection has it's own literal syntax"
  (fact (type '(a b :name 12.5)) => clojure.lang.PersistentList)
  (fact (type ['a 'b :name 12.5]) => clojure.lang.PersistentVector)
  (fact (type {:name "Chas" :age 31}) => clojure.lang.PersistentArrayMap)
  (fact (type #{1 2 3}) => clojure.lang.PersistentHashSet)
  (fact (type {Math/PI "~3.14"
               [:composite "key"] 42
               nil "nothing"})  => clojure.lang.PersistentArrayMap)
  (fact (type #{{:first-name "chas" :last-name "emerick"}
                {:first-name "brian" :last-name "carper"}
                {:first-name "christophe" :last-name "grand"}})
    => clojure.lang.PersistentHashSet))

"
Clojure collections are idiomatically used in terms of abstractions and not as
concrete implementations.
They are immutable and persistent.
"

[[:section {:title "Abstractions over Implementations --  page: 84"}]]

"
Clojures insistence on using abstractions over collections is similar to polymorphism
and interface types in OO languages, but more general and more powerful.

"
(def v [1 2 3])

(facts "Some operations on a vector:"
  (fact (conj v 4)
    => [1 2 3 4])
  (fact (conj v 4 5)
    => [1 2 3 4 5])
  (fact (seq v)
    => '(1 2 3)))

(def m {:a 5 :b 6})

(facts "Same operations work on maps:"
  (fact (conj m [:c 7])
    => {:c 7, :b 6, :a 5})
  (fact (seq m)
    => '([:b 6] [:a 5])))

(def s #{1 2 3})

(facts "...and sets:"
  (fact (conj s 10)
    => #{1 3 2 10})
  (fact (conj s 3 4)
    => #{1 4 3 2})
  (fact (seq s)
    => '(1 3 2)))

(def lst '(1 2 3))

(facts "...and lists:"
  (fact (conj lst 0)
    => '(0 1 2 3))
  (fact (conj lst 0 -1)
    => '(-1 0 1 2 3))
  (fact (seq lst)
    => '(1 2 3)))

"
'conj' and 'seq' provide a polymorphic and general interface to the collections. 'Conj' add an item to the collection in the most efficient way for that collection. The front
lists and the end of vectors for instance. 'Seq' provides a common view over collections. This provides a small interface that can be used to build other functions.
"

(facts "for example 'into':"
  (fact (first '((1 2 3) 4 5 6))
    => '(1 2 3))
  (fact (into v [4 5])
    => [1 2 3 4 5])
  (fact (into m [[:c 7] [:d 8]])
    => {:c 7, :b 6, :d 8, :a 5})
  (fact (into #{1 2} [2 3 4 5 3 3 2])
    => #{1 4 3 2 5})
  (fact (into [1] {:a 1 :b 2})
    => [1 [:b 2] [:a 1]]))

"
A concrete implementation doesn't have to implement the full abstraction to be useful.

There seven primary abstractions that Clojures data structures implement:
- Collection
- Sequence
- Associative
- Indexed
- Stack
- Set
- Sorted
"

[[:subsection {:title "Collection --  page: 87"}]]

"
All data structures implement the 'collection' abstraction:
- 'conj' to add an item to a collection.
- 'seq' to get the sequence interface of a collection.
- 'count' to get the number of item in a collection.
- 'empty' to get an empty instance of the collection.
- = to determine the value equality of the collection.

Each of these operations are polymorphic on the collection and maintain the
collections semantics.
"
(facts "conj prepends items to lists:"
  (fact (conj '(1 2 3) 4)
    => '(4 1 2 3))
  (fact (into '(1 2 4) [:a :b :c])
    => '(:c :b :a 1 2 4)))

"
empty - allows you to write generic collection code not knowing up front what
data type the collection is:"

(defn swap-pairs
  "swap pairs of values in a sequential collection"
  [sequential]
  (into (empty sequential)
        (interleave
         (take-nth 2 (drop 1 sequential))
         (take-nth 2 sequential))))

(fact (swap-pairs (apply list (range 10)))
  => '(8 9 6 7 4 5 2 3 0 1))

(fact (swap-pairs (apply vector (range 10)))
  => [1 0 3 2 5 4 7 6 9 8])

"swap-pairs return type is the same as it's argument

also works with 'map types:
"

(defn map-map
  "maps a given function over every item in a map"
  [fun mp]
  (into (empty mp)
        (for [[k v] mp]
          [k (fun v)])))

(fact (map-map inc (hash-map :z 5 :c 6 :a 0))
  => {:z 6, :c 7, :a 1})

(fact (map-map inc (sorted-map :z 5 :c 6 :a 0))
  => {:a 1, :c 7, :z 6})


"
'count' - indicates the number of items in the collection.
"
(fact "number of items:"
  (fact (count [1 2 3])
    => 3)
  (fact (count {:a 1 :b 2 :c 3})
    => 3)
  (fact (count #{1 2 3})
    => 3)
  (fact (count '(1 2 3))
    => 3))
"
Count also works on Java collections ans Strings."


[[:subsection {:title "Sequences --  page: 89"}]]
"
'sequence' is an abstraction that defines an interface for traversing a collection
or some computation.

They involve a few operations:
"
"
- 'seq' produces a sequence over it's argument.
- 'first', 'rest', and 'next' provide a way to consume sequences
- 'lazy-seq' produce a lazy sequence given an expression to evaluate.

'seq'able types include: Clojure and Java collections, Java maps, Java
CharSequences (including Strings), Java Iterables, Arrays, nil (null), anything
clojure.lang.Seq.

"
(facts "some examples:"
  (fact (seq "Clojure")
    => '(\C \l \o \j \u \r \e))
  (fact (seq {:a 5 :b 6})
    => '([:b 6] [:a 5]))
  (fact (seq (java.util.ArrayList. (range 5)))
    => '(0 1 2 3 4))
  (fact (seq (into-array ["Clojure" "Programming"]))
    => '("Clojure" "Programming"))
  (fact (seq [])
    => nil)
  (fact (seq nil)
    => nil))

"
Many fucntions that work on collections call 'seq' on them implicitly:"

(fact (map str "Clojure")
  => '("C" "l" "o" "j" "u" "r" "e"))
(fact (set "Programming")
  => #{\a \g \i \m \n \o \P \r})

"
The fundamental operations for traversing sequences are: 'first', 'rest' and 'next:
"
(fact (first "Clojure")
  => \C)
(fact (rest "Clojure")
  => '(\l \o \j \u \r \e))
(fact (next "Clojure")
  => '(\l \o \j \u \r \e))

"
'rest' and 'next' differ on the way that they treat sequences containing 0 or 1 values:
"
(facts "'rest' vs. 'next'"
  (fact (rest [1])
    => '())
  (fact (next [1])
    => nil)
  (fact (rest nil)
    => ())
  (fact (next nil)
    => nil))

(fact "this identity is true"
  (= (next ..any..)
     (seq (rest ..any..)))
  => truthy)

"
This makes it possible to generate sequences lazily
"
"


Sequences are not iterators:
"
(doseq [x (range 3)]
  (println x))
"
remember that the seq just created is immutable"

(let [r (range 3)
      rst (rest r)]
  (prn (map str rst))
  (prn (map #(+ 100 %) r))
  (prn (conj r -1) (conj rst 42)))
"
As the products returned are immutable, they can be safely reused."

"

Sequences are not lists:

- 'count' on a 'seq' can be costly.
- contents of a 'seq' may be calculated lazily.
- a lazy 'seq' may be infinite, therefore uncountable.
"
(comment
  (fact
    (let [s (range 1e6)]
      (time (count s)))
    ;;"Elapsed time: 529.126988 msecs"
    => 1000000)

  (fact
    (let [s (apply list (range 1e6))]
      (time (count s)))
    ;;"Elapsed time: 0.054079 msecs"
    => 1000000)
  )

"
You don't normally create 'seq's but you can with 'cons' and 'list*

'cons' takes two arguments a value that serves as the head and another collection
that serves as the tail.
"
(fact (cons 0 (range 1 5))
  => '(0 1 2 3 4))
"
'cons' always prepends to it's collection even if that is inefficient:"
(fact (cons :a [:b :c :d])
  => '(:a :b :c :d))

"
'list*' produces a sequence with any number of heads:"

(facts "these are equivalent:"
  (fact (cons 0 (cons 1 (cons 2 (cons 3 (range 4 10)))))
    => '(0 1 2 3 4 5 6 7 8 9))
  (fact (list* 0 1 2 3 (range 4 10))
    => '(0 1 2 3 4 5 6 7 8 9)))

"'cons' and 'list* are most commonly used in macros



Lazy seqs:
"
(fact "lazy seqs are easy to create:"
  (lazy-seq [1 2 3])
  => '(1 2 3))

(defn random-ints
  "Returns a lazy seq of random integers in the range [0,limit)]"
  [limit]
  (lazy-seq
   (cons (rand-int limit)
         (random-ints limit))))

(take 10 (random-ints 50))
;;  => (39 7 6 21 9 23 20 5 38 8)

"Is this list lazy? Let's check:"

(defn random-ints
  [limit]
  (lazy-seq
   (println "realizing random number")
   (cons (rand-int limit)
         (random-ints limit))))

(def rands (take 10 (random-ints 50))) ; generate a lazy sequence of 10 units.

(comment
  (first rands)
  ;; realizing random number  ; use 1
  ;; => 29

  (nth rands 3)
  ;;realizing random number
  ;;realizing random number
  ;;realizing random number
  ;;=> 30

  (count rands)
  ;;realizing random number
  ;;realizing random number
  ;;realizing random number
  ;;realizing random number
  ;;realizing random number
  ;;realizing random number  ; use all
  ;;=> 10

  (count rands)
  ;; => 10
  )

(repeatedly 10 (partial rand-int 10))
;;  => (6 4 7 4 0 3 1 8 0 3)

"
'next' can return 'nil' on an empty sequence because it realizes the next values
checking.  'rest' returns the tail maximizing laziness.
"
(comment
  (def x (next (random-ints 50)))
  ;;realizing random number
  ;;realizing random number

  (def x (rest (random-ints 50)))
  ;;realizing random number

  "destructuring uses 'next' always realizing the tails head."
  (let [[x & rest] (random-ints 50)])
  ;;realizing random number
  ;;realizing random number

  "'doall' and 'dorun' force the realization of lazy sequences"
  (dorun (take 5 (random-ints 50)))
  ;;realizing random number
  ;;realizing random number
  ;;realizing random number
  ;;realizing random number
  ;;realizing random number
  )

"
Code defining lazy sequences should minimize side effects.
"


[[:subsection {:title "Associative --  page: 99"}]]

[[:subsection {:title "Indexed --  page: 103"}]]

[[:subsection {:title "Stack --  page: 104"}]]

[[:subsection {:title "Set --  page: 105"}]]


[[:subsection {:title "Sorted --  page: 106"}]]

[[:section {:title "Concise Collection Access --  page: 111"}]]

[[:subsection {:title "Idiomatic Usage --  page: 112"}]]

[[:subsection {:title "Collections and Keys and Higher-Order Functions --  page: 113"}]]

[[:section {:title "Data Structure Types --  page: 114"}]]

[[:subsection {:title "Lists --  page: 114"}]]

[[:subsection {:title "Vectors --  page: 115"}]]

[[:subsection {:title "Sets --  page: 117"}]]

[[:subsection {:title "Maps --  page: 117"}]]

[[:section {:title "Immutability and Persistence --  page: 122"}]]

[[:subsection {:title "Persistence and Structural Sharing --  page: 123"}]]

[[:subsection {:title "Transients --  page: 130"}]]

[[:section {:title "Metadata --  page: 134"}]]

[[:section {:title "Putting Clojure’s Collections to Work --  page: 136"}]]

[[:subsection {:title "Identifiers and Cycles --  page: 137"}]]

[[:subsection {:title "Thinking Different: From Imperative to Functional --  page: 138"}]]

[[:subsection {:title "Navigation, Update, and Zippers --  page: 151"}]]

[[:section {:title "In Summary --  page: 157"}]]

(fact (+ 3 4) => 7)
