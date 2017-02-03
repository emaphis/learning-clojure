(ns clojure-noob.05-functional-programming)

;;; pure functions should be referentially Transparent

;; referentially transparent
(defn wisdom
  [words]
  (str words ", Daniel-san"))

(wisdom "Always bathe on Fridays")
;; => "Always bathe on Fridays, Daniel-san"


;; NOT referentially transparent
(defn year-end-evaluation
  []
  (if (> (rand) 0.5)
    "You get a raise!"
    "Better luck next year!"))


(defn analysis
  [text]
  (str "Character count: " (count text)))

(defn analyze-file
  [filename]
  (analysis (slurp filename)))



;;; Living with immutable data structures

;; recursion instead of for/while

;; creating a sum
(def great-baby-name "Rosanthony")
great-baby-name
;; => "Rosanthony"


(let [great-baby-name "Bloodthunder"]
  great-baby-name) ; local definition
;; => "Bloodthunder"

great-baby-name
;; => "Rosanthony"


;; general solution
(defn sum
  ([vals] (sum vals 0))
  ([vals accum]
   (if (empty? vals)
     accum
     (sum (rest vals) (+ (first vals) accum)))))

;; using recur
(defn sum'
  ([vals] (sum vals 0))
  ([vals accum]
   (if (empty? vals)
     accum
     (recur (rest vals) (+ (first vals) accum)))))


;; use  nested function composition  instead of mutation

(require '[clojure.string :as s])

(defn clean
  [text]
  (s/replace (s/trim text) #"lol" "LOL"))

(clean "My boa constrictor is so sassy lol!  ")
;; => "My boa constrictor is so sassy LOL!"


;;; Cool things to do with pure functions


;; composing functions with comp

((comp inc *) 2 3)
;; => 7


;; character attributes function

(def character
  {:name "Smooches McCutes"
   :attributes {:intelligence 10
               :strength 4
               :dexterity 5}})

(def c-int (comp :intelligence :attributes))
(def c-str (comp :strength :attributes))
(def c-dex (comp :dexterity :attributes))

(c-int character)
;; => 10

(c-str character)
;; => 4


(c-dex character)
;; => 5


;; composition of multi-arg functions:

(defn spell-slots
  [char]
  (int (inc (/ (c-int char) 2))))

(spell-slots character)
;; => 6

;; using comp

(def spell-slots-comp (comp int inc #(/ % 2) c-int))


;; compose two functions
(defn two-comp
  [f g]
  (fn [& args]
    (f (apply g args))))


;;; memoize

(defn sleepy-identity
  "Returns the given value after 1 second"
  [x]
  (Thread/sleep 1000)
  x)

(sleepy-identity "Mr. Fantastico")
;; => "Mr. Fantastico"
;; after one second


(def memo-sleepy-identity (memoize sleepy-identity))

(memo-sleepy-identity "Mr. Fantastico")
;; => "Mr. Fantastico"
;; after one second

(memo-sleepy-identity "Mr. Fantastico")
;; => "Mr. Fantastico"
;; immediately

