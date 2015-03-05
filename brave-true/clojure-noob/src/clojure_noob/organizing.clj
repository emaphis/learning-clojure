(ns user
  (:require [midje.sweet :refer :all]))

;; (ns-name *ns*) ; => clojure-noob.organizing

(fact "storing objects with 'def'"
  (def great-books ["East of Eden" "The Glass Bead Game"])
  => #'user/great-books)

(fact
  great-books
  => ["East of Eden" "The Glass Bead Game"])

;; return a map of interned Vars
;;(ns-interns *ns*)
;; => {great-books #'user/great-books}

(fact "Get a specific Var"
  (get (ns-interns *ns*) 'great-books)
  => #'user/great-books)

;;get the map the namespace uses for looking up a Var when given a symbol:
;;(ns-map *ns*)
;; => very large map which I won't print here; try it out!

(fact "The symbol 'great-books is mapped to the Var we created above"
  (get (ns-map *ns*) 'great-books)
  => #'user/great-books)  ;; reader form of binary data, in this case a Var

(fact "We can deref vars to get the objects they point to:"
  (deref #'user/great-books)
  => ["East of Eden" "The Glass Bead Game"])

(fact "Normally, though, you'll just use the symbol:"
  great-books
  => ["East of Eden" "The Glass Bead Game"])

;; Melvil recoils in horror!:
(def great-books ["The Power of Bees" "Journey to Upstairs"])

(fact "the Var has been updated to the address ot a new vector"
  great-books
  => ["The Power of Bees" "Journey to Upstairs"])

;; Avoid collisions by creating namespaces


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; creating and switching to namespaces
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;(fact  "Creates the namespace if it doesn't exist and return"
;;  (create-ns 'cheese.taxonomy)
;;  => #<Namespace cheese.taxonomy>)

;; Returns the namespace if it already exists
;;user> (create-ns 'cheese.taxonomy)
;; => #<Namespace cheese.taxonomy>

;; Pass the returned namespace as an argument
;;(ns-name (create-ns 'cheese.taxonomy))
; => secret-lair

(in-ns 'cheese.taxonomy)

(def cheddars ["mild" "medium" "strong" "sharp" "extra sharp"])

cheddars

(in-ns 'cheese.analysis)

cheddars

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; refer and alias
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(in-ns 'cheese.taxonomy)
(def cheddars ["mild" "medium" "strong" "sharp" "extra sharp"])
(def bries ["Wisconsin" "Somerset" "Brie de Meaux" "Brie de Melun"])

(in-ns 'cheese.analysis)

(clojure.core/refer 'cheese.taxonomy)

(midje.sweet/fact "now we can refer to 'cheese.taxonomy symbols"
  bries
  => ["Wisconsin" "Somerset" "Brie de Meaux" "Brie de Melun"]
  cheddars
  => ["mild" "medium" "strong" "sharp" "extra sharp"])

;;(midje.sweet/fact
;;  (clojure.core/get (clojure.core/ns-map clojure.core/*ns*) 'bries)
;;  => #'cheese.taxonomy/bries
;;
;;  (clojure.core/get (clojure.core/ns-map clojure.core/*ns*) 'cheddars)
;;  =>   #'cheese.taxonomy/cheddars)

;;--;;--;;
(clojure.core/ns-name (clojure.core/create-ns 'cheese.taxonomy))

(clojure.core/ns-name clojure.core/*ns*)

