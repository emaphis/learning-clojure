(ns clojure-noob.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "I'm a little teapot!"))

(println "Cleanliness is next to godliness")

(defn train
  []
  (println "Choo choo!"))

(+ 1 (+) 2 3 4)
;; (+ 1 (* 2 3) 4)

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

;; empty map
{}

;; :a :b :c are keywords
{:a 1
 :b "boring example"
 :c []}

;; Maps can be nested
{:name {:first "John" :middle "Jacob" :last "Jingleheimerscmidt"}}

(get {:a 0 :b 1} :b)

(get {:a 0 :b {:c "ho hum"}} :b)

;; default value
(get {:a 0 :b 1} :c "UNICORNS")

;; get-in for nested maps
(get-in {:a 0 :b {:c "ho hum"}} [:b :c])

;; keys can act as lookup functions
({:name "The Human Coffe Pot" :occupation "Brewing"} :name)


;; inc by one:
(inc 1.1)

(map inc [0 1 2 3 4])

(defn too-enthusiastic
  "Return a cheer that might be a bit too enthusiastic"
  [name]
  (str "OH. MY. GOD! " name " YOU ARE MOST DEFINITELY LIKE THE BEST "
       "MAN SLASH WOMAN EVER I LOVE YOU AND WE SHOULD RUN AWAY TO SOMEWHERE"))


;; Parameters
(defn no-params
  []
  "I take no parameters!")

(defn one-param
  [x]
  (str "I take one param: " x " It'd better be a string!"))

(defn two-params
  [x y]
  (str "Two Parametes! That's nothing! Pah! I will smoosh them "))


(defn codger-communication
  [whippersnapper]
  (str "Get off my lawn, " whippersnapper "!!!"))

(defn codger
  [& whippersnappers]   ; the ampersand idecates the "rest-param"
  (map codger-communication whippersnappers))

;; (codger "Billy" "Anne-Marie" "The Incredible Bulk")


;; mixed rest params
(defn favorite-things
  [name & things]
  (str "Hi, " name ", hera are my favorite things: "
       (clojure.string/join ", " things)))

;;(favorite-things "Doreen" "gum" "shoes" "kara-te")


;; Destructuring

;; Return the first element of a collection
(defn my-first
  [[first-thing]]  ; Notice the first-thing is within a vector
  first-thing)

;; (my-first ["oven" "bike" "waraxe"])

;; without destructuring
(defn my-other-first
  [collection]
  (first collection))

;;(my-other-first ["nickle" "hair"])


(defn my-rest
  [[first-thing & rest-things]]
  rest-things)

;; (my-rest ["oven" "bike" "waraxe"])

;;; destructuring maps
(defn announce-trasure-location
  [{lat :lat lng :lng}]
  (println (str "Treasure lat: " lat))
  (println (str "Treasure lng: " lng)))

;;(announce-trasure-location {:lat 28.22 :lng 81.33})

;; anonymous functions
(map (fn [name] (str "Hi, " name))
     ["Darth Vader" "Mr. Magoo"])

;;((fn [x] (* x 3)) 8)

(def my-special-multiplier (fn [x] (* x 3)))

;;(my-special-multiplier 12)

;; #(* % 3)  ; multiply by three

;; (#(* %  3) 8)

;;(map #(str "Hi, " %)
;;     ["Darth Vader" "Mr. Magoo"])

(#(str %1 " and " %2) "corn bread" "butter beans")

;; a rest param:
(#(identity %&) 1 "blarg" :yip)

;; functions can return funtions:
(defn inc-maker
  "Create a cutom incrementor"
  [inc-by]
  #(+ % inc-by))

(def inc3 (inc-maker 3))

;;(inc3 7)
