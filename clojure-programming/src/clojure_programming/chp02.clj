(ns clojure-programming.chp02
  (:require [midje.sweet :refer :all]))


[[:section {:title "Functional Programming and Values --  page: 52"}]]

"Functional programming:
- Preference of working with immutable data structures.
- Functions as values themselves.
- Preference for working wih declarative processing of data rather than using
  imperative control structures: do, while, for.
- Incremental composition of functions, higher order functions an imutable data
  structures to form higher level abstractions.
"
[[:subsection {:title "Importance of Values -- page: 52"}]]

"
Values:
 Most programming language incourage the use of mutable state. Functional lanugages
 including Clojure incourage the use of immutable values.

Since clojure values are immutable:
"
(facts "these are always true:"
  (fact (= 5 5) => true)
  (fact (= 5(+ 2 3)) => true)
  (fact (= "boot" (str "bo" "ot")) => true)
  (fact  (= nil nil) => true)
  (fact
    (let [a 5]
      (+ a 3)
      (= a 5))
    => true))
"
In almost all languages, numer are immutable so they can be trusted.
Strings in most languages are immutable so they can be trusted as key in hash-tables.
"

[[:subsection {:title "A Ctritical Choice -- page: 58"}]]
"
Immutable object state means that:
- Mutable objects can't be passed safely to methods.
- Mutable objects can't be reliablly used as hask keys.
- Mutable objects can't be safely cached.
- Mutable objects can't be safely used in a multi-threaded environment.

Whole classes of bug disappear when you use immutable values.

Object oriented languages have evolved various coping mechanisms:
- Copy constructors and deep copy methods.
- Many patterns for tracking an managing state: Observer, Reactor.
- Facade over mutable objects such as provided by java.util.Collections.
- Documentation and advice.
"

[[:section {:title "First-Class and Higher-Order Functions --  page: 59"}]]
"
In functional languages, functions are values: they can be passed and returned as values,
and they can be stored in data structures.

"
(defn call-twice [fnc x]
  (fnc x)
  (fnc x))

(call-twice println 123)
"
123
123
"

"
Java functions are complicated to use because they are tied to classes and objects:

public static int maxOf (int[] numbers) {
  int max = Integer.MIN_VALUE;
  for (int i : numbers) {
    max = Math.max(i, max);
  }
  return max;
}

public static void toLowerCase (List<String> strings) {
  for (ListIterator<String> iter = strings.listIterator(); iter.hasNext(); ) {
    iter.set(iter.next().toLowerCase());
  }
}

