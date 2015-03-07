(ns clojure-noob.macros
  (:require [midje.sweet :refer :all]))


(fact "'when' is implemented in terms of 'if' and 'do':"
  (macroexpand '(when boolean-expression
                expression-1
                expression-2
                expression-3))
 =>
 '(if boolean-expression
    (do expression-1
        expression-2
        expression-3)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; anatomy of a macro
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmacro postfix-notation
  "I'm too indie for prefix notation"
  [expression]
  (conj (butlast expression) (last expression)))

(fact "You call macros just like you would a function or special form:"

  (postfix-notation (1 1 +))
  => 2)

(fact "use conj, butlast, and last functions to rearrange the list so that
       it's something Clojure can evaluate:"

  (macroexpand '(postfix-notation (1 1 +)))
  => '(+ 1 1))

;; you can use argument destructuring in macro definitions argument
;; destructuring, just like you can with functions:

(defmacro code-critic
  "phrases are courtesy Hermes Conrad from Futurama"
  [{:keys [good bad]}]
  (list 'do
        (list 'println
              "Great squid of Madrid, this is bad code:"
              (list 'quote bad))
        (list 'println
              "Sweet gorilla of Manila, this is good code:"
              (list 'quote good))))

(fact

  (code-critic {:good (+ 1 1) :bad (1 + 1)})
;;Great squid of Madrid, this is bad code: (1 + 1)
;;Sweet gorilla of Manila, this is good code: (+ 1 1)  
  => nil)

(fact  "lets look at the output of the macro"
  
  (macroexpand '(code-critic {:good (+ 1 1) :bad (1 + 1)}))
  =>
  '(do (println "Great squid of Madrid, this is bad code:" (quote (1 + 1))) (println "Sweet gorilla of Manila, this is good code:" (quote (+ 1 1)))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; building lists for evaluation

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; be careful about distinguishing symbols and values
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; the postfix-notation example

(defmacro postfix-notation
  [expression]
  (conj (butlast expression) (last expression)))

(fact

  (macroexpand '(postfix-notation (1 1 +)))
  => '(+ 1 1)

  (postfix-notation (1 1 +))
  => 2)


;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; simple quoting
;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; a brief refresher on quoting:

(fact "No quoting"

  (+ 1 2)
  => 3)

(fact "Quoting returns unevaluated data structure
       + is a symbol in the returned list"

  (quote (+ 1 2))
  => '(+ 1 2)) 

;; Evaluating the plus symbol yields the plus function
+
;; => #<core$_PLUS_ clojure.core$_PLUS_@47b36583>

(fact "Quoting the plus symbol yields the plus symbol"

  (quote +)
  => '+)

;(fact "Evaluating an unbound symbol raises an exception"
;  sweating-to-the-oldies
;  => (throws Exception))
;"Unable to resolve symbol: sweating-to-the-oldies in this context"


(fact "quoting returns a symbol regardless of whether the symbol
       has a value associated with it"

  (quote sweating-to-the-oldies)
  => 'sweating-to-the-oldies)

(fact "The single quote character is a shorthand for (quote x)
        This example works just like (quote (+ 1 2))"

  '(+ 1 2)
  => '(+ 1 2))

(fact
  
  'dr-jekyll-and-richard-simmons
  => 'dr-jekyll-and-richard-simmons)


;;(defmacro when
;;  "Evaluates test. If logical true, evaluates body in an implicit do."
;;  {:added "1.0"}
;;  [test & body]
;;  (list 'if test (cons 'do body)))

(fact
  (macroexpand '(when (the-cows-come :home)
                  (call me :pappy)
                  (slap me :silly)))
  =>
  '(if (the-cows-come :home)
     (do (call me :pappy)
         (slap me :silly))) )

(defmacro unless
  "Inverted 'if'"
  [test & branches]
  (conj (reverse branches) test 'if))

(fact
  (macroexpand '(unless (done-been slapped? me)
                        (slap me :silly)
                        (say "I reckon that'll learn me")))
  =>
  '(if (done-been slapped? me)
     (say "I reckon that'll learn me")
     (slap me :silly)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; syntax quoting
;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmacro code-critic-old
  "phrases are courtesy Hermes Conrad from Futurama"
  [{:keys [good bad]}]
  (list 'do
        (list 'println
              "Great squid of Madrid, this is bad code:"
              (list 'quote bad))
        (list 'println
              "Sweet gorilla of Manila, this is good code:"
              (list 'quote good))))


;; using syntax-quote
(defmacro code-critic
  "phrases are courtesy Hermes Conrad from Futurama"
  [{:keys [good bad]}]
  `(do (println "Great squid of Madrid, this is bad code:"
                (quote ~bad))
       (println "Sweet gorilla of Manila, this is good code:"
                (quote ~good))))


(fact "Quoting does not include a namespace unless your code
       includes a namespace"
  '+
  
  => '+)

(fact "Write out the namespace and it'll be returned by normal quote"

  'clojure.core/+
  => 'clojure.core/+ )


(fact "Syntax quoting will always include the symbol's full namespace  "

  `+
  => clojure.core/+)

(fact "Quoting a list"

  '(+ 1 2)
  => '(+ 1 2))

(fact "Syntax-quoting a list"

  `(+ 1 2)
  => '(clojure.core/+ 1 2))


(fact "the tilde"

  `(+ 1 ~(inc 1))
  => (clojure.core/+ 1 2))

(fact "Without the unquote, syntax quote returns the unevaluated
       form with fully qualified symbols:"

  `(+ 1 (inc 1))
  => '(clojure.core/+ 1 (clojure.core/inc 1)))

(facts "three examples of building a list:"

  (fact  "Building a list with the list function"

    (list '+ 1 (inc 1))
    => '(+ 1 2))

  (fact "Building a list from a quoted list - super awkward"

    (concat '(+ 1) (list (inc 1)))
    => '(+ 1 2))

  (fact "Building a list with unquoting"

    `(+ 1 ~(inc 1))
    => '(clojure.core/+ 1 2))  )


