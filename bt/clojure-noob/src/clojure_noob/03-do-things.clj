(ns clojure-noob.03-do-things
  (:require [clojure.test :refer :all]))



;; (operator operand operand2 ... operandn66)

(+ 1 2 3)
;; => 6

(str "It was the panda " "in the library " "with a dust buster")
;; => "It was the panda in the library with a dust buster"


;;; Control Flow

;; (if boolean-form
;;   then-form
;;   optional-else-form)

(if true
  "By Zeus's hammer!"
  "By Aquaman's trident!")
;; => "By Zeus's hammer!"

(if false
  "By Zeus's hammer!"
  "By Aquaman's trident!")
;; => "By Aquaman's trident!"


;; you can omit the 'else' branch
(if false
  "By Odin's Elbow!")
;; => nil


;; using the do operator to wrap multiple forms
(if true
  (do (println "Success!")
      "By Zeus's hammer!")
  (do (println "Failure!")
      "By Aquaman's trident!"))
;; Success!
;; => "By Zeus's hammer!"


;;; 'when' is like if and do but with only one clause

(when true
  (println "Success!")
  "abra cadabra")
;; => "abra cadabra"


;;; nil true false Truthiness Equality, Boolean expressions

(nil? 1)
;; => false

(nil? nil)
;; => true

;; nil and false represent logical falseness, all other values are true

(if "bears eat beets"
  "bears beets Battlestar Galactica")
;; => "bears beets Battlestar Galactica"

(if nil
  "This won't be the result because nil is falsey"
  "nil is falsey")
;; => "nil is falsey"

;; equality operator

(= 1 1)
;; => true

(= nil nil)
;; => true

(= 1 2)
;; => false

(= nil false)
;; => false


;; or - returns either the first truthy value or the last value

(or false nil :large_I_mean_venti :why_cant_I_just_say_large)
;; => :large_I_mean_venti

(or (= 0 1) (= "yes" "no"))
;; => false

(or nil)
;; => nil

;; and - returns the first fasley value of if no values are falsey
;; the last truth value

(and :free_wifi :hot_coffee)
;; => :hot_coffee

(and :feelin_super_cool nil false)
;; => nil


;;; Naming Values with def - binding values

(def failed-protagonist-names
  ["Larry Potter" "Doreen the Explorer" "The Incredible Bulk"])

failed-protagonist-names
;; => ["Larry Potter" "Doreen the Explorer" "The Incredible Bulk"]


(defn error-message
  [severity]
  (str "OH GOD! IT'S A DISASTER! WE'RE "
       (if (= severity :mild)
         "MILDLY INCONVENIENCED!"
         "DOOOOMED!")))

(error-message :mild)
;; => "OH GOD! IT'S A DISASTER! WE'RE MILDLY INCONVENIENCED!"


;;; Numbers
93
1.2
1/5

;;; Strings
"Lord Voldemort"
"\"He must not be named\""
"\"Great cow of Moscow!\" - Hermes Conrad "

(def name "Chewbacca")
(str "\"Uggllglglglglglglgll\" - " name)
;; => "\"Uggllglglglglglglgll\" - Chewbacca"

(println (str "\"Uggllglglglglglglgll\" - " name))


;;; Maps

{}  ;; empty map

{:first-name "Charlie"
 :last-name "McFishwich"}

;; associate "string-key" with the + function
{"string-key" +}

;; nested maps:
{:name {:first "John" :middle "Jacob" :last "Jingleheimerschmidt"}}

;; hash-map creates maps

(hash-map :a 1 :b 2)
;; => {:b 2, :a 1}

;; look up values in a map
(get {:a 0 :b {:c "ho hum"}} :b)
;; => {:c "ho hum"}

;; get will return nil for unmatched keys or a default value

(get {:a 0 :b 1} :c)
;; => nil

(get {:a 0 :b 1} :c "unicorns?")
;; => "unicorns?"

;; get in to look up values in nested maps:

(get-in {:a 0 :b {:c "ho hum"}} [:b :c])
;; => "ho hum"

;; using the map as a look up function
({:name "The Human Coffeepot"} :name)
;; => "The Human Coffeepot"

;; keys as look up function

(:name {:name "The Human Coffeepot"})
;; => "The Human Coffeepot"


