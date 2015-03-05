(ns clojure-noob.fun-prog
  (:require [midje.sweet :refer :all]))

(facts  "pure functions:"
  (fact
    (get [:chumbawumba] 0)
    => :chumbawumba)
  (fact
    (reduce + [1 10 5])
    => 16)
  (fact
    (str "wax on " "wax off")
    => "wax on wax off"))

(facts "pure functions are refentially transparent"
  (fact
    (+ 1 2)
    => 3))

(defn wisdom
  "the string ', Daniel-san' is immutable"
  [words]
  (str words ", Daniel-san"))

(fact
  (wisdom "Always bathe on Fridays")
  => "Always bathe on Fridays, Daniel-san")

;; always gives a differen result so not referntially transperent
(defn year-end-evaluation
  []
  (if (> (rand) 0.5)  ;; rand is not a function.
    "You get a raise!"
    "Better luck next year!"))

;; separate referencialy transparent functions from side-effecting procedures
(defn analysis
  "analysis is a function"
  [text]
  (str "Character count: " (count text)))

(defn analyze-file
  "but analyze-file isn't"
  [filename]
  (analysis (slurp filename)))  ;; slurp is not a function

;;(System/getProperty "user.dir")
;;(analyze-file "project.clj")
;;=> "Character count: 423"


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; living with immutable data structures
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn no-mutation
  [x]
  (println x)

  ;; let creates a new scope
  (let [x "Kafka Human"]
    (println x))

  ;; outside of the let scope
  (println x))

(fact "Kafka Human is in it's own scope"
  (no-mutation "Existential Angst Person")
  => nil)
;;Existential Angst Person
;;Kafka Human
;;Existential Angst Person


;; recursive proplem solving:
(defn sum
  ([vals] (sum vals 0))  ;; --1-- default value for accumulator
  ([vals accumulating-total]
    (if (empty? vals)    ;; --2-- empty vals is the base condition
      accumulating-total
      (sum (rest vals) (+ (first vals) accumulating-total)))))

(fact "evaluate sum"
  (sum [39 5 1])   => 45   ;; single-arity - call 2-arity
  (sum [39 5 1] 0) => 45
  (sum [5 1] 39)   => 45
  (sum [1] 44)     => 45
  (sum [] 45)      => 45   ;; base case is reached, so return the accumulator
  45               => 45 )

;; should use 'recur' for more efficient looping
(defn sum-r
  ([vals]
     (sum vals 0))
  ([vals accumulating-total]
     (if (empty? vals)
       accumulating-total
       (recur (rest vals) (+ (first vals) accumulating-total)))))

(fact (sum-r [39 5 1]) => 45 )


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; function compostion instead of attribute mutation
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(require '[clojure.string :as s])

(defn clean
  [text]
  (s/replace (s/trim text) #"lol" "LOL"))

(fact
  (clean "My boa constrictor is so sassy lol!  ")
  => "My boa constrictor is so sassy LOL!")

;; use a chain of function calls instead of progressively mutating an object


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; cool things to do with functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(fact "chaining function is pretty common style so function called 'comp'"
  ((comp s/lower-case s/trim) " Unclean string ")
  => "unclean string")

;; 'def' create a function
(def my-lower-trim-1 (comp s/lower-case s/trim))
(fact
  (my-lower-trim-1  " Unclean string ")
  => "unclean string")

;; an implementation of 'comp' that composes two functions
(defn two-comp
  [f g]
  (fn [& args]
    (f (apply g args))))

(def my-lower-trim-2 (two-comp s/lower-case s/trim))

(fact
  (my-lower-trim-2 " Unclean string  ")
  => "unclean string")

(def sum-times2 (two-comp  #(* 2 %) +))

(fact
  (sum-times2 1 2 3)
  => 12)

;; define a new version of 'comp'
(defn my-comp
  [f & funs]
  (if (empty? funs) f    ;; base case - returned composed function
      (recur (fn [& args]
                 (f (apply (first funs) args)))
               (rest funs))))

(fact "works like two-comp"
  ((my-comp s/lower-case s/trim) " Unclean string ")
  => "unclean string")

(fact "works like regular 'comp'"
  ((my-comp s/reverse s/lower-case s/trim) " Unclean string ")
  => "gnirts naelcnu")

(def sum-times2' (two-comp  #(* 2 %) +))

(fact (sum-times2' 1 2 3)
  => 12)


;;;;;;;;;;;;;;;;;;;;
;; memoization
;;;;;;;;;;;;;;;;;;;;

(defn sleepy-identity
  "returns the given value after a 1 second 'calculation'"
  [x]
  (Thread/sleep 1000)
  x)

(sleepy-identity "Mr. Fantatstico")    ;; after one second

(def memo-sleepy-identity
  "memoize does calculation once, then returns immediatly"
  (memoize sleepy-identity))

(memo-sleepy-identity "Mr. Fantatstico")  ;; after one second
(memo-sleepy-identity "Mr. Fantatstico")  ;; immediately


;; Pretty cool!


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Peg Thing - in it's own project
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
