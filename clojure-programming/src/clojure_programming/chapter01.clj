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


(facts "Keynames don't have to match"
  (fact
    (let [{f "foo"} m]
      (+ f 12))
    => 100)
  (fact
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
(fact "default values:"
  (let [{k :unknown x :a
         :or {k 50}} m]
    (+ k x))
  => 55)

"manually setting defualits is"
(fact "more tiring:"
  (let [{k :unknown x :a} m
        k (or k 50)]
    (+ k x))
  => 55)

(fact ":or knows the differnce between no value and 'false' value"
  (let [{opt1 :option} {:option false}
        opt1 (or opt1 true)
        {opt2 :option :or {opt2 true}} {:option false}]
    {:opt1 opt1 :opt2 opt2})
  => {:opt1 true, :opt2 false})


"Binding values to there key's names  page 34"

(def chas {:name "Chas" :age 31 :location "Massachesetts"})

(fact "binding values using the same names can get repetittive:"
  (let [{name :name age :age location :location} chas]
    (format "%s is %s years old and lives in %s." name age location))
  => "Chas is 31 years old and lives in Massachesetts.")

(fact "using the ':keys' option:"
  (let [{:keys [name age location]} chas]
    (format "%s is %s years old and lives in %s." name age location))
  => "Chas is 31 years old and lives in Massachesetts.")

"switch when we know we are using strings or symbols as keys"

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

"Functions are first class so Clojure can create annonymous
functions as a data type"

(fn [x]
  (+ 10 x))

"The parameters are passed in a 'let' style vector that can be destructured
 The body is inserted into an implicit 'do' form that can contain any
 number of forms"

(fact "arguments are matched to each name or destructuring form based on position"
  ((fn [x] (+ 10 x)) 8)
  => 18)

(fact "these are equivalent:"
  ((fn [x] (+ 10 x)) 8)
  =>
  (let [x 8]
    (+ 10 x)))

(fact "multiple arguments"
  ((fn [x y z] (+ x y z)) 3 4 12)
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

(fact (strange-adder 10)
  => 11)

(fact (strange-adder 10 50)
  => 60)

"notice that the single arity version references it self so that it
 can call the two arity version to do its work."


(fact "Mutally recursive funtions with letfn:"
  (letfn [(odd? [n]
            (even? (dec n)))
          (even? [n]
            (or (zero? n)
                (odd? (dec n))))]
    (odd? 11))
  => true)


"
'defn' builds on 'fn'  page: 37"

(fact "these are equivalent:"
  (def strange-adder (fn strange-adder
                       ([x] (strange-adder x 1))
                       ))
  =>
  (defn strange-adder
    ([x] (strange-adder x 1))
    ([x y] (+ x y))))

"single arity eliminates extra parantheses:"

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

(fact (concat-rest 0 1 2 3 4)
  => "123")


(defn make-user
  [& [user-id]]
  {:user-id (or user-id
                (str (java.util.UUID/randomUUID)))})

(make-user)
;; => {:user-id "85a7db56-c7c2-4fc6-942d-bfc7ba6f840c"}

(fact (make-user "Bobby")
  => {:user-id "Bobby"})


"Keyword arguments"

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
Function literals also have rest agruments (%&):"

(fn [x & rest]
  (- x (apply + rest)))

" is equivalent to:
"
#(- % (apply + %&))


[[:subsection {:title "Conditionals: if  --  page: "}]]

"
If is Clojures sole primative conditionsal operator.
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
cond - similar to else-if alows testing of multiple conditions.

if-let and when-let - compositions of 'if' and 'when' and 'let'

Clojure provides 'true?' and 'false?' predicates but they are unrelated to
to the conditionals:
"
(fact (true? "string")
  => false)

(fact (if "string" \t \t)
  => \t)


[[:subsection {:title "Looping: loop recure -- page: 43"}]]

"
Several useful imperative looping constructs (doseq and dotimes) are based on recure
'recure' transferes controle without consuming stack space.
"


(fact "a very simple coundown loop:"
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

Because 'recur' doesn't use stack space it is preferable to use inplace of natural recursion. It also alows you to do numerics without using boxed representations.

"


[[:subsection {:title "Reffering to Vars: var -- page:  page: 44"}]]

"Sybols name a var evaluate to that vars value:"

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
All Java interop (intantiation, static, instance method invocations) is based on 'new' and '.' special forms.
Since Clojure provides reader sugar it't rare to see 'new' and '.' used.

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

- Dyanmic Scope: page: 210
- Accessing object fields: page: 359
- deftype: page: 277
"

[[:subsection {:title "Primative Locking: monitor-enter, monitor-exit -- page: 45"}]]
"Primatives for Java Object locking:  page: 225"


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
encapsulate evauation semantics
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

"Example 1-4:  a reimplented Clojure REPL:"

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
