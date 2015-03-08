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


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; applying out knowledge
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Without syntax-quote
(defmacro code-praiser
  [code]
  (list 'println
        "Sweet gorilla of Manila, this is good code:"
        (list 'quote code)))

;; With syntax-quote
(defmacro code-praiser
  [code]
  `(println
    "Sweet gorilla of Manila, this is good code:"
    (quote ~code)))

(fact "FIXME they don't have identical expansions"

  (macroexpand '(code-praiser (+ 1 1)))
  =>
  '(clojure.core/println "Sweet gorilla of Manila, this is good code:" (quote (+ 1 1))))


(defmacro code-praiser
  [code]
  `(println
    "Sweet gorilla of Manila, this is good code:"
    (quote code)))

(fact "here's what happens if you don't unquote code in the
       macro definition:"

  (macroexpand '(code-praiser (+ 1 1)))
  =>
  '(clojure.core/println
    "Sweet gorilla of Manila, this is good code:"
    (quote clojure-noob.macros/code)))


(defmacro code-makeover
  [code]
  `(println "Before: " (quote ~code))
  `(println "After: " (quote ~(reverse code))))

(fact "why do we need 'do'"

  (code-makeover (1 2 +))
  => nil
;;  After:  (+ 2 1)
  )


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Refactoring a macro and unquote splicing
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; better
(defn criticize-code
  [criticism code]
  `(println ~criticism (quote ~code)))

(defmacro code-critic
  [{:keys [good bad]}]
  `(do ~(criticize-code "Cursed bacteria of Liberia, this is bad code:" bad)
       ~(criticize-code "Sweet sacred boa of Western and Eastern Samoa, this is good code:" good)))

;; more better
(defmacro code-critic
  [{:keys [good bad]}]
  `(do ~(map #(apply criticize-code %)
             [["Great squid of Madrid, this is bad code:" bad]
              ["Sweet gorilla of Manila, this is good code:" good]])))

(fact "Oh No!"

  (code-critic {:good (+ 1 1) :bad (1 + 1)})
  => (throws NullPointerException))

(fact

  (clojure.pprint/pprint (macroexpand '(code-critic {:good (+ 1 1) :bad (1 + 1)})))
  => nil)
;;(do
;; ((clojure.core/println
;;   "Great squid of Madrid, this is bad code:"
;;   '(1 + 1))
;;  (clojure.core/println
;;   "Sweet gorilla of Manila, this is good code:"
;;   '(+ 1 1))))

;;(do
;; ((clojure.core/println "criticism" '(1 + 1))
;;  (clojure.core/println "criticism" '(+ 1 1))))
;;=>
;; ;; After evaluating first println call:
;;(do
;;(nil
 ;; (clojure.core/println "criticism" '(+ 1 1))))

;; After evaluating second println call:
;;(do
;; (nil nil))


(fact "Without unquote splicing"

  `(+ ~(list 1 2 3))(clojure.core/+ (1 2 3))
  => '(clojure.core/+ (1 2 3)))

(fact "With unquote splicing"

  `(+ ~@(list 1 2 3))
  => '(clojure.core/+ 1 2 3))


;; the best
(defmacro code-critic
  [{:keys [good bad]}]
  `(do ~@(map #(apply criticize-code %)
              [["Sweet lion of Zion, this is bad code:" bad]
               ["Great cow of Moscow, this is good code:" good]])))

(fact "unquote splicing"

  (code-critic {:good (+ 1 1) :bad (1 + 1)})
  => nil)
;;Sweet lion of Zion, this is bad code: (1 + 1)
;;Great cow of Moscow, this is good code: (+ 1 1)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; things to watch out for

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; variable capture
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def message "Good job!")

(defmacro with-mischief
  [& stuff-to-do]
  (concat (list 'let ['message "Oh, big deal!"])
          stuff-to-do))

(fact
  (with-mischief
    (println "Here's how I feel about that thing you did: " message))
  => nil)
;; Here's how I feel about that thing you did: Oh, big deal!


(def message "Good job!")

(defmacro with-mischief
  [& stuff-to-do]
  `(let [message "Oh, big deal!"]
     ~@stuff-to-do))

;;(fact "using syntax-quote results in exception"

;;  (with-mischief
;;    (println "Here's how I feel about that thing you did: " message))
;;  => (throws Compiler/CompilerException))
;;Exception: Can't let qualified name: user/message

;;(gensym)
;; => G__15169

;;(gensym)
;; => G__15178

;;(gensym 'message)
;; => message15184

;;(gensym 'message)
;; => message15187

;; better example
(defmacro without-mischief
  [& stuff-to-do]
  (let [macro-message (gensym 'message)]
    `(let [~macro-message "Oh, big deal!"]
       ~@stuff-to-do
       (println "I still need to say: " ~macro-message))))

(fact "using gensym"

  (without-mischief
   (println "Here's how I feel about that thing you did: " message))
  => nil)
;; Here's how I feel about that thing you did:  Good job!
;; I still need to say:  Oh, big deal!


(defmacro gensym-example
  []
  `(let [name# "Larry Potter"] name#))

(fact

  (gensym-example)
  => "Larry Potter")

(macroexpand '(gensym-example))
;; =>
;;(let* [name__4947__auto__ "Larry Potter"]
;;      name__4947__auto__)


;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; double evaluation
;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmacro report
  [to-try]
  `(if ~to-try
     (println (quote ~to-try) "was successful:" ~to-try)
     (println (quote ~to-try) "was not successful:" ~to-try)))


(fact  "Thread/sleep takes a number of milliseconds to sleep for"

  (report (do (Thread/sleep 1000) (+ 1 1)))
  => nil)
;; (do (Thread/sleep 1000) (+ 1 1)) was successful: 2

;; better: autogensym
(defmacro report
  [to-try]
  `(let [result# ~to-try]
     (if result#
       (println (quote ~to-try) "was successful:" result#)
       (println (quote ~to-try) "was not successful:" result#))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; macros all the way down.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(fact "Instead of multiple calls to report..."

  (report (= 1 1))
  => nil
;; (= 1 1) was successful: true

  (report (= 1 2))
  => nil)
;; (= 1 2) was not successful: false

(fact "Let's iterate"

  (doseq [code ['(= 1 1) '(= 1 2)]]
    (report code))
  => nil)
;;code was successful: (= 1 1)
;;code was successful: (= 1 2)


;;To resolve this situation we might write another macro, like:

(defmacro doseq-macro
  [macroname & args]
  `(do
     ~@(map (fn [arg] (list macroname arg)) args)))

(fact "using new macro"

  (doseq-macro report (= 1 1) (= 1 2))
  => nil)
;;(= 1 1) was successful: true
;;(= 1 2) was not successful: false


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Brews for the Brave and the True - set brews.clj
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
