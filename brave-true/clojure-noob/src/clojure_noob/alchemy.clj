(ns clojure-noob.alchemy
  (:require [midje.sweet :refer :all]))

(def addition-list (list + 1 2))

(fact "clojure can evaluate lists"

  (eval addition-list)
  => 3

  (eval '(+ 1 2))
  => 3)

(fact "'text' => (reader) => 'data-structures' => (evaluator) => 'value'"

  (if :in-doubt (do "something"))
  => "something")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; the 'reader'
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; (str "To understand what recursio is," " you must first understand recursion")
(fact "the result of reading this reader form is a list with 3 members:"
  (read-string "(+ 8 3)")
  => '(+ 8 3))


;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; reader macros
;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; (#(+ 1 %) 3)

;;(read-string "#(+ 1 %)")  ;;reader macro
;; => (fn* [p1__13149#] (+ 1 p1__13149#))

(fact "quote special form"

  (read-string "'(a b c)")
  => '(quote (a b c)))

(fact "the deref reader macro"

  (read-string "@var")
  => '(clojure.core/deref var))

(fact "The semicolon designates the single-line comment reader macro:"

  (read-string "; ignore!\n(+ 1 2)")
  => '(+ 1 2))


;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; evaluation
;;;;;;;;;;;;;;;;;;;;;;;;;;;

(fact
  (def addition-list (list + 1 2))
  (eval addition-list)
  => 3)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; things that evaluate to themselves
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(fact "A string evaluates to itself"

  (eval (read-string "\"t\""))
  => "t")

(fact "accomplishes the same thing"

  (eval "t")
  => "t")

(fact "bools and keywords"

  (eval (read-string "true"))
  => true

  (eval (read-string "false"))
  => false

  (eval (read-string ":huzzah"))
  => :huzzah)


;;;;;;;;;;;;;;;;;
;; symbols
;;;;;;;;;;;;;;;;;

(fact "The symbol x is bound to 5. When the evaluator resolves x, it
       resolves it to the value 5:"

  (let [x 5]
    (+ x 3))
  => 8)

(fact "x is mapped to 15. Clojure resolves the symbol x to the value 15:"

  (def x 15)
  (+ x 3)
  => 18)

(fact "Now x is mapped to 15, but we introduce a local binding of x to 5.
       x is resolved to 5:"

  (def x 15)
  (let [x 5]
    (+ x 3))
  => 8)

(fact "The 'closest' binding takes precedence:"

  (let [x 5]
    (let [x 6]
      (+ x 3)))
  => 9)

(fact "'exclaim' is mapped to a function. Within the function body,
       'exclamation' is bound to the argument passed to the function"

  (defn exclaim
    [exclamation]
    (str exclamation "!"))
  (exclaim "Hadoken")
  => "Hadoken!" )

(fact "'map' and 'inc' are both mapped to functions:"

  (map inc [1 2 3])
  => '(2 3 4))


;;;;;;;;;;;;;;;;;;
;; lists
;;;;;;;;;;;;;;;;;

(fact "If the data structure is an empty list, it evaluates to an empty list:"

  (eval (read-string "()"))
  => '())

;; Otherwise, it is a call to the first element of the list:


;;;;;;;;;;;;;;;;;;;;;;;
;; function calls
;;;;;;;;;;;;;;;;;;;;;;;

(fact "the + symbol resolves to a function. The function is 'called' with
        the arguments 1 and 2:"

  (+ 1 2)
  => 3)


;;;;;;;;;;;;;;;;;;;;;;;;;;
;; special forms
;;;;;;;;;;;;;;;;;;;;;;;;;;

(fact "You can also call special forms. For example:"

  (eval (read-string "(if true 1 2)"))
  => 1)

(fact "the 'quote' special form"

  '(a b c)
  => (quote (a b c)))


;;;;;;;;;;;
;; macros
;;;;;;;;;;;

;; Exploding heads.

(fact "create a list which represents infix addition:"

  (read-string "(1 + 1)")
  => '(1 + 1))

(fact "but Clojure will throw an exception if you try to make it
       evaluate this list:"

  (eval (read-string "(1 + 1)"))
  => (throws ClassCastException))

(fact  "create a reorganized list that can br successfully evaluated:"

  (let [infix (read-string "(1 + 1)")]
    (list (second infix) (first infix) (last infix)))
  => '(+ 1 1))

(fact "If you eval this, then it returns 2, just like you'd expect:"

  (eval
   (let [infix (read-string "(1 + 1)")]
     (list (second infix) (first infix) (last infix))))
  => 2)

(defn eval-infix
  "do we really need a macro?"
  [expression]
  (eval
   (let [infix (read-string expression)]
     (list (second infix) (first infix) (last infix)))))

(fact "well, what do you know"

  (eval-infix "(1 + 1)")
  => 2)

(defmacro ignore-last-operand
  [function-call]
  (butlast function-call))

(fact "we can unit test macros?"

  (ignore-last-operand (+ 1 2 10))
  => 3) ;; yes we can.


(fact "This will not print anything"

  (ignore-last-operand (+ 1 2 (println "look at me!!!")))
  => 3)  ;; sweet

(fact
  (macroexpand '(ignore-last-operand (+ 1 2 10)))
  => '(+ 1 2)

  (macroexpand '(ignore-last-operand (+ 1 2 (println "look at me!!!"))))
  => '(+ 1 2))

(fact " read => macro-expand => eval"

  (when :in-doubt "something")
  => "something"

  (macroexpand '(when :in-doubt "something"))
  => '(if :in-doubt (do "something"))

  (eval (macroexpand '(when :in-doubt "something")))
  => "something")


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; a syntax abstraction exampe: the '->' macro
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn read-resource
  "Read a resource into a string"
  [path]
  (read-string (slurp (clojure.java.io/resource path))))

(defn read-resource
  [path]
  (-> path
      clojure.java.io/resource
      slurp
      read-string))

(fact "test 'read-resource"
  (read-resource "resource.txt")
  => 'This)

(defmacro backwards
  [form]
  (reverse form))

(fact
  (backwards (" cowboys" "mamas don't let your babies grow up to be" str))
  => "mamas don't let your babies grow up to be cowboys")
