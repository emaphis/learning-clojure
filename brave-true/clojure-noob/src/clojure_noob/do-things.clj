(ns clojure-noob.do-things
  (:require [midje.sweet :refer :all]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; forms
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; do


(if true
  (do (println "Success!")
      "abra cadabra")
  (do (println "Failure!")
      "hocus pocus"))

(when true
  (println-str "Success!")
  "abra cadabra")

(def failed-protagonist-names
  ["Larry Potter"
   "Doreen the Explorer"
   "The Incredible Bulk"])


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Maps
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(fact
  "empty map"
  {} => {})

(fact ":a :b :c are keywords"
  {:a 1
   :b "boring example"
   :c []}
  =>
  {:a 1 :b "boring example" :c []})

;; Maps can be nested
{:name {:first "John" :middle "Jacob" :last "Jingleheimerscmidt"}}

(fact "using 'get' function"
  (get {:a 0 :b 1} :b)
  => 1
  (get {:a 0 :b {:c "ho hum"}} :b)
  => {:c "ho hum"})

(fact "default value"
  (get {:a 0 :b 1} :c "UNICORNS")
  => "UNICORNS")

(fact  "'get-in' for nested maps"
  (get-in {:a 0 :b {:c "ho hum"}} [:b :c])
  => "ho hum")

(fact  "maps can act as lookup functions, with a key as an arguement"
  ({:name "The Human Coffee Pot" :occupation "Brewing"} :name)
  => "The Human Coffee Pot")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Keywords

(facts "about keywords"
  (fact "keywords can act as a lookup function"
    (:a {:a 1 :b 2 :c 3})
    => 1)
  (fact "This is equivalent to"
    (get {:a 1 :b 2 :c 3} :a)
    => 1)
  (fact "defult values"
    (:d {:a 1 :b 2 :c 3} "FAERIES")
    => "FAERIES"))

(fact "using 'hash-map' to create a map"
  (hash-map :a 1 :b 2)
  => {:a 1 :b 2})


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; vectors
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(facts "about vectors"

  (fact  "Here's a vector literal"
    [3 2 1]
    => [3 2 1])

  (fact "Here we 're returning an element of a vector"
    (get [3 2 1] 0)
    => 3)

  (fact
    "Another example of getting by index. Notice as well that vector
     elements can be of any type and you can mix types."
    (get ["a" {:name "Pugsley Winterbottom"} "c"] 1)
    => {:name "Pugsley Winterbottom"}))

(fact "creat a vector with the 'vector' function"
  (vector "creepy" "full" "moon")
  => ["creepy" "full" "moon"])

(fact "elements get added to the 'end' of a vector"
  (conj [1 2 3] 4)
  => [1 2 3 4])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Lists
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(fact "Here 's a list - note the preceding single quote"
  '(1 2 3 4) => '(1 2 3 4))

(fact "Doesn 't work for lists"
  (get '(100 200 300 400) 0)=> nil)

(fact "This works but has different performance characteristics which we
       don't care about right now."
  (nth '(100 200 300 400) 3)
  => 400)

(fact "You can create lists with the list function:"
  (list 1 2 3 4)
  => '(1 2 3 4))

(fact "Elements get added to the beginning of a list:"
  (conj '(1 2 3) 4)
  => '(4 1 2 3))

;; by defualt use vectors instead of lists.


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Sets
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(facts "about sets:"

  (fact "Literal notation"
    #{"hannah montanna" "miley cyrus" 20 45}
      => #{"hannah montanna" "miley cyrus" 20 45})

  (fact "If you try to add :b to a set which already contains :b,
          the set still only has one :b"
    (conj #{:a :b} :b)
    => #{:a :b})

  (fact "You can check whether a value exists in a set"
    (get #{:a :b} :a)
    => :a

    (:a #{:a :b})
    => :a

    (get #{:a :b} "hannah montanna")
    => nil))

(facts "you can create sets from existing vectors and list"
  (fact
    (set [3 3 3 4 4])
    => #{3 4})

  (fact  "3 exists in vector"
    (get (set [3 3 3 4 4]) 3)
    => 3)

  (fact  "but 5 doesn't"
    (get (set [3 3 3 4 4]) 5)
    => nil))

(facts "you can create hash sets and sorted sets"
  (hash-set 1 1 3 1 2)
  => #{1 2 3}

  (sorted-set :b :a :c)
  => #{:a :b :c})


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; symbols and naming
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def failed-movie-titles ["Gone With the Moving Air" "Swellfellas"])

(fact "Identity returns its argument"
  (identity 'test)
  => test)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; quoting
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(fact "symbol returns the 'object' is refers to"
  failed-protagonist-names
  => ["Larry Potter" "Doreen the Explorer" "The Incredible Bulk"]
  (first failed-protagonist-names)
  => "Larry Potter")

(fact "about quoting"
  (fact
    "Quoting a symbol tells Clojure to use the symbol itself as a data structure"
    'failed-protagonist-names
    => 'failed-protagonist-names)

  (fact "can 'eval' quoted symbol"
    (eval 'failed-protagonist-names)
    => ["Larry Potter" "Doreen the Explorer" "The Incredible Bulk"])

  (fact "throws exception"
    (first 'failed-protagonist-names)
    => (throws Exception))

  (fact
    (first ['failed-protagonist-names 'failed-antagonist-names])
    => 'failed-protagonist-names))

(facts "You can also quote collections like lists, maps, and vectors. All
        symbols within the collection will be unevaluated:"
  (fact
    '(failed-protagonist-names 0 1)
    => '(failed-protagonist-names 0 1))
  (fact
    (first '(failed-protagonist-names 0 1))
    => 'failed-protagonist-names)
  (fact
    (second '(failed-protagonist-names 0 1))
    => 0))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Fucntions

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; calling functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(fact "Return value of "or" is first truthy value, and + is truthy"
  (or + -) => truthy)

(fact "You can use that expression as the operator in another expression:"
  ((or + -) 1 2 3)
  => 6)

(facts "a couple more valid function calls which return 6:"

  (fact "Return value of 'and' is first falsey value or last truthy value.
         + is the last truthy value"
    ((and (= 1 1) +) 1 2 3) => 6)

  (fact
    " Return value of 'first' is the first element in a sequence"
    ((first [+ 0]) 1 2 3) => 6))

(fact "the 'inc' function increments a number by 1"
  (inc 1.1)
  => 2.1
  (map inc [0 1 2 3])
  => '(1 2 3 4))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; defining functions

(defn too-enthusiastic
  "Return a cheer that might be a bit too enthusiastic"
  [name]
  (str "OH. MY. GOD! " name " YOU ARE MOST DEFINITELY LIKE THE BEST "
       "MAN SLASH WOMAN EVER I LOVE YOU AND WE SHOULD RUN AWAY TO SOMEWHERE"))

(fact
  (too-enthusiastic "Zelda")
  => "OH. MY. GOD! Zelda YOU ARE MOST DEFINITELY LIKE THE BEST MAN SLASH WOMAN EVER I LOVE YOU AND WE SHOULD RUN AWAY TO SOMEWHERE" )

;;; look up docstring
;(doc map)

;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Parameters

(defn no-params
  []
  "I take no parameters!")

(defn one-param
  [x]
  (str "I take one param: " x " It'd better be a string!"))

(defn two-params
  [x y]
  (str "Two Parametes! That's nothing! Pah! I will smoosh them "
       "together to spite you! " x y))


;; Multi arity

(defn do-things [one & many] )

(defn multi-arity
  ;; 3-arity arguments and body
  ([first-arg second-arg third-arg]
     (do-things first-arg second-arg third-arg))
  ;; 2-arity arguments and body
  ([first-arg second-arg]
     (do-things first-arg second-arg))
  ;; 1-arity arguments and body
  ([first-arg]
     (do-things first-arg)))


;; overloading by arity
(defn x-chop
  "Describe the kind of chop you're inflicting on someone"
  ([name chop-type]
   (str "I " chop-type " chop " name "! Take that!"))
  ([name]
   (x-chop name "karate")))  ;; defining a function in terms of itself.

(fact "call 'x-chop' with two agruments"
  (x-chop "Kanye West" "slap")
  => "I slap chop Kanye West! Take that!")

(fact "with one arguement"
  (x-chop "Kanye East")
  => "I karate chop Kanye East! Take that!")


;; 'rest' argumens

(defn codger-communication
  [whippersnapper]
  (str "Get off my lawn, " whippersnapper "!!!"))

(defn codger
  [& whippersnappers]   ; the ampersand idecates the "rest-param"
  (map codger-communication whippersnappers))

(fact "using rest arguments"
  (codger "Billy" "Anne-Marie" "The Incredible Bulk")
 =>
 '("Get off my lawn, Billy!!!"
   "Get off my lawn, Anne-Marie!!!"
   "Get off my lawn, The Incredible Bulk!!!"))

;; mixed rest params
(defn favorite-things
  [name & things]
  (str "Hi, " name ", here are my favorite things: "
       (clojure.string/join ", " things)))

(fact
  (favorite-things "Doreen" "gum" "shoes" "kara-te")
  => "Hi, Doreen, here are my favorite things: gum, shoes, kara-te")


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Destructuring
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Return the first element of a collection
(defn my-first
  [[first-thing]]  ; Notice the first-thing is within a vector
  first-thing)

(fact
  (my-first ["oven" "bike" "waraxe"])
  => "oven")

;; without destructuring
(defn my-other-first
  [collection]
  (first collection))

(fact
  (my-other-first ["nickel" "hair"])
  => "nickel")

;; name as many items as we need:
(defn chooser
  [[first-choice second-choice & unimportant-choices]]
  (println (str "Your first choice is: " first-choice))
  (println (str "Your second choice is: " second-choice))
  (println (str "We're ignoring the rest of your choices. "
                "Here they are in case you need to cry over them: "
                (clojure.string/join ", " unimportant-choices))))

(fact
  (chooser ["Marmalade", "Handsome Jack", "Pigpen", "Aquaman"])
  => nil)
;  Your first choice is: Marmalade
;  Your second choice is: Handsome Jack
;  We 're ignoring the rest of your choices. Here they are in case \
;  you need to cry over them: Pigpen, Aquaman)


(defn my-rest
  [[first-thing & rest-things]]
  rest-things)

(fact (my-rest ["oven" "bike" "waraxe"])
  => '("bike" "waraxe"))


;;; destructuring maps

(defn announce-treasure-location
  [{lat :lat lng :lng}]
  (println (str "Treasure lat: " lat))
  (println (str "Treasure lng: " lng)))

(fact
  (announce-treasure-location {:lat 28.22 :lng 81.33})
   ;Treasure lat: 28.22
   ;Treasure lng: 81.33
  => nil)

;; Works the same as above.
(defn announce-treasure-location'
  [{:keys [lat lng]}]
  (println (str "Treasure lat: " lat))
  (println (str "Treasure lng: " lng)))

(fact  "works the same"
  (announce-treasure-location' {:lat 28.22 :lng 81.33})
   ;Treasure lat: 28.22
   ;Treasure lng: 81.33
  => nil)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; function body
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn illustrative-function
  []
  (+ 1 304)
  30
  "joe")

(fact "returns last form"
  (illustrative-function)
  => "joe")

(defn number-comment
  [x]
  (if (> x 6)
    "Oh my gosh! What a big number!"
    "That number's OK, I guess"))

(fact
  (number-comment 5)
  => "That number's OK, I guess"

  (number-comment 7)
  => "Oh my gosh! What a big number!")


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; anonymous functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(fact
  ((fn [x y] (+ x y)) 3 4)
  => 7)

(fact "Example"
  (map (fn [name] (str "Hi, " name))
       ["Darth Vader" "Mr. Magoo"])
  => '("Hi, Darth Vader" "Hi, Mr. Magoo"))

(fact  "Another example"
  ((fn [x] (* x 3)) 8)
  => 24)

(def my-special-multiplier (fn [x] (* x 3)))

(fact "associating an anonymous function with a name"
  (my-special-multiplier 12)
  => 36)

;; I'm surprised!

;; #(* % 3)  ; multiply by three

(fact "Apply this weird looking thing"
  (#(* % 3) 8)
  => 24)

(fact "Another example:"
  (map #(str "Hi, " %)
       ["Darth Vader" "Mr. Magoo"])
  => '("Hi, Darth Vader" "Hi, Mr. Magoo"))

;; look the same
;; (* 8 5)    ; Function call
;; #(* % 3)   ; Anonymous function

(fact "multi-parameters %1 %2 %3 %4"
  (#(str %1 " and " %2) "corn bread" "butter beans")
  => "corn bread and butter beans")

(fact " a rest param:"
  (#(identity %&) 1 "blarg" :yip)
  => '(1 "blarg" :yip))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; returning functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; functions can return funtions:

;; inc-by is in scope, so the returned function has access to it even
;; when the returned function is used outside inc-maker
(defn inc-maker
  "Create a custom incrementor"
  [inc-by]
  #(+ % inc-by))

(def inc3 (inc-maker 3))

(fact
  (inc3 7)
  => 10)

;; Woohoo!


;; see hobbit.clj for hobbit example


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; let
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(fact "about let"
  (let [x 3]
  x)
  => 3)

(fact "about dalmations"
  (def dalmatian-list
    ["Pongo" "Perdita" "Puppy 1" "Puppy 2"]) ; and 97 more...
  (let [dalmatians (take 2 dalmatian-list)]
    dalmatians)
  => '("Pongo" "Perdita"))

(fact "'let' introduces a new scope"
  (def x 0)
  (let [x 1] x)
  => 1)

(fact "you can reference existing bindings in your let binding:"
  (def x 0)
  (let [x (inc x)] x)
  => 1)

(fact "You can also use rest-params in let, just like you can in functions:"
  (let [[pongo & dalmatians] dalmatian-list]
    [pongo dalmatians])
  => ["Pongo" '("Perdita" "Puppy 1" "Puppy 2")])


;;;;;;;;;;;;;;;;;;;;;;
;; loop
;;;;;;;;;;;;;;;;;;;;;;
(fact "'loop' provides another way to do recursion in Clojure.
        Let's look at a simple example:"
  (loop [iteration 0]
    (println (str "Iteration " iteration))
    (if (> iteration 3)
      (println "Goodbye!")
      (recur (inc iteration))))
  => nil)
;;Iteration 0
;;Iteration 1
;;Iteration 2
;;Iteration 3
;;Iteration 4
;;Goodbye!

(fact "You could in fact accomplish the same thing just using functions:"
  (defn recursive-printer
    ([]
     (recursive-printer 0))
    ([iteration]
     (println iteration)
     (if (> iteration 3)
       (println "Goodbye!")
       (recursive-printer (inc iteration)))))
  (recursive-printer)
  => nil)
;;Iteration 0
;;Iteration 1
;;Iteration 2
;;Iteration 3
;;Iteration 4
;;Goodbye!


;;;;;;;;;;;;;;;;;;;;;;;;
;; reduce
;;;;;;;;;;;;;;;;;;;;;;;

(facts "about reduce"

  (fact "sum with reduce"
    (reduce + [1 2 3 4])
    => 10)

  (fact "same as"
    (+ (+ (+ 1 2) 3) 4)
    => 10)

  (fact "Reduce also takes an optional initial value"
    (reduce + 15 [1 2 3 4])
    => 25))


;;; one definition of 'reduce'
(defn my-reduce
  ([f initial coll]
     (loop [result initial
            remaining coll]
       (let [[current & rest] remaining]
         (if (empty? remaining)
           result
           (recur (f result current) rest)))))
  ([f [head & tail]]
     (my-reduce f (f head (first tail)) (rest tail))))

(fact "test 'my-reduce'"
  (my-reduce + [1 2 3 4])
  => 10
  (my-reduce + 15 [1 2 3 4])
  => 25)
