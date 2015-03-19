(ns clojure-programming.chapter01
  (:require [midje.sweet :refer :all]))


[[:section {:title "The Clojure REPL  -- page: 3"}]]


"
'defn' defines a new function named 'average' in the namespace.
'average' takes one  referred to as 'number'.
"
(defn average
  [numbers]
  (/ (apply + numbers) (count numbers)))

(fact (average [60 80 100 400]) => 160)


[[:section {:title "The Reader -- page: 12"}]]


"
 reader (read-string) returns a language AST of the string read
"
(facts "the reader 'read-str' converts strings into Clojure data structures"
  (fact (read-string "42") => 42)
  (fact (read-string "(+ 1 2)") => '(+ 1 2)))

"
The dual of  'read-string' is 'pr-str' it prints out Clojure data
strutures as strings
"
(facts "'pr-str' is the complement of 'read-str'"
  (fact (pr-str [1 2 3]) => "[1 2 3]")
  (fact (read-string "[1 2 3]") => [1 2 3])
  (fact (pr-str (read-string "[1 2 3]")) => "[1 2 3]"))


[[:subsection {:title "scalars --  page: 13"}]]

(fact "Strings are Java strings delimited by \"\""
  "hello there" => "hello there"
"multiline strings
Are very handy" => "multiline strings\nAre very handy")

(fact "Booleans 'true' and 'false are used to denote literal truth values"
  true => true
  false => false)

(fact "'nil' is nil or Java Null"
      (type nil) => nil)


(fact "Characters literals are indicated by a backslash"
  (class \c) => java.lang.Character)

(facts "Unicode and octal representations use prefixes"
  (fact "\\u00ff"
    \u00ff => \�)
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

(fact
  pizza
  => {:name "Ramunto's", :clojure-programming.chapter01/location "42.3734, -72.3365", :location "Claremont, NH"})

(fact "namespace denoted by '/'"
  (:clojure-programming.chapter01/location pizza)
  => "42.3734, -72.3365")

(facts "namespaces and keywords"
  (fact
    (name :clojure-programming.chapter01/location)
    => "location")
  (fact
    (namespace :clojure-programming.chapter01/location)
    => "clojure-programming.chapter01")
  (fact
    (namespace :locations)
    => nil))

"

symbols:

'Symbols' are identifiers that evaluate to values that they name in the current name space.

'average' was a symbol defined earlier:
"
(fact "the symbol 'average': "
  (average [40 80 100 400]) => 155)


"
page 17
regular expressions:
Clojure treats strings prefixed with '#' as regular expect literals.

"
(fact "class of regular expression literal: "
  (class #"(p|h)ail") => java.util.regex.Pattern )

(fact
  (re-seq #"(...) (...)" "foo bar")
  => '(["foo bar" "foo" "bar"]))

(fact "Clojure regexp do not need escaping backslashes "
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



[[:subsection {:title "Whitespaces and Commas" }]]
"
page: 18
Commas are whitespace in Clojure.

So:"
(fact "these are equivalent"
  (defn silly-adder
    [x,y]
    (+, x, y))
  =>
  (defn silly-adder
    [x y]
    (+ x y)))

"And: "
(fact "slightly pedantic:"
  (= [1 2 3] [1, 2, 3])
  => true)


[[:subsection {:title "Collection Literals-- page: 19" }]]

(facts
  (fact "the list '(...):"
    (type '(a b :name 12.5)) => clojure.lang.PersistentList)
  (fact "the vector [...]:"
    (type ['a 'b :name 12.5]) => clojure.lang.PersistentVector)
  (fact "the map {:... ...}:"
    (type {:name "Chas" :age 31}) => clojure.lang.PersistentArrayMap)
  (fact "the set #{...}:"
    (type #{1 2 3}) => clojure.lang.PersistentHashSet))



[[:subsection {:title "Miscellaneous Reader Sugar-- page: 20" }]]

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

var references quotes:  (#'), prevents var from dereferencing
"
(def var1 10)
(fact #'var1 => #'clojure-programming.chapter01/var1)

;(fact "dereference a var (@)"
;  @var1 => 10)


[[:section {:title "Namespaces-- page: 20" }]]

"
Namespaces are Clojures unit of code modularity.

A 'namespace' is fundamentally a dynamic mapping between symbols and
either vars and Java classes.

Vars (a reference type) are mutable storage that can hold any value.

They are defined using the 'def' special form.
"
(fact
  (def x 1)
  => #'clojure-programming.chapter01/x)

(fact "We can access the var's value using that symbol"
  x
  => 1)

(facts "We can redefine vars"
  (def x "hello")  => #'clojure-programming.chapter01/x
  x => "hello")

"Symbols may be namespace qualified"

;"*ns*  ;=> #<Namespace clojure-programming.chapter01>"


;"(fact (ns foo) => nil)"

;"*ns* ;=> #<Namespace foo>"

;(fact clojure-programming.chapter01/x
; => "hello")

;(ns clojure-programming.chapter01)

"

Never define 'vars' in a function body, only at the top level.
"

(facts "Any symbol that names a class evaluates to that class"
  (fact String => java.lang.String)
  (fact Integer => java.lang.Integer)
  (fact java.util.List => java.util.List)
  (fact java.net.Socket => java.net.Socket))

"namespaces also alias the vars included in Clojures standard library 'clojure.core' so it may be used without qualifications
"


[[:section {:title "Symbol Evaluation  -- page: 23"}]]

"
vars evaluate to there contents,
numbers, strings and other atomic values evaluate to themselves.
"


[[:section {:title "Special Forms -- page: 24"}]]

"
Symbols in function call position can only eval to two different things:
* The value of a named var or a local -or-
* A Clojure special form.

Clojure special forms form the basis of Clojure computation, all other
things are build on top of special forms.
Special forms have there own evaluation syntax
"

[[:subsection {:title "Suppressing Evaluation: quote  -- page: 24"}]]

(fact (quote x) => 'x)
(fact (symbol? (quote x)) => truthy)

"Reader syntax for 'quote':  '()"
(fact 'x => 'x)

(facts "Any Clojure form can be quoted:"
  '(+ x x) => '(+ x x)
  (list? '(+ x x)) => true)

(fact (list '+ 'x 'x) => '(+ x x))

(fact "peek at what the reader produces by quoting a form."
  ''x => '(quote x))

;TODO: fix this
(facts "use this for other reader sugars"
  (fact '@x => '(clojure.core/deref x)))

;; '#(+ % %)
;;=> (fn* [p1__9908127#] (+ p1__9908127# p1__9908127#))

;; '`(a b ~c)



[[:subsection {:title "Code Blocks: do  -- page: 25"}]]

(fact "do evaluates all of it's expressions in order and yields the last"
  (do (println "hi")
      (+ 3 4)
      (apply * [4 5 6]))
  ;; hi
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


[[:subsection {:title "Defining Vars: def  --  page 26"}]]
"
def defines or redefines a var with an optional value in the
current namespace
"
(facts "def'ing vars"
  (def p "foo") => #'p
  p => "foo")
"
(defn defn- defprotocol defonce defmacro) all use 'def' implicitly
therefore can create or redefine vars.

(deftype defrecord defmethod) don't define or modify vars.
"


[[:subsection {:title "Local Bindings: let --  page 27"}]]
"
let binds locally scoped references, let defines locals
"
(defn hypot
  [x y]
  (let [x2 (* x x)
        y2 (* y y)]
    (Math/sqrt (+ x2 y2))))

(fact "use 'hypot':"
  (hypot 3.0 4.0)
  => 5.0)
"
let is implicitly used int 'fn' and 'defn' to bind parameters in the
local scope.

All locals are immutable, but you can override local bindings.
- loop and recurse are special forms that override immutability.
- reference type can be used to override immutability but have special
  semantics.

Let bindings provide destructuring at.
"

[[:subsection {:title "Destructuring - (let, part 2) --  page 28"}]]
"
Most Clojure functions are based around sequential and map data structures.
This allows functions and data structures to be trivially composed.
One challenge is to access$ the data in these structures.
"
(def v [42 "foo" 99.2 [5 12]])

(facts "a couple of approaches to accessing 'v' values:"
  (fact (first v) => 42)     ; convenience functions 'first' 'second' 'last'
  (fact (second v) => "foo")
  (fact (last v) => [5 12])  ; pluck value using and index.
  (fact (nth v 2) => 99.2)
  (fact (v 2) => 99.2)       ; vectors a functions of their indices
  (fact (.get v 2) => 99.2)) ; clojure data structures implement java.util.List
"
'first' 'second' 'last' are standard functions for accessing
sequential values.
'nth' allows you to pick any value using an index.
Vectors are functions of there indexes.
Clojure sequential collections implement the java.util.List interface (.get)
"

"
Accessing deeper structures are more complicated
"
(facts "this is complicated"
  (fact
    (+ (first v) (v 2))
    => 141.2)
  (fact "if we need to access values in nested collections:"
    (+ (first v) (first (last v)))
    => 47))
"
Clojure destructuring provides a more concise syntax
Destructuring syntax of 'let' also works for (fn defn loop ...).

Destructuring comes in two forms Sequential and Map.
"
"Sequential destructuring works with many types of collections:
  list vector seq, java.util.List (ArrayList LinkedList),
  java arrays, Strings.
"
(def v [42 "foo" 99.2 [5 12]])

(fact "basic sequential destructuring example"
  (let [[x y z] v]
    (+ x z))
  => 141.2)

(fact "this is equivalent using locals"
  (let [x (nth v 0)
        y (nth v 1)
        z (nth v 2)]
    (+ x z))
  => 141.2)

"Destructuring can be nested"

(fact "nested destructuring on a nested vector"
  (let [[x _ _ [y z]] v]
    (+ x y z))
  => 59)

"
Extra-positional sequential values
& gathers up the rest of values that lay beyond the values in
the destructuring form.
"
(fact "extra-positional using &"
  (let [[x & rest] v]
    rest)
  => '("foo" 99.2 [5 12]))
"
Retaining the destructured value using ':as'
"
(fact "original vector"
  (let [[x _ z :as original-vector] v]
    (conj original-vector (+ x z)))
  => '[42 "foo" 99.2 [5 12] 141.2])

"
Map destructuring  -- page: 32
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

(fact "basic map destructuring"

  (let [{a :a b :b} m]
        (+ a b))
  => 11)

(facts "Key used for destructuring don't have to be keywords:"

  (fact "a string:"
    (let [{f "foo"} m]
      (+ f 12))
    => 100)

  (fact "a number:"
    (let [{v 42} m]
      (if v 1 0))
    => 0))

(fact "Matrix destructuring"
  (let [{x 3, y 8} [12 0 0 -18 44 6 0 0 1]]
    (+ x y))
  => -17)

(fact  "Map entries may also be composed:"
  (let [{{e :e} :d} m]
    (* 2 e))
  => 20)

(fact "Map and sequence destructuring:"
  (let [{[x _ y] :c} m]
    (+ x y))
  => 16)

(fact 
  (let [{f "foo"} m]
    (+ f 12))  => 100)

(fact
  (let [{v 42} m]
    (if v 1 0)) => 0)
 
(fact "Matrix destructuring"
  (let [{x 3, y 8} [12 0 0 -18 44 6 0 0 1]]
    (+ x y)) => -17)

(fact  "Map entries may also be composed:"
  (let [{{e :e} :d} m]
    (* 2 e)) => 20)

(fact "Map and sequence destructuring:"
  (let [{[x _ y] :c} m]
    (+ x y)) => 16)


;; TODO: mistake on page 32-33
(def map-in-vector ["James" {:birthday (java.util.Date. 73 1 6)}])

(fact "map in vector"
  (let [[name {bd :birthday}] map-in-vector]
    (str name " was born on " bd))
  => "James was born on Tue Feb 06 00:00:00 EST 1973")


"Retaining the destructured value
 ':as retains the sourced collection"
(let [{r1 :x r2 :y :as randoms}
      (zipmap [:x :y :z] (repeatedly (partial rand-int 10)))]
  (assoc randoms :sum (+ r1 r2)))
"=> {:sum 17, :z 3, :y 8, :x 9}"

" Use ':or' to provide default values for destructuring"
(fact "default values using :or"
  (let [{k :unknown x :a
         :or {k 50}} m]
    (+ k x))
  => 55)

"manually setting defaults is"
(fact "more tiring:"
  (let [{k :unknown x :a} m
        k (or k 50)]
    (+ k x))
  => 55)

(fact ":or knows the difference between no value and 'false' value"
  (let [{opt1 :option} {:option false}
        opt1 (or opt1 true)
        {opt2 :option :or {opt2 true}} {:option false}]
    {:opt1 opt1 :opt2 opt2})
  => {:opt1 true, :opt2 false})


"Binding values to there key's names  page 34"

(def chas {:name "Chas" :age 31 :location "Massachusetts"})

(fact "binding values using the same names can get repetitive:"
  (let [{name :name age :age location :location} chas]
    (format "%s is %s years old and lives in %s." name age location))
  => "Chas is 31 years old and lives in Massachusetts.")

(fact "using the ':keys' option for keywords:"
  (let [{:keys [name age location]} chas]
    (format "%s is %s years old and lives in %s." name age location))
  => "Chas is 31 years old and lives in Massachusetts.")

"switch to :strs or :syms when we know we are using strings or symbols as keys"

(def brian {"name" "Brian" "age" 31 "location" "British Columbia"})

(fact "using the ':strs' option:"
  (let [{:strs [name age location]} brian]
    (format "%s is %s years old and lives in %s." name age location))
  => "Brian is 31 years old and lives in British Columbia.")

(def christophe {'name "Christophe" 'age 33 'location "Rhône-Alpes"})

(fact "the ':syms option:"
  (let [{:syms [name age location]} christophe]
    (format "%s is %s years old and lives in %s." name age location))
  => "Christophe is 33 years old and lives in Rhône-Alpes.")


"Destructuring rest sequences s map key/value pairs  page: 35"

(def user-info ["robert8990" 2011 :name "Bob" :city "Boston"])

(fact "the manual approach is tolerable"
  (let [[username account-year & extra-info] user-info
        {:keys [name city]} (apply hash-map extra-info)]
    (format "%s is in %s" name city))
  => "Bob is in Boston")

(fact "use map destructured of rest seqs"
  (let [[username account-year & {:keys [name city]}] user-info]
    (format "%s is in %s" name city))
  => "Bob is in Boston")



[[:subsection {:title "Creating Functions:fn -- page: 36"}]]

"
Functions are first class so Clojure can create anonymous
functions as a data type
"

(fn [x]
  (+ 10 x))

"The parameters are passed in a 'let' style vector that can be destructured
 The body is inserted into an implicit 'do' form that can contain any
 number of forms"

(fact "arguments are matched to each name or destructuring form based on position"
  ((fn [x] (+ 10 x)) 8)
  => 18)

(fact "these are equivalent:"
  ((fn [x] (+ 10 x)) 8) ; 8 is bound to 'x'
  =>
  (let [x 8]
    (+ 10 x)))

(fact "function with multiple arguments"
  ((fn [x y z] (+ x y z))
   3 4 12)
  => 19)

(fact "is equivalent to this 'let' form:"
  (let [x 3
        y 4
        z 12]
    (+ x y z))
  => 19)


"multiple arities:   page 37"

(def strange-adder (fn adder-self-reference
                     ([x] (adder-self-reference x 1))
                     ([x y] (+ x y))))

(fact
  (strange-adder 10)
  => 11)

(fact
  (strange-adder 10 50)
  => 60)

" each arity and body must be enclosed in it's own parentheses

notice that the single arity version references it self so that it
can call the two arity version to do its work."

(fact "Mutually recursive functions with letfn:"
  (letfn [(odd? [n]
            (even? (dec n)))
          (even? [n]
            (or (zero? n)
                (odd? (dec n))))]
    (odd? 11))
  => true)
"
the vector consists of several 'fn' bodies with the 'fn' symbol missing
"

"
'defn' builds on 'fn'  page: 37"
"combines 'fn' and 'def' into one macro"

(fact "these are equivalent:"
  (def strange-adder (fn strange-adder
                       ([x] (strange-adder x 1))
                       ([x y] (+ x y))))
  =>
  (defn strange-adder
    ([x] (strange-adder x 1))
    ([x y] (+ x y))) )

"single arity eliminates extra parentheses:"

(fact "these are also equivalent:"
  (def redundant-adder (fn redundant-adder
                         [x y z]
                         (+ x y z)))
  =>
  (defn redundant-adder
    [x y z]
    (+ x y z)))


"
Destructuring function arguments:   page: 38

Variadic functions:  functions can gather additional arguments
into a seq:
"

(defn concat-rest
  "ignore first argument"
  [x & rest]
  (apply str (butlast rest)))

(fact
  (concat-rest 0 1 2 3 4)
  => "123")


"seq formed for the rest arguments can be destructured just like any other sequence
"
(defn make-user
  [& [user-id]]
  {:user-id (or user-id
                (str (java.util.UUID/randomUUID)))})

(make-user)
;; => {:user-id "85a7db56-c7c2-4fc6-942d-bfc7ba6f840c"}

(fact (make-user "Bobby")
  => {:user-id "Bobby"})


"Keyword arguments or defining functions that can take optional augments

Built on top of the 'map destructuring of rest sequences' idiom.
"

(defn  make-usr
  [username & {:keys [email join-date]
               :or {join-date (java.util.Date.)}}]
  {:username username
   :join-date join-date
   :email email
   ;; 2.592e9 -> one month in ms.
   :exp-date (java.util.Date. (long (+ 2.592e9 (.getTime join-date))))})

(make-usr "Bobby")
;=> {:username "Bobby", :join-date #inst "2014-08-19T18:32:26.929-00:00", :email nil, :exp-date #inst "2014-09-18T18:32:26.929-00:00"}

(fact
  (make-usr "Bobby"
            :join-date (java.util.Date. 111 0 1)
            :email "bobby@example.com")
  => {:username "Bobby",
      :join-date #inst "2011-01-01T05:00:00.000-00:00",
      :email "bobby@example.com",
      :exp-date #inst "2011-01-31T05:00:00.000-00:00"})

"destructuring the rest argument map using types other than keywords:"
(defn foo
  [& {k ["m" 9]}]
  (inc k))

(fact "destructuring using a string:"
  (foo ["m" 9] 19)
  => 20)

"
Function literals:    page: 40"

"both are equivalent:"
(fn [x y] (Math/pow x y))

#(Math/pow %1 %2)

"This is just reader sugar for the former"
(read-string "#(Math/pow $1 %2)")
" => (fn* [p1__15465# p2__15464#] (Math/pow $1 p2__15464#))"

"Function literals don't have an implicit 'do' form.
"
(fn [x y]
  (println (str x \^ y))'\
  (Math/pow x y))

" is equivalent to:
"
#(do (println (str %1 \^ %2))
     (Math/pow %1 %2))

"
Function literals also have rest arguments (%&):"

(fn [x & rest]
  (- x (apply + rest)))

" is equivalent to:
"
#(- % (apply + %&))


[[:subsection {:title "Conditionals: if  --  page: "}]]

"
If is Clojures sole primitive conditional operator.
"
(facts "Conditionals determine logical truth to be any thing other than 'nil' or 'false"
  (fact (if "hi" \t)
    => \t)
  (fact (if 42 \t)
    => \t )
  (fact nil "unevaluated" \f
    => \f)
  (fact false "unevaluated" \f
    => \f)
  (fact (if (not true) \t)
    =>  nil))

"If a condition is false and no else expression is provided
 the result will be 'nil'.

Other refinements of the if conditional:

when - if 'nil' should be returned or no other action should be taken with
       a false conditional.
cond - similar to else-if allows testing of multiple conditions.

if-let and when-let - compositions of 'if' and 'when' and 'let'

Clojure provides 'true?' and 'false?' predicates but they are unrelated to
to the conditionals:
"
(fact (true? "string")
  => false)

(fact (if "string" \t \t)
  => \t)


[[:subsection {:title "Looping: loop recur -- page: 43"}]]

"
Several useful imperative looping constructs (doseq and dotimes) are based on recur
'recur' transfers control without consuming stack space.
"


(fact "a very simple countdown loop:"
  (loop [x 5]
    (if (neg? x)
      x
      (recur (dec x))))
  => -1)

(defn countdown
  [x]
  (if (zero? x)
    :blastoff!
    (do (println x)
        (recur (dec x)))))

(fact (countdown 5) => :blastoff!)

"
Prefer the higher level 'doseq' or 'dotimes' to using 'recur'.
When 'iterating' over a collection use the functionals 'map', 'reduce' or 'for'

Because 'recur' doesn't use stack space it is preferable to use in place of natural recursion. It also allows you to do numerics without using boxed representations.

"


[[:subsection {:title "Referring to Vars: var -- page:  page: 44"}]]

"Symbols name a var evaluate to that vars value:"

(def x 5)

(fact x
  => 5)

(fact "reference to a 'var' itself"
  (var x)
  => #'clojure-programming.chapter01/x)

(fact "reader syntax shorthand for var reference"
  #'x
  => #'clojure-programming.chapter01/x)



[[:subsection {:title "Java Interop: . and new  -- page: 44"}]]

"
All Java interop (instantiation, static, instance method invocations) is based on 'new' and '.' special forms.
Since Clojure provides reader sugar it's rare to see 'new' and '.' used.

"

"Object instantiation:"
(facts "Java: new java.util.ArrayList(100)"
  (fact  "sugared interop form"
    (java.util.ArrayList. 100)
    => [])
  (fact "equivalent special form"
    (new java.util.ArrayList 100)
    => []))

"Static method invocation"
(facts "Java: Math.pow(2, 10)"
  (fact "sugared interop form"
    (Math/pow 2 10)
    => 1024.0)
  (fact "equivalent special form"
    (. Math pow 2 10)
    => 1024.0))

"Instance method invocation"
(facts "Java: 'hello'.substring(1, 3)"
  (fact "sugared interop form"
    (.substring "hello" 1 3)
    => "el")
  (fact "equivalent special form"
    (. "hello" substring 1 3)
    => "el"))

"Static field access:"
(facts "Java Integer.MAX_VALUE"
  (fact "sugared interop form"
    Integer/MAX_VALUE
    => 2147483647)
  (fact "equivalent special form"
    (. Integer MAX_VALUE)
    => 2147483647))
"
 Instance field access
  someObject.someField       ; Java code
  (.someField some-object)   ; sugared interop form
  (. some-object some-field) ; equivalent special form
"


[[:subsection {:title "Exception Handling: try, throw -- page: 44"}]]

"see page: 362"


[[:subsection {:title "Special Mutation: set! -- page: 44"}]]

"Exception to Clojure using immutable data.  Useful for Java interop.

- Dynamic Scope: page: 210
- Accessing object fields: page: 359
- deftype: page: 277
"

[[:subsection {:title "Primitive Locking: monitor-enter, monitor-exit -- page: 45"}]]
"Primitives for Java Object locking:  page: 225"


[[:section {:title "Putting If All Together --  page:  46"}]]

(defn average
  "Average a list of numbers"
  [numbers]
  (/ (apply + numbers) (count numbers)))

"
This structure is simply a List data structure that may be evaluated to produce a result.

defn is shorthand for:
"
(def average
  (fn average
    [numbers]
    (/ (apply + numbers) (count numbers))))

"fn creates a function data type and def assigns it to the average var"

[[:subsection {:title "Eval -- page: 46"}]]

"
encapsulate evaluation semantics
"
(facts "evaluation of literals:"
  (fact (eval :foo)
    => :foo)
  (fact (eval [1 2 3])
    => [1 2 3])
  (fact (eval "text")
    => "text"))

(fact "a list evaluates to the return value of the call it describes"
  (eval '(average [60 80 100 400]))
  => 160)

(fact "one step further- read-string:"
  (eval (read-string "(average [60 80 100 400])"))
  => 160)

"Example 1-4:  a reimplemented Clojure REPL:"

(defn embedded-repl
  "A naive Clojure REPL implementation. Enter ':quit' to exit."
  []
  (print (str (ns-name *ns*) ">>> "))
  (flush)
  (let [expr (read)
        value (eval expr)]
    (when (not= :quit value)
      (println value)
      (recur))))

(comment
  (embedded-repl)
  )
