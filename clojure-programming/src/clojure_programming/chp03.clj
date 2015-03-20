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
Clojures insistence on using abstractions over collections is similar to
 polymorphism and interface types in OO languages, but more general and more
 powerful.

"

(def v [1 2 3])

(facts "Some operations on a vector:"
  (fact "'conj' add to end"
    (conj v 4)
    => [1 2 3 4])
  (fact "... adds to end"
    (conj v 4 5)
    => [1 2 3 4 5])
  (fact "immutable"
    (seq v)
    => '(1 2 3)) )


(def m {:a 5 :b 6})

(facts "Same operations work on maps:"
  (fact
    (conj m [:c 7])
    => (contains {:c 7, :b 6, :a 5}))
  (fact "immutable"
    (seq m)
    => '([:b 6] [:a 5])) )


(def s #{1 2 3})

(facts "...and sets:"
  (fact "unsorted"
    (conj s 10)
    => #{1 3 2 10})
  (fact "unsorted"
    (conj s 3 4)
    => #{1 4 3 2})
  (fact "immutable"
    (seq s)
    => '(1 3 2)))


(facts "...and lists:"
  (def lst '(1 2 3))

  (fact "'conj' adds to front"
    (conj lst 0)
    => '(0 1 2 3))
  (fact "... adds to front"
    (conj lst 0 -1)
    => '(-1 0 1 2 3))
  (fact "immuatable"
    (seq lst)
    => '(1 2 3)))

"
'conj' and 'seq' provide a polymorphic and general interface to the collections.
'conj' adds an item to the collection in the most efficient way for that
 collection. The front of lists and the end of vectors for instance.
'Seq' provides a common view over collections. This provides a small interface
 that can be used to build other functions.

'into' is built on top of 'seq' and 'conj' so it works on the same collections
"

(facts "for example 'into':"
  (fact
    (first '((1 2 3) 4 5 6))
    => '(1 2 3))
  (fact
    (into v [4 5])
    => [1 2 3 4 5])
  (fact
    (into m [[:c 7] [:d 8]])
    => {:c 7, :b 6, :d 8, :a 5})
  (fact
    (into #{1 2} [2 3 4 5 3 3 2])
    => #{1 4 3 2 5})
  (fact
    (into [1] {:a 1 :b 2})
    => [1 [:b 2] [:a 1]]) )

"
A concrete implementation doesn't have to implement the full abstraction to be
useful, for instance the clojure collections don't implement Java's mutable
interfaces.

There seven primary abstractions that Clojures data structures implement:
- Collection
- Sequence
- Associative
- Indexed
- Stack
- Set
- Sorted
"

[[:subsection {:title "Collection Abstraction --  page: 87"}]]

"
All data structures implement the 'collection' abstraction:
- 'conj' to add an item to a collection.
- 'seq' to get the sequence interface of a collection.
- 'count' to get the number of item in a collection.
- 'empty' to get an empty instance of the collection.
- '=' to determine the value equality of the collection.

Each of these operations are polymorphic on the collection and maintain the
collections semantics.
"
"'conj' appends the item in the most efficient order."

(facts "conj prepends items to lists:"
  (fact
    (conj '(1 2 3) 4)
    => '(4 1 2 3))
  (fact "'into' is implemented in terms of 'conj'"
    (into '(1 2 4) [:a :b :c])
    => '(:c :b :a 1 2 4)) )

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

(fact "so, it works with lists."
  (swap-pairs (apply list (range 10)))
  => '(8 9 6 7 4 5 2 3 0 1) )

(fact "... and vectors."
  (swap-pairs (apply vector (range 10)))
  => [1 0 3 2 5 4 7 6 9 8] )

"swap-pairs return type is the same as it's argument because of empty

'empty' also works with 'map' types:
"

(defn map-map
  "maps a given function over every value in a map"
  [fun mp]
  (into (empty mp)
        (for [[k v] mp]   ; 'for' is a list comprehension form.
          [k (fun v)])))

(fact "maps 'inc' over the values of a map"
  (map-map inc (hash-map :z 5 :c 6 :a 0))
  => {:z 6, :c 7, :a 1})

(fact
  (map-map inc (sorted-map :z 5 :c 6 :a 0))
  => {:a 1, :c 7, :z 6})


"
'count' - indicates the number of items in the collection.
"
(fact "number of items in the collection:"
  (fact "vector"
    (count [1 2 3])
    => 3)
  (fact "map"
    (count {:a 1 :b 2 :c 3})
    => 3)
  (fact "set"
    (count #{1 2 3})
    => 3)
  (fact "list"
    (count '(1 2 3))
    => 3) )
"
Count also works on Java collections and Strings."


[[:subsection {:title "Sequences Abstraction --  page: 89"}]]
"
'sequence' is an abstraction that defines an interface for obtaining or
 traversing a sequential view of a collection or successive values of some
 computation.

They involve a few operations over the collection interface:
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
  (fact "a string"
    (seq "Clojure")
    => '(\C \l \o \j \u \r \e))
  (fact "a map"
    (seq {:a 5 :b 6})
    => '([:b 6] [:a 5]))
  (fact "a Java array list"
    (seq (java.util.ArrayList. (range 5)))
    => '(0 1 2 3 4))
  (fact "array"
    (seq (into-array ["Clojure" "Programming"]))
    => '("Clojure" "Programming"))
  (fact "the 'seq' of 'nil' or empty collection is 'nil'..."
    (seq [])
    => nil)
  (fact "..."
    (seq nil)
    => nil))

"
Many functions that work on collections call 'seq' on them implicitly:"

(fact "so we don't have to call 'seq' on a string to use it as a collection:"
  (map str "Clojure")
  => '("C" "l" "o" "j" "u" "r" "e"))
(fact
  (set "Programming")
  => #{\a \g \i \m \n \o \P \r})

"
The fundamental operations for traversing sequences are: 'first', 'rest' and
 'next:
"
(fact
  (first "Clojure")
  => \C)
(fact
  (rest "Clojure")
  => '(\l \o \j \u \r \e))
(fact
  (next "Clojure")
  => '(\l \o \j \u \r \e))

"
'rest' underlies the the implementation of variadic functions

'rest' and 'next' differ on the way that they treat sequences containing 0 or 1
 values:
"
(facts "'rest' vs. 'next'"
  (fact "'rest' of collection of one item returns empty collection"
    (rest [1])
    => '())
  (fact "'next' of collection of one item returns 'nil'"
    (next [1])
    => nil)
  (fact "'rest' empty collection is an empty collection"
    (rest nil)
    => ())
  (fact "'next' of empty collection is a 'nil; "
    (next nil)
    => nil))

"'rest' returns an empty sequence, while 'next' returns a 'nil'..."

(fact "... so, this identity is always true"
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
;;=> 0
;;=> 1
;;=> 3
"
remember that the seq just created is immutable, so the value can't be changed
like mutable stateful iterators:"

(let [r (range 3)
      rst (rest r)]   ;; the 'derivative' rst
  (prn (map str rst))
  (prn (map #(+ 100 %) r))  ;; 'r' is immutable
  (prn (conj r -1) (conj rst 42)))
;;=> ("1" "2")
;;=> (100 101 102)
;;=> (-1 - 1 2) (42 1 2)

"
As the products returned are immutable, they can be safely reused."


"
Sequences are not lists:

- 'count' on a 'seq' can be costly.
- contents of a 'seq' may be calculated lazily and realized when they are
   accessed.
- the computation producing a lazy 'seq' may be infinite, therefore uncountable.

lists track there length, so 'count' is cheap.
"
(comment
  (fact "'range' produces a lazy sequence realized at access time, counting"
    (let [s (range 1e6)]
      (time (count s)))
    ;;"Elapsed time: 529.126988 msecs"
    => 1000000)

  (fact "lists always track there length"
    (let [s (apply list (range 1e6))]
      (time (count s)))
    ;;"Elapsed time: 0.054079 msecs"
    => 1000000)
  )

"
Creating 'seqs':

You don't normally create 'seq's they are normally returned by functions but, but
 you can with 'cons' and 'list*'.

'cons' takes two arguments, a value that serves as the head and another
 collection that serves as the tail.
"
(fact
  (cons 0 (range 1 5))
  => '(0 1 2 3 4))
"
'cons' always prepends to it's collection even if that is inefficient
 unlike 'conj':"
(fact "Yuck!"
  (cons :a [:b :c :d])
  => '(:a :b :c :d))

"
'list*' produces a sequence with any number of heads:"

(facts "these are equivalent:"
  (fact
    (cons 0 (cons 1 (cons 2 (cons 3 (range 4 10)))))
    => '(0 1 2 3 4 5 6 7 8 9))
  (fact
    (list* 0 1 2 3 (range 4 10))
    => '(0 1 2 3 4 5 6 7 8 9)))

"'cons' and 'list* are most commonly used in macros, where 'seq's and 'lists'
are equivalent.

"
(fact "'list*' does not produce a list:"
  (list? (list* 0 (range 1 5)))
  => false )

"so program to abstractions instead."

"
Lazy seqs:
"
(fact "lazy seqs are easy to create using 'lazy-seq:"
  (lazy-seq [1 2 3])
  => '(1 2 3))

(defn random-ints
  "Returns a lazy seq of random integers in the range [0,limit)]"
  [limit]
  (lazy-seq
   (cons (rand-int limit)       ; return a lazy seq that's defined by 'head'
         (random-ints limit)))) ; cons onto a lazy tail.

(take 10 (random-ints 50))
;;  => (39 7 6 21 9 23 20 5 38 8)

"Is this list lazy? Let's check:
"
(defn random-ints
  [limit]
  (lazy-seq
   (println "realizing random number") ;print when value is realized
   (cons (rand-int limit)
         (random-ints limit))))

(def rands (take 10 (random-ints 50))) ; generate a lazy sequence of 10 units.

(comment
  (first rands)  ; force production of a lazy value
  ;; realizing random number  ; use 1
  ;; => 29

  (nth rands 3)
  ;;realizing random number
  ;;realizing random number
  ;;realizing random number ; use three more
  ;;=> 30

  (count rands)    ; force the rest.
  ;;realizing random number
  ;;realizing random number
  ;;realizing random number
  ;;realizing random number
  ;;realizing random number
  ;;realizing random number  ; use all
  ;;=> 10

  (count rands)  ; now does not require recomputing
  ;; => 10
  )

" 
since 'cons' and 'list*' don't force realization of a values in s 'seq' these
 functions are key to building lazy sequences.

so build a sequence with concrete values 'consed' onto a lazy tail, that 
suspends it's computation. 
"
"
a simpler solution using composition of random-int and repeatedly:
"
(repeatedly 10 (partial rand-int 10))
;;  => (6 4 7 4 0 3 1 8 0 3)

"'repeatedly' returns an infinite sequence given a function that produces values."
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

lazy sequences should be considered ephemeral methods of computation and not
a collection
"

[[:subsection {:title "Associative --  page: 99"}]]
"
The associative abstraction is used by collections that link keys to values.

It is defined by four functions:

- 'assoc' creates new associations between keys and values in a collection.
- 'dissoc' drops association for keys in the collection.
- 'get' looks up a value in a collection. 
- 'contains?' a predicate that returns whether a key is in the collection.

The most used structure is the 'map'."

(def m {:a 1, :b 2, :c 3})

(facts "'assoc' and 'dissoc' are a more natural fit then 'conj' ans 'seq'"
  (fact (get m :b)
    => 2)
  (fact (get m :d)
    => nil)
  (fact (get m :d "not-found")
    => "not-found")
  (fact (assoc m :d 4)
    => {:c 3, :b 2, :d 4, :a 1})
  (fact (dissoc m :b)
    => {:c 3, :a 1}))

(facts "'assoc' and 'dissoc can be used for mutiple entries"
  (fact
    (assoc m
      :x 4
      :y 5
      :z 6)
    => {:y 5, :z 6, :c 3, :b 2, :x 4, :a 1})
  (fact
    (dissoc m :a :c)
    => {:b 2}))


(def v [1 2 3])

(facts "'get' and 'assoc' are supported by vectors:"
  (fact (get v 1)
    => 2)
  (fact (get v 10)
    => nil)
  (fact (get v 10 "not-found")
    => "not-found")
  (fact (assoc v
          1 4
          0 -12
          2 :p)
    => [-12 4 :p]))

(fact "can add but need to remember what the new index will be:"
  (assoc v 3 10)
  => [1 2 3 10])

(facts "'get' works on sets - the key is the value"
  (fact (get #{1 2 3} 2)
    => 2)
  (fact (get #{1 2 3} 4)
    => nil)
  (fact (get #{1 2 3} 4 "not-found")
    => "not-found"))


(fact "sets can be used like a conditional\"\""
  (when (get #{1 2 3} 2)
    "it contains '2'!")
  => "it contains '2'!")

(facts "'contains?' is a predicate that returns 'true' if a collection contains a key"
  (fact (contains? [1 2 3] 0)
    => true)
  (fact (contains? {:a 5 :b 6} :b)
    => true)
  (fact (contains? {:a 5 :b 6} 42)
    => false)
  (fact (contains? #{1 2 3} 1)
    => true))

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