"
(require 'clojure.string)

(facts "clojures versions are easy to call separate functions"
  (fact
    (max 5 6)
    => 6)
  (fact
    (clojure.string/lower-case "Clojure")
    => "clojure"))
"
Because Clojure functions are values they may be passed or returned for other functions
called higher-order functions.
Some include map, reduce, partial, comp, complement, repeatedly.

page: 62
Map - takes a function and one or more collections returning collections with the
functions applied to each unit:
(map f [a b c]) => [(f a) (f b) (f c)]
"

(fact (map inc [1 2 3 4])
  => '(2 3 4 5))

(fact (map clojure.string/lower-case ["Java" "Imperative" "Weeping"
                                      "Clojure" "Leaning" "Peace"])
  => '("java" "imperative" "weeping" "clojure" "leaning" "peace"))

(fact (map * [1 2 3 4] [5 6 7 8])
  => '(5 12 21 32))

"
Differences between map and 'toLowercase':
- 'toLowercase' mutated it's argument.
- if 'toLowercase' returned a new collection, we would have to handle memory allocation
i  and type of collection. Map aways returns a sequence.
- We would have to worry about the imperative flow of control.

Reduce  applies a function to a collection producing a single value
"
(fact (reduce max [0 -3 10 48])
  => 48)
"similar to:"
(fact (max (max (max 0 -3) 10) 48)
  => 48)

(fact "intial seed value"
  (reduce + 50 [1 2 3 4])
  => 60)

(fact "no seed:"
  (reduce + [1 2 3 4])
  => 10)

(fact "using a collection a seed alows us to reduce to that type of collection"
  (reduce 
   (fn [m v]
     (assoc m v (* v v)))
   {}
   [1 2 3 4])
  => {4 16, 3 9, 2 4, 1 1})

(fact "using a function literal"
  (reduce
   #(assoc %1 %2 (* %2 %2))
   {}
   [1 2 3 4])
  => {4 16, 3 9, 2 4, 1 1})

"
No one would define 'maxOf' of 'toLowercase' type functions that operate over
entire collections in Clojure. It makes more sense core functions like 'max' or 
'lower-case' and use higher-order functions to apply them to collections.
"

[[:subsection {:title "Applying Ourselves Partially -- page: 65"}]]

"Function application is the invocation of a function  on a sequence of values:"

(fact "clojure function application:"
  (apply hash-map [:a 5 :b 6])
  => {:b 6, :a 5})

(def args [2 -2 10])

(fact "prefix with a number of arguments"
  (apply * 0.5 3 args)
  => -60.0)

"
'apply' applies a function to all arguments in a sequence, 'partial' only applies
some of the arguments returning a new function that applies to the rest.
"
(def only-strings (partial filter string?))

(fact "partial application"
  (only-strings ["a" 5 "b" 6])
  => '("a" "b"))

(fact "function literals are similar to partials:"
  (#(filter string? %) ["a" 5 "b" 6])
  => '("a" "b"))

(facts "not just limited to initial arguments"
  (fact (#(filter % ["a" 5 "b" 6]) number?)
    => '(5 6))
  (fact (#(filter % ["a" 5 "b" 6]) string?)
    => '("a" "b")))

(facts "But function literals require all arguments to be fully specified"
  (fact "must account for number of arguments"
    (#(map *) [1 2 3] [4 5 6] [7 8 9])
    => (throws clojure.lang.ArityException))
  (fact "enumerating arguments"
    (#(map * %1 %2 %3) [1 2 3] [4 5 6] [7 8 9])
    => '(28 80 162))
  (fact "must align wiht passed arguments"
    (#(map * %1 %2 %3) [1 2 3] [4 5 6])
    => (throws clojure.lang.ArityException))
  (fact "apply using 'rest' arguments"
    (#(apply map * %&) [1 2 3] [4 5 6] [7 8 9])
    => '(28 80 162))
  (fact "still works but duplicates what 'partial' does better"
    (#(apply map * %&) [1 2 3])
    => '(1 2 3))
  (fact "much better"
    ((partial map *) [1 2 3] [4 5 6] [7 8 9])
    => '(28 80 162)))


(comp-negated-sum-str 10 12 3.4)
(comp-negated-sum-str 10 12 3.4)
[[:section {:title "Composition of Functionality --  page: 68"}]]

"
Compositionality is the ability to build more complex things out of smaller simpler parts. Different programming languages use diffent methods of composition. Functional languages us functions.
"
(defn negated-sum-str
  "negate a sum of some given numbers"
  [& numbers]
  (str (- (apply + numbers))))

(fact (negated-sum-str 10 12 3.4)
  => "-25.4")

(def comp-negated-sum-str (comp str - +))

(fact "composed version:"
  (comp-negated-sum-str 10 12 3.4)
  => "-25.4")

" (comp f g h) acts like a pipeline passing arguments from h to g to f 
"



[[:subsection {:title "Writing Higher-Order Functions -- page: 71"}]]

[[:subsection {:title "Primative Logging System Example -- page: 72"}]]

[[:section {:title "Pure Functions --  page: 78"}]]

[[:subsection {:title "Why are pure functions Interesting -- page: 78"}]]

[[:section {:title "Real World --  page: 81"}]]
