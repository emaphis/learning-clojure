(ns clojure-noob.fun-prog
  (:require [midje.sweet :refer :all]))

(facts  "pure functions:"
  (fact (get [:chumbawumba] 0)
    => :chumbawumba)
  (fact (reduce + [1 10 5])
    => 16)
  (fact (str "wax on " "wax off")
    => "wax on wax off"))

(facts "pure functions are refentially transparent"
  (fact (+ 1 2)
    => 3))

(defn wisdom
  [words]
  (str words ", Daniel-san"))

(fact (wisdom "Always bathe on Fridays")
  => "Always bathe on Fridays, Daniel-san")

;; always gives a differen result so not referntially transperent
(defn year-end-evaluation
  []
  (if (> (rand) 0.5)  ;; rand is not a function.
    "You get a raise!"
    "Better luck next year!"))

(defn analysis
  "analysis is a function"
  [text]
  (str "Character count: " (count text)))

(defn analyze-file
  "but analyze-file isn't"
  [filename]
  (analysis (slurp filename)))  ;; slurp is not a function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; living with immutable data structures

(defn no-mutation
  [x]
  (println x)

  ;; let creates a new scope
  (let [x "Kafka Human"]
    (println x))

  ;; outside of the let scope
  (println x))

(no-mutation "Existential Angst Person")
;; =>
;;Existential Angst Person
;;Kafka Human
;;Existential Angst Person

(defn sum
  ([vals] (sum vals 0))
  ([vals accumulating-total]
    (if (empty? vals)
      accumulating-total
      (sum (rest vals) (+ (first vals) accumulating-total)))))

  (fact "test sum"
    (sum []) => 0
    (sum [1]) => 1
    (sum [39 5 1]) => 45)


;;;;;;;;;;
;; function compostion instead of attribute mutation

(require '[clojure.string :as s])

(defn clean
  [text]
  (s/replace (s/trim text) #"lol" "LOL"))

(fact (clean "My boa constrictor is so sassy lol!  ")
  => "My boa constrictor is so sassy LOL!")

(fact "pretty common style"
  ((comp s/lower-case s/trim) " Unclean string ")
  => "unclean string")


(defn two-comp
  [f g]
  (fn [& args]
    (f (apply g args))))

(def my-lower-trim (two-comp s/lower-case s/trim))

(fact (my-lower-trim " Unclean string  ")
  => "unclean string")

(def sum-times2 (two-comp  #(* 2 %) +))

(fact (sum-times2 1 2 3)
  => 12)

;; define a new version of 'comp'
(defn my-comp
  [f & funs]
  (if (empty? funs) f
      (recur (fn [& args]
                 (f (apply (first funs) args)))
               (rest funs))))

(fact ((my-comp s/lower-case s/trim) " Unclean string ")
  => "unclean string")
(fact ((my-comp s/reverse s/lower-case s/trim) " Unclean string ")
  => "gnirts naelcnu")

(def sum-times2' (two-comp  #(* 2 %) +))

(fact (sum-times2' 1 2 3)
  => 12)

;;;;;;;;;;;;;;;;;;;;
;; memoization

(defn sleepy-identity
  "returns the given value after a 1 second 'calculation'"
  [x]
  (Thread/sleep 1000)
  x)

(sleepy-identity "Mr. Fantatstico")

(def memo-sleepy-identity
  "does calculation once, then returns imediatly"
  (memoize sleepy-identity))

(memo-sleepy-identity "Mr. Fantatstico")
