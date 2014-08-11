(ns clojure-programming.chapter01
  (:require [midje.sweet :refer :all]))


[[:section {:title "The Clojure REPL" :tag "page 3"}]]

"
'defn' defines a new function named 'average' in the namespace.
'average' takes one arguement refered to as 'number'.
"
(defn average
  [numbers]
  (/ (apply + numbers) (count numbers)))

(fact (average [60 80 100 400]) => 160)


[[:section {:title "The Reader" :tag "page 12" }]]

"
The reader (read-string) returns a language AST of the string read
"
(facts "the reader 'resd-str' converts strings into Clojure data structures"
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

(fact "Strings are Jave strings delimited by \"\""
  "hello there" => "hello there"
"multiline strings
are very handy" => "multiline strings\nare very handy")
(fact "Booleans"
  true => true
  false => false)
(fact "'nil' is nil or Java Null"
  (type nil) = nil)



