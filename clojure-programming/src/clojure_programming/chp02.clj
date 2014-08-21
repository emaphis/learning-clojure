(ns clojure-programming.chp02
  (:require [midje.sweet :refer :all]))


[[:section {:title "Functional Programming and Values --  page: 52"}]]

"Fuctional programming:
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

(facts "clojures versions are easy to call separate fucntions"
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
Differnces between map and 'toLowercase':
- 'toLowercase' mutated it's argument.
- if 'toLowercase' returned a new collection, we would have to handle memory allocation
  and type of collection. Map aways returns a sequence.
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


[[:subsection {:title "Applying Ourselves Partially -- page: 65"}]]


[[:section {:title "Composition of Functionality --  page: 68"}]]

[[:subsection {:title "Writing Higher-Order Functions -- page: 71"}]]

[[:subsection {:title "Primative Logging System Example -- page: 72"}]]

[[:section {:title "Pure Functions --  page: 78"}]]

[[:subsection {:title "Why are pure functions Interesting -- page: 78"}]]

[[:section {:title "Real World --  page: 81"}]]