;;; Keywords

:a
:rumplestiltsken
:34
:_?

;; look up :a in a map
(:a {:a 1 :b 2 :c 3})  ; key as a function
;; => 1

(get {:a 1 :b 2 :c 3} :a)
;; => 1

(:d {:a 1 :b 2 :c 3} "No gnome knows homes like Noah knows")
;; => "No gnome knows homes like Noah knows"


;;; Vectors
;; similar to a 0-indexed array

[1 2 3]

(get [3 2 1] 0)  ; zero indexed
;; => 3

(get ["" {:name "Pugsley Winterbottom"} "c"] 1)
;; => {:name "Pugsley Winterbottom"}


;; the vector function crates vectors
(vector "creepy" "full" "moon")
;; => ["creepy" "full" "moon"]

;; use conj to add items to the end of the vector
(conj [1 2 3] 4)
;; => [1 2 3 4]


;;; Lists

'(1 2 3 4)
;; => (1 2 3 4)

;; to retrieve and element from a list, use nth

(nth '(:a :b :c) 0)
;; => :a

;; nth on a list is slower than get on a vector

;; the list function
(list 1 "two" {3 4})
;; => (1 "two" {3 4})

;; conj adds elements to the beginning of a list
(conj '(1 2 3) 4)
;; => (4 1 2 3)


;;; Sets
;; collections of unique values

#{"kurt vonnegut" 20 "icicle"}

(hash-set 1 1 2 2)
;; => #{1 2}


;; set only hold a unique value
(conj #{:a :b} :b)
;; => #{:b :a}

;; convert a list or vector into a set
(set [3 3 3 4 4])
;; => #{4 3}

;; check membership

(contains? #{:a :b} :a)
;; => true

(contains? #{:a :b} 3)
;; => false

(contains? #{nil} nil)
;; => true

;; membership using a keyword

(:a #{:a :b})
;; => :a

(:c #{:a :b})
;; => nil

(#{:a :b} :b)
;; => :b

;; membership using get

(get #{:a :b} :a)
;; => :a

(get #{:a nil} nil) ; confusing
;; => nil

(get #{:a :b} "kurt vonnegut")
;; => nil


;;; Simplicity

;; It is better to have 100 functions operate on one data structure
;; than 10 functions on 10 data structures.
;; —Alan Perlis

;; Clojure sticks with a few built-in data-structures instead of class based
;; modeling to model data.

;;; Functions

;; Calling Functions

(+ 1 2 3 4)
(* 1 2 3 4)
(first [1 2 3 4])

;; a function expression that returns a function
(or + -)
;; => #function[clojure.core/+]


((or + -) 1 2 3)
;; => 6

((and (= 1 1) +) 1 2 3)
;; => 6

((first [+ 0]) 1 2 3)
;; => 6


;; (1 2 3 4)
;; Unhandled java.lang.ClassCastException
;;   java.lang.Long cannot be cast to clojure.lang.IFn


;;("test" 1 2 3)
;; Unhandled java.lang.ClassCastException
;;   java.lang.Long cannot be cast to clojure.lang.IFn


;; functions passed as parameters

(inc 1.1)
;; => 2.1


(map inc [0 1 2 3])
;; => (1 2 3 4)


;;; Function Call, Macro Calls, and Special Forms

;;; Defining Functions

(defn too-enthusiastic
  "Return a cheer that might be a bit too enthusiastic"
  [name]
  (str "OH. MY. GOD! " name " YOU ARE MOST DEFINITELY LIKE THE BEST "
       "MAN SLASH WOMAN EVER I LOVE YOU AND WE SHOULD RUN AWAY SOMEWHERE"))

(too-enthusiastic "Zelda")
;; => "OH. MY. GOD! Zelda YOU ARE MOST DEFINITELY LIKE THE BEST MAN SLASH WOMAN EVER I LOVE YOU AND WE SHOULD RUN AWAY SOMEWHERE"


(defn no-params
  []
  "I take no parameters!")

(defn one-param
  [x]
  (str "I take one parameter: " x))

(defn two-params
  [x y]
  (str "Two parameters! That's nothing! Pah! I will smoosh them "
       "together to spite you! " x y))

(two-params "one" "two")
;; => "Two parameters! That's nothing! Pah! I will smoosh them together to spite you! onetwo"


;; multi-param functions

(defn x-chop
  "Describe the kind of chop you're inflicting on someone"
  ([name chop-type]
     (str "I " chop-type " chop " name "! Take that!"))
  ([name]
   (x-chop name "karate")))

(x-chop "Kanye West" "slap")
;; => "I slap chop Kanye West! Take that!"

(x-chop "Kanye West")
;; => "I karate chop Kanye West! Take that!"


;; bad idea, different types:

(defn weird-arity
  ([]
     "Destiny dressed you this morning, my friend, and now Fear is
     trying to pull off your pants. If you give up, if you give in,
     you're gonna end up naked with Fear just standing there laughing
     at your dangling unmentionables! - the Tick")
  ([number]
     (inc number)))


;; rest arguments are stored on a list

(defn codger-communication
  [whippersnapper]
  (str "Get off my lawn, " whippersnapper "!!!"))

(defn codger
  [& whippersnappers]
  (map codger-communication whippersnappers))

(codger "Billy" "Anne-Marie" "The Incredible Bulk")
;; ("Get off my lawn, Billy!!!"
;;  "Get off my lawn, Anne-Marie!!!"
;;  "Get off my lawn, The Incredible Bulk!!!")


;; you can mix rest f=parameters with regular parameters but the rest parameter
;; has to come last

(defn favorite-things
  [name & things]
  (str "Hi, " name ", here are my favorite things: "
       (clojure.string/join ", " things)))

(favorite-things "Doreen" "gum" "shoes" "kara-te")
;; => "Hi, Doreen, here are my favorite things: gum, shoes, kara-te"


;;; Destructuring

;; Return the first element of a collection
(defn my-first
  [[first-thing]] ; Notice that first-thing is within a vector
  first-thing)


(my-first ["oven" "bike" "war-axe"])
;; => "oven"


;; you can use as many named elements as you want
(defn chooser
  [[first-choice second-choice & unimportant-choices]]
  (println (str "Your first choice is: " first-choice))
  (println (str "Your second choice is: " second-choice))
  (println (str "We're ignoring the rest of your choices. "
                "Here they are in case you need to cry over them: "
                (clojure.string/join ", " unimportant-choices))))


(chooser ["Marmalade", "Handsome Jack", "Pigpen", "Aquaman"])
;; Your first choice is: Marmalade
;; Your second choice is: Handsome Jack
;;We're ignoring the rest of your choices. Here they are in case you need to cry over them: Pigpen, Aquaman

;; you can destructure maps

(defn announce-treasure-location
  [{lat :lat lng :lng}]
  (println (str "Treasure lat: " lat))
  (println (str "Treasure lng: " lng)))

(announce-treasure-location {:lat 28.22 :lng 81.33})

;; Treasure lat: 28.22q
;; Treasure lng: 81.33


(defn announce-treasure-location
  [{:keys [lat lng]}]
  (println (str "Treasure lat: " lat))
  (println (str "Treasure lng: " lng)))


;(defn receive-treasure-location
;  [{:keys [lat lng] :as treasure-location}]
;  (println (str "Treasure lat: " lat))
;  (println (str "Treasure lng: " lng))


;;; Function body

;; can contain any number of forms, the bodies value is the value of the last form.

(defn illustraive-function
  []
  (+ 1 304)
  30
  "joe")

(illustraive-function)
;; => "joe"



;; a function whose body is a if expression
(defn number-comment
  [x]
  (if (> x 6)
    "Oh my gosh! What a big number!"
    "That number's OK, I guess"))

(number-comment 5)
;; => "That number's OK, I guess"

(number-comment 7)
;; => "Oh my gosh! What a big number!"



;; All functions Are Created Equal
;; build in functions have no privilege over user defined functions


;;; Anonymous Functions

;; the fn form:
;(fn [parameter-list]
;  function body)

(fn [name] (str "Hi, " name))
;; => #function[clojure-noob.03-do-things/eval11780/fn--11781]

(map (fn [name] (str "Hi, " name))
     ["Darth Vader" "Mr. Magoo"])
;; => ("Hi, Darth Vader" "Hi, Mr. Magoo")


((fn [x] (* x 3)) 8)
;; => 24


(def my-special-multiplier (fn [x] (* x 3)))

(my-special-multiplier 12)
;; => 36


#(* % 3)
;; => #function[clojure-noob.03-do-things/eval11800/fn--11801]


(#(* % 3) 8)
;; => 24

(map #(str "Hi, " %)
     ["Darth Vader" "Mr. Magoo"])
;; => ("Hi, Darth Vader" "Hi, Mr. Magoo")


;; the anonymous function reader syntax is similar to a function call
;; with the % replacing one of the parameters

;; Function call
(* 8 3)

;; Anonymous function
#(* % 3)


;; multiple arguments

(#(str %1 " and " %2) "cornbread" "butter beans")
;; => "cornbread and butter beans"


;; rest parameters

(#(identity %&) 1 "blarg" :yip)
;; => (1 "blarg" :yip)



;;; Returning functions:

(defn inc-maker
  "Create a custom incrementor"
  [inc-by]
  #(+ % inc-by))

(def inc3 (inc-maker 3))

(inc3 7)
;; => 10



;;; Hobbits

(def asym-hobbit-body-parts [{:name "head" :size 3}
                             {:name "left-eye" :size 1}
                             {:name "left-ear" :size 1}
                             {:name "mouth" :size 1}
                             {:name "nose" :size 1}
                             {:name "neck" :size 2}
                             {:name "left-shoulder" :size 3}
                             {:name "left-upper-arm" :size 3}
                             {:name "chest" :size 10}
                             {:name "back" :size 10}
                             {:name "left-forearm" :size 3}
                             {:name "abdomen" :size 6}
                             {:name "left-kidney" :size 1}
                             {:name "left-hand" :size 2}
                             {:name "left-knee" :size 2}
                             {:name "left-thigh" :size 4}
                             {:name "left-lower-leg" :size 3}
                             {:name "left-achilles" :size 1}
                             {:name "left-foot" :size 2}])



(defn matching-part
  [part]
  {:name (clojure.string/replace (:name part) #"^left-" "right-")
   :size (:size part)})

(defn symmetrize-body-parts
  "Expects a seq of maps that have a :name and :size"
  [asym-body-parts]
  (loop [remaining-asym-parts asym-body-parts
         final-body-parts []]
    (if (empty? remaining-asym-parts)
      final-body-parts
      (let [[part & remaining] remaining-asym-parts]
        (recur remaining
               (into final-body-parts
                     (set [part (matching-part part)])))))))


(symmetrize-body-parts asym-hobbit-body-parts)
;; [{:name "head", :size 3}
;;  {:name "left-eye", :size 1}
;;  {:name "right-eye", :size 1}
;;  {:name "left-ear", :size 1}
;;  {:name "right-ear", :size 1}
;;  {:name "mouth", :size 1}
;;  {:name "nose", :size 1}
;;  {:name "neck", :size 2}
;;  {:name "left-shoulder", :size 3}
;;  {:name "right-shoulder", :size 3}
;;  {:name "right-upper-arm", :size 3}
;;  {:name "left-upper-arm", :size 3}
;;  {:name "chest", :size 10}
;;  {:name "back", :size 10}
;;  {:name "left-forearm", :size 3}
;;  {:name "right-forearm", :size 3}
;;  {:name "abdomen", :size 6}
;;  {:name "left-kidney", :size 1}
;;  {:name "right-kidney", :size 1}
;;  {:name "left-hand", :size 2}
;;  {:name "right-hand", :size 2}
;;  {:name "right-knee", :size 2}
;;  {:name "left-knee", :size 2}
;;  {:name "right-thigh", :size 4}
;;  {:name "left-thigh", :size 4}
;;  {:name "right-lower-leg", :size 3}
;;  {:name "left-lower-leg", :size 3}
;;  {:name "right-achilles", :size 1}
;;  {:name "left-achilles", :size 1}
;;  {:name "right-foot", :size 2}
;;  {:name "left-foot", :size 2}]


;; let - binds names to values

(let [x 3]
  x)
;; => 3

(def dalmation-list
  ["Pongo" "Perdita" "Puppy 1" "Puppy 2"])

(let [dalmations (take 2 dalmation-list)]
  dalmations)
;; => ("Pongo" "Perdita")


;; let introduces a new lexical scope

(def x 0)
(let [x 1] x)
;; => 1


(def x 1)
(let [x (inc x)] x)
;; => 1


;; let can contain rest parameters

(let [[pongo & dalmatians] dalmation-list]
  [pongo dalmatians])
;; => ["Pongo" ("Perdita" "Puppy 1" "Puppy 2")]


(into [] (set [:a :a]))
;; => [:a]


;;; loop - recursion in Clojure

(loop [iteration 0]
  (println (str "Iteration " iteration))
  (if (> iteration 3)
    (println "Goodbye!")
    (recur (inc iteration))))
;; Iteration 0
;; Iteration 1
;; Iteration 2
;; Iteration 3
;; Iteration 4
;; Goodbye!
;; => nil

;; doing the same thing with a function call
(defn recursive-printer
  ([]
   (recursive-printer 0))
  ([iteration]
   (println (str "Iteration " iteration))
   (if (> iteration 3)
     (println "Goodbye!")
     (recursive-printer (inc iteration)))))

(recursive-printer)
;; Iteration 0
;; Iteration 1
;; Iteration 2
;; Iteration 3
;; Iteration 4
;; Goodbye!
;; => nil


;; Regular Expressions

#"regular-expression"

(re-find #"^left-" "left-eye")
;; => "left-"

(re-find #"^left-" "cleft-chin")
;; => nil


(re-find #"^left-" "wongleblart")
;; => nil


(defn matching-part
  [part]
  {:name (clojure.string/replace (:name part) #"^left-" "right-")
   :size (:size part)})

(matching-part {:name "left-eye" :size 1})
;; => {:name "right-eye", :size 1}

(matching-part {:name "head" :size 3})
;; => {:name "head", :size 3}


;;; Better Symmetrizer with reduce

;; sum with reduce
(reduce + [1 2 3 4])
;; => 10


(defn my-reduce
  ([f initial coll]
   (loop [result initial
          remaining coll]
     (if (empty? remaining)
       result
       (recur (f result (first remaining)) (rest remaining)))))
  ([f [head & tail]]
   (my-reduce f head tail)))


(my-reduce + [1 2 3 4])
;; => 10


(defn better-symmetrize-body-parts
  "Expects a seq of maps that have a :name and :size"
  [asym-body-parts]
  (reduce (fn [final-body-parts part]
            (into final-body-parts (set [part (matching-part part)])))
          []
          asym-body-parts))


;;; Hobbit Violence

(defn hit
  [asym-body-parts]
  (let [sym-parts (better-symmetrize-body-parts asym-body-parts)
        body-part-size-sum (reduce + (map :size sym-parts))
        target (rand body-part-size-sum)]
    (loop [[part & remaining] sym-parts
           accumulated-size (:size part)]
      (if (> accumulated-size target)
        part
        (recur remaining (+ accumulated-size (:size (first remaining))))))))


(hit asym-hobbit-body-parts)
;; => {:name "left-upper-arm", :size 3}

(hit asym-hobbit-body-parts)
;; => {:name "abdomen", :size 6}

(hit asym-hobbit-body-parts)
;; => {:name "left-lower-leg", :size 3}


;; Exercises


;; 1 Use the str, vector, list, hash-map, and hash-set functions.

(str "Hello " "world!")
;; => "Hello world!"

(vector "Hello" "world")
;; => ["Hello" "world"]

(list "Hello" "world")
;; => ("Hello" "world")


(hash-map :a "hello" :b "world")
;; => {:b "world", :a "hello"}


(hash-set  "hello" "world" "hello")
;; => #{"hello" "world"}


;; 2. Write a function that takes a number and adds 100 to it.

(defn add-100
  "add 100 to a given number"
  [num]
  (+ num 100))

(add-100 0)
;; => 100


;; 3. Write a function, dec-maker, that works exactly like the function inc-maker
;; except with subtraction: 

(defn dec-maker
  "returns a function that subtracts a given constant"
  [const]
  #(- % const))

(def dec9 (dec-maker 9))
(dec9 10)
;; => 1


;; 4. Write a function, mapset, that works like map except the return value is a set:
(defn mapset
  "maps a function to a passed collection and returns the result as a set"
  [f coll]
  (into #{} (map f coll)))

(mapset inc [1 1 2 2])
;; => #{3 2}
