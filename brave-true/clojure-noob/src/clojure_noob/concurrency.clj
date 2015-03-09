(ns clojure-noob.concurrency
  (require [midje.sweet :refer :all]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; futures
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;(future (Thread/sleep 4000)
;;        (println "I'll print after 4 seconds"))
;;(println "I'll print immediately")

;; Futures differ from the values you're used to, like hashes and maps,
;; in that you have to dereference them to obtain their value


(fact "dereference a future"

  (let [result (future (println "this prints once")
                       (+ 1 1))]
    (deref result) => 2
    @result => 2))

(fact "time limit:"

  (deref (future (Thread/sleep 1000) 0) 10 5)
  => 5)

(fact "interrogate a future to see if it's done running wit 'realized?'"
  
  (realized? (future (Thread/sleep 1000)))
  => false

  (let [f (future)]
    @f
    (realized? f))
  => true )


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; dereferencing
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; delays
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def jackson-5-delay
      (delay (let [message "Just call my name and I'll be there"]
               (println "First defef:" message)
               message)))


(fact "deref the delay"

  (force jackson-5-delay)
;; First defef: Just call my name and I'll be there
  => "Just call my name and I'll be there" )


(fact "Like futures, a delay is only run once and its result is cached"

  @jackson-5-delay
  => "Just call my name and I'll be there" )


(def gimli-headshots ["serious.jpg" "fun.jpg" "playful.jpg"])

(defn email-user
  [email-address]
  (println "Sending headshot notification to" email-address))

(defn upload-document
  "Needs to be implemented"
  [headshot]
  true)


(fact "sending email"

  (let [notify (delay (email-user "and-my-axe@gmail.com"))]
    (doseq [headshot gimli-headshots]
      (future (upload-document headshot)
              (force notify))))
  ;; Sending headshot notification to and-my-axe@gmail.com
  => nil)


;;;;;;;;;;;;;;;;;;;;;;;
;; promises
;;;;;;;;;;;;;;;;;;;;;;;

(fact "promises"

  (def my-promise (promise))
  (deliver my-promise (+ 1 2))
  @my-promise
  => 3 )


;; yak butter

(def yak-butter-international
  {:store "Yak Butter International"
    :price 90
    :smoothness 90})

(def butter-than-nothing
  {:store "Butter than Nothing"
   :price 150
   :smoothness 83})

;; This is the butter that meets our requirements
(def baby-got-yak
  {:store "Baby Got Yak"
   :price 94
   :smoothness 99})

(defn mock-api-call
  [result]
  (Thread/sleep 1000)
  result)

(defn satisfactory?
  "If the butter meets our criteria, return the butter, else return false"
  [butter]
  (and (<= (:price butter) 100)
       (>= (:smoothness butter) 97)
       butter))


(fact "'some' applies a function to each element of a collection and 
        rturns the first truthy result"

  (some odd? [2 3 4 5])
  => true

  ;; return the value which has the truthy result
  (some #(and (odd? %) %) [2 3 4 5])
  => 3 )

(fact "check each site synchronously"
  
  (time (some (comp satisfactory? mock-api-call)
              [yak-butter-international butter-than-nothing baby-got-yak]))
;; => "Elapsed time: 3002.132 msecs"
  => {:store "Baby Got Yak", :smoothness 99, :price 94})

