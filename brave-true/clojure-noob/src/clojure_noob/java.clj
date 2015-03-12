(ns clojure-noob.java
  (:require [midje.sweet :refer :all]))

;; classes, objects, methods.

(fact "Math.abs(-50):"

  (Math/abs -50)
  => 50)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; java interop

;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; interop syntax
;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; (.methodName object)


(fact "calling java methods:"

  (.toUpperCase "By Bluebeard's bananas!")
  => "BY BLUEBEARD'S BANANAS!"

  (.indexOf "Let's synergize our bleeding edges" "y")
  => 7 )

;; equivalent to the following Java:
;; "By Bluebeard's bananas!".toUpperCase()
;; "Let's synergize our bleeding edges".indexOf("y")


(fact "You can also call static methods on classes and access classes'
       static fields: "

  (java.lang.Math/abs -3)
  => 3

  java.lang.Math/PI
  => 3.141592653589793 )

(fact  "dot special form:"

  (macroexpand-1 '(.toUpperCase "By Bluebeard's bananas!"))
  => '(. "By Bluebeard's bananas!" toUpperCase)

  (macroexpand-1 '(.indexOf "Let's synergize our bleeding edges" "y"))
  => '(. "Let's synergize our bleeding edges" indexOf "y")

  (macroexpand-1 '(Math/abs -3))
  => '(. Math abs -3) )

;; general form:
;; (. object-expr-or-classname-symbol method-or-member-symbol optional-args*)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; creating and mutating instances
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(fact "There are two ways to create a new object: (new ClassName optional-args*)
       and (ClassName. optional-args*):"

  (new String)
  => ""

  (String.)
  => ""

  (String. "To Davey Jones' Locker with ye hardies")
  => "To Davey Jones' Locker with ye hardies"   )


(fact "create and modify a java stack:"

  (java.util.Stack.)
  => []

  (let [stack (java.util.Stack.)]
    (.push stack "Latest episode of Game of Thrones, ho!")
    stack)
  => ["Latest episode of Game of Thrones, ho!"]  )


(fact "the return value of push, is the string pushed on the stack:"

  (.push (java.util.Stack.) "Latest episode of Game of Thrones, ho!")
  => "Latest episode of Game of Thrones, ho!" )


(fact "the java stack is a seqable data structure:"

  (let [stack (java.util.Stack.)]
    (.push stack "Latest episode of Game of Thrones, ho!")
    (first stack))
  => "Latest episode of Game of Thrones, ho!" )


(fact "Clojure provides the doto macro, which allows you to execute multiple
        methods on the same object more succinctly:"

  (doto (java.util.Stack.)
    (.push "Latest episode of Game of Thrones, ho!")
    (.push "Whoops, I meant 'Land, ho!'"))
  => ["Latest episode of Game of Thrones, ho!" "Whoops, I meant 'Land, ho!'"] )

(macroexpand-1
 '(doto (java.util.Stack.)
    (.push "Latest episode of Game of Thrones, ho!")
    (.push "Whoops, I meant 'Land, ho!'")))
;; => (clojure.core/let
;; =>  [G__2876 (java.util.Stack.)]
;; =>  (.push G__2876 "Latest episode of Game of Thrones, ho!")
;; =>  (.push G__2876 "Whoops, I meant 'Land, ho!'")
;; =>  G__2876)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; importing
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(import java.util.Stack)

(fact "importing: you get to use classes without having to type out their
        entire package prefix:"

  (Stack.)
  => [] )

;; multi-class import
(import [java.util Date Stack]
        [java.net Proxy URI])

(Date.)
;; => #inst "2015-03-12T03:15:32.744-00:00"

;; usually you import with the 'ns' macro:
;; (ns pirate.talk
;;  (:import [java.util Date Stack]
;;           [java.net Proxy URI]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; commonly used java classes


;;;;;;;;;;;;;;;;;;;;;;;
;; System
;;;;;;;;;;;;;;;;;;;;;;;
;; exit, getenv, getProperty

;; (System/getenv)  ;; wow!

(System/getProperty "user.dir")
;; => "/home/emaphis/src/learning-clojure/brave-true/clojure-noob"

(System/getProperty "java.version")
;;=> "1.7.0_75"


;;;;;;;;;;;;;;;;
;; Date
;;;;;;;;;;;;;;;;

(Date.)
;;=> #inst "2015-03-12T03:26:49.443-00:00"
;; java.util.DateFormat  to customize and convert date to strings
;; clj-time


;;;;;;;;;;;;;;;;;;;;;;;;;
;; Files and IO
;;;;;;;;;;;;;;;;;;;;;;;;;

(let [file (java.io.File. "/")]
  (println (.exists file))
  (println (.canWrite file))
  (println (.getPath file)))
;; => true
;; => false
;; => /

;; spit slurp:



(fact "slurping and spitting:"

  (spit "/tmp/hercules-todo-list"
        "- kill dat lion brov
- chop up what nasty multi-headed snake thing")
  => nil
  
  (slurp "/tmp/hercules-todo-list")
  =>
  "- kill dat lion brov
- chop up what nasty multi-headed snake thing"  )


(fact "StringWriter, which allows you to perform IO operations on a string:"

  (let [s (java.io.StringWriter.)]
    (spit s "- capture cerynian hind like for real")
    (.toString s))
  => "- capture cerynian hind like for real"  )


(fact "you can also read from a StringReader with slurp:"

  (let [s (java.io.StringReader. "- get erymanthian pig what with the tusks")]
    (slurp s))
  => "- get erymanthian pig what with the tusks"  )


;; The with-open macro is another convenience: it implicitly closes a resource
;; at the end of its body.

(fact
  
  (with-open [todo-list-rdr (clojure.java.io/reader "/tmp/hercules-todo-list")]
    (doseq [todo (line-seq todo-list-rdr)]
      (println todo)))
  => nil)
;; => - kill dat lion brov
;; => - chop up what nasty multi-headed snake thing

