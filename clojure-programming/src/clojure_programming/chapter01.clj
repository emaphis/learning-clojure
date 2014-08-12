(ns clojure-programming.chapter01
  (:require [midje.sweet :refer :all]))


[[:section {:title "The Clojure REPL" :tag "page 3"}]]

"
'defn' defines a new function named 'average' in the namespace.
'average' takes one  refered to as 'number'.
"
(defn average
  [numbers]
  (/ (apply + numbers) (count numbers)))

(fact (average [60 80 100 400]) => 160)


[[:section {:title "The Reader" :tag "page 12" }]]


"
The reader (read-string) returns a language AST of the string read
"
(facts "the reader 'read-str' converts strings into Clojure data structures"
  (fact (read-string "42") => 42)
  (fact (read-string "(+ 1 2)") => '(+ 1 2)))

"
The dual of  'read-string' is 'pr-str' it prints out Clojue data
strutures as strings
"
(facts "'pr-str' is the complement of 'read-str'"
  (fact (pr-str [1 2 3]) => "[1 2 3]")
  (fact (read-string "[1 2 3]") => [1 2 3]))


[[:subsection {:title "scalars" :tag "page 13" }]]

(fact "Strings are Java strings delimited by \"\""
  "hello there" => "hello there"
"multiline strings
are very handy" => "multiline strings\nare very handy")
(fact "Booleans"
  true => true
  false => false)
(fact "'nil' is nil or Java Null"
  (type nil) => nil)

(fact "Characters literals ar dented by a backslash"
  (class \c) => java.lang.Character)

(facts "Unicode and octal representations use prefixes"
  (fact "\\u00ff"
    \u00ff => \ÿ) 
  (fact "\\o41"
    \o41 => \!))

"
keywords:
"
(def person {:name "Sandra Cruz"
             :city "Portland, ME"})

(fact "keywords evaluate to themselves"
  (:city person) => "Portland, ME")

"
Keywords can act as functions  ':city' looks up the city by the key value
of the hash-map person.

keywords are always prefixed by the colon ':' and can be prefixed by
a namespace alias '::aliea/kw'.

(/) denotes a 'namespace keyword'
(::) is expanded to a namespaced keyword in the current namespace

"
(def pizza {:name "Ramunto's"
            :location "Claremont, NH"
            ::location "42.3734, -72.3365"})

(fact pizza
  => {:name "Ramunto's", :clojure-programming.chapter01/location "42.3734, -72.3365", :location "Claremont, NH"})

(fact (:clojure-programming.chapter01/location pizza)
  => "42.3734, -72.3365")

"

symbols:

'Symbols' are identifiers that evaluate to values that they name in the current name space.  

'average' was a symbol defined earlier:
"
(fact "the symbol 'average': "
  (average [40 80 100 400]) => 155)


"

regualar expressions:
Clojure treats strings prefixed with '#' as regualar expect literals.

"
(fact "class of regualar expression literal: "
  (class #"(p|h)ail") => java.util.regex.Pattern )

(fact "Clojure regexs do not need escaping backslashes "
  (re-seq #"(\d+)-(\d+)" "1-3")  => '(["1-3" "1" "3"]))
" would be (\\d+)-(\\d+) in Java"


[[:subsection {:title "Comments" :tag "page 18" }]]
"
Two kinds of comments:

Single line begin with a ';' character
all text after the ';' are ignored until the end of the line

Form level using the '#_' reader macro, This causes the reader to 
ignore the 'sexp' following the macro:
"
(fact
  (read-string "(+ 1 2 #_(* 2 2) 8)")  => '(+ 1 2 8))



[[:subsection {:title "Whitespaces and Commas" :tag "page 19" }]]
"
Commas are whitespace in Clojure.

So:"
(fact "these are equivalent"
  (defn silly-adder [x,y]
    (+, x, y))
  =>
  (defn silly-adder [x y]
    (+ x y)))

"And: "
(fact (= [1 2 3] [1, 2, 3]) => true)


[[:subsection {:title "Collection Literals" :tag "page 19.5" }]]

(facts
  (fact "the list '(...):"
    (type '(a b :name 12.5)) => clojure.lang.PersistentList)
  (fact "the vector [...]:"
    (type ['a 'b :name 12.5]) => clojure.lang.PersistentVector)
  (fact "the map {:... ...}:"
    (type {:name "Chas" :age 31}) => clojure.lang.PersistentArrayMap)
  (fact "the set #{...}:"
    (type #{1 2 3}) => clojure.lang.PersistentHashSet))



[[:subsection {:title "Miscellaneous Reader Sugar" :tag "page 20.a" }]]

"
Evaluation can be suppressed with the (') quote macro, this is equivalent 
to the (quote from)
"
(fact (quote (a b c)) => '(a b c))
"

Anonymous functions - #()
"
(fact (#(+ 3 %) 3) => 6)
"

var refences quotes:  (#'), prevents var from dereferencing
"
(def var1 10)
(fact #'var1 => #'clojure-programming.chapter01/var1)
"
derenfernce a var (@)
"
;;(fact @var1 => 10)


[[:section {:title "Namespaces" :tag "page 20.b" }]]

"
Namespaces are Clojures unit of code modularity.

A 'namespace' is fundamentally a dynamic mapping between symbols and 
either vars and Java classes.

Vars (a reference type) are mutable storage that can hold any value.

They are defined using the 'def' special form.

Never define 'vars' in a funtion body, only at the top level.
"

(def x 1)
(fact x => 1)

(def  x "hello")
(fact x => "hello")

"symbols may be qualified by a namespace.
*ns* is bound to the current namespace

the 'java.lang' namepace is imported by default into each Clojure namepace:
"
(facts
  (fact String => java.lang.String)
  (fact Integer => java.lang.Integer)
  (fact java.util.List => java.util.List)
  (fact java.net.Socket => java.net.Socket))

"namespaces also alias the vars included in Clojures standard library 'clojure.core' so it may be used without qualifications"


[[:section {:title "Symbol Evaluation" :tag "page 23" }]]

"
vars evaluate to there contents,
numbers, strings and other atomic values evaluate to themselves.
"


[[:section {:title "Special Forms" :tag "page 24.a" }]]

"
Symbols in function call position can only eval to two different things:
* The value of a named var or a local -or-
* A Clojure special form.

Clojure special forms form the basis of Clojure computation, all other 
things are build on top of special forms. 
Special forms have there own evaluation syntax
"

[[:subsection {:title "Suppressing Evaluation: quote" :tag "page 24.b" }]]

(fact (quote x) => 'x)
(fact (symbol? (quote x)) => truthy)

"Reader syntax for 'quote':  '()"
(fact 'x => 'x)

(facts "Any Clojure form can be quoted:"
  '(+ x x) => '(+ x x)
  (list? '(+ x x)) => true)

(fact ''x => '(quote x))


[[:subsection {:title "Code Blocks: do" :tag "page 25" }]]

(fact "do evaluates all of it's expessions in order and yields the last"
  (do (println "hi")
      (+ 3 4)
      (apply * [4 5 6]))
  => 120)

"The steps before the last are usually executed for there side effects.

Many forms (fn let loop try and defn) wrap there bodies in a 'do' form.
"
(let [a (inc (rand-int 6))
      b (int (rand-int 6))]
  (println (format "You rolled a %s and a %s" a b))
  (+ a b))
" 
contains an implicit 'do'
such as:"

(let [a (inc (rand-int 6))
      b (inc (rand-int 6))]
  (do
    (println (format "You rolled a %s and a %s" a b))
    (+ a b)))


[[:subsection {:title "Defining Vars: def" :tag "page 26" }]]
"
def defines or redifines a var with an optional value in the 
current namespace
"
(facts "def'ing vars"
  (def p "foo") => #'p
  p => "foo")
"
(defn defn- defprotocol defonce defmacro) all use 'def' implicityl
therefore can create or redifine vars.

(deftype defrecord defmethod) don't define or modify vars.
"


[[:subsection {:title "Local Bindings: let" :tag "page 27" }]]
"
let binds locally scoped references, let defines locals
"
(defn hypot
  [x y]
  (let [x2 (* x x)
        y2 (* y y)]
    (Math/sqrt (+ x2 y2))))

(fact (hypot 3 4) => 5.0)
"
let is implicitly used int 'fn' and 'defn' to bind parmeters in the
local scope.

All locals are immutable, but you can override local bindings.
- loop and recure are special forms that override immutability.
- reference type can be used to override immutability but have special
  semantics.

Let bindings provide destructuring at.
"

[[:subsection {:title "Destructuring - let" :tag "page 28" }]]
"
Most Clojure funtions are based around sequential and map data structures.
This allows functions and data structures to be trivially composed.
One challenge is to access$ the data in these structures.
"
(def v [42 "foo" 99.2 [5 12]])

(facts "a couple of approaches"
  (fact (first v) => 42)
  (fact (second v) => "foo")
  (fact (last v) => [5 12])
  (fact (nth v 2) => 99.2)
  (fact (v 2) => 99.2)
  (fact (.get v 2) => 99.2))
"
'first' 'second' 'last' are standard functions for accessing
sequential values.
'nth' allows you to pick any value using an index.
Vectors are functions of there indexes.
Clojure sequential collections implement the java.util.List interface (.get)

Accessing deeper structures are more complicated
"
(facts "this is complicated"
  (fact (+ (first v) (v 2)) => 141.2)
  (fact (+ (first v) (first (last v))) => 47))
"
Clojure destructuring provides a more concise syntax
Destructuring syntax of 'let' also works for (fn defn loop ...).

Destructuring comes in two forms Sequntial and Map.
"
"Sequential destructuring works with many types of collections:
  list vector seq, java.util.List (ArrayList LinkedList), 
  java arrays, Strings.
"
(fact "basic example"
  (let [[x y z] v]
    (+ x z))
  => 141.2)

(fact "this is equivalent"
  (let [x (nth v 0)
        y (nth v 1)
        z (nth v 2)]
    (+ x z))
  => 141.2)

"Destructuring can be nested"

(fact "nested destructuring"
  (let [[x _ _ [y z]] v]
    (+ x y z))
  => 59)

"
Extra-positional sequential values
& gathers up the rest of values that lay beyond the values in 
the destructuring form.
"
(fact "extra-positional"
  (let [[x & rest] v]
    rest)
  => '("foo" 99.2 [5 12]))
"
Retaining the destructured value
"
(fact "original vector"
  (let [[x _ z :as original-vector] v]
    (conj original-vector (+ x z)))
  => '[42 "foo" 99.2 [5 12] 141.2])

"
Map destructuring 
similar to sequential destructuring.
It works with hash-maps array-maps records, anything that implements
java.util.Map, anything that is supported by 'get' vectors Strings Arrays
"
"define a map to work with"
(def m {:a 5 :b 6
        :c [7 8 9]
        :d {:e 10 :f 11}
        "foo" 88
        42 false})

(fact (let [{a :a b :b} m]
        (+ a b))
  => 11)
