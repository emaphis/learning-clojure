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


(fact "using promises and futures to run asynchronously on more than one core"

  (time
   (let [butter-promise (promise)]
     (doseq [butter [yak-butter-international butter-than-nothing baby-got-yak]]
       (future (if-let [satisfactory-butter (satisfactory? (mock-api-call butter))]
                 (deliver butter-promise satisfactory-butter))))
     (println "And the winner is:" @butter-promise)))

;; => And the winner is: {:store Baby Got Yak, :smoothness 99, :price 94}
;; => "Elapsed time: 1002.652 msecs"
  => nil)


(fact "registering callbacks "

  (let [ferengi-wisdom-promise (promise)]
    (future (println "Here's some Ferengi wisdom:" @ferengi-wisdom-promise))
    (Thread/sleep 100)
    (deliver ferengi-wisdom-promise "Whisper your way to success."))
;; => Here's some Ferengi wisdom: Whisper your way to success.
  => anything)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; simple queueing
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn append-to-file
  [filename s]
  (spit filename s :append true))

(defn format-quote
  [quote]
  (str "=== BEGIN QUOTE ===\n" quote "=== END QUOTE ===\n\n"))

(defn snag-quotes
  [n filename]
  (dotimes [_ n]
    (->> (slurp "http://www.iheartquotes.com/api/v1/random")
         format-quote
         (append-to-file filename)
         (future))))

;;(snag-quotes 2 "quotes.txt")



(defmacro enqueue
  [q concurrent-promise-name & work]
  (let [concurrent (butlast work)
        serialized (last work)]
    `(let [~concurrent-promise-name (promise)]
       (future (deliver ~concurrent-promise-name (do ~@concurrent)))
       (deref ~q)
       ~serialized
       ~concurrent-promise-name)))

(defmacro wait
  "Sleep `timeout` seconds before evaluating body"
  [timeout & body]
  `(do (Thread/sleep ~timeout) ~@body))

(fact "out of order"

  (future (wait 200 (println "'Ello, gov'na!")))
  (future (wait 400 (println "Pip pip!")))
  (future (wait 100 (println "Cheerio!")))
;; => Cheerio!
;; => 'Ello, gov'na!
;; => Pip pip!
  => anything)

(fact "using enqueue"
  (time @(-> (future (wait 200 (println "'Ello, gov'na!")))
             (enqueue saying (wait 400 "Pip pip!") (println @saying))
             (enqueue saying (wait 100 "Cheerio!") (println @saying))))
;;'Ello, gov'na!
;; Pip pip!
;; Cheerio!
;; "Elapsed time: 409.443334 msecs"
  => "Cheerio!")

;; new version

(defn append-to-file
  [filename s]
  (spit filename s :append true))

(defn format-quote
  [quote]
  (str "=== BEGIN QUOTE ===\n" quote "=== END QUOTE ===\n\n"))

(defn random-quote
  []
  (format-quote (slurp "http://www.iheartquotes.com/api/v1/random")))

(defmacro snag-quotes-queued
  [n filename]
  (let [quote-gensym (gensym)
        queue `(enqueue ~quote-gensym
                        (random-quote)
                        (append-to-file ~filename @~quote-gensym))]
    `(-> (future)
         ~@(take n (repeat queue)))))

;;(macroexpand (snag-quotes-queued 4 "quotes.txt"))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; escaping the pit of evil
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; cuddle zombies

;; oo-state

;; clojure - immutable state.   identity -> states


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; atoms
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def fred (atom {:cuddle-hunger-level 0
                 :percent-deteriorated 0}))

;; futures, delays, promises, atoms
;; atoms never block.

(fact "Fred's currnet state:"

  @fred
  => {:cuddle-hunger-level 0, :percent-deteriorated 0})


(fact "how to 'log' a zombie state:"

  (let [zombie-state @fred]
    (if (>= (:percent-deteriorated zombie-state) 50)
      (future (println (:percent-deteriorated zombie-state)))))
  => nil)


(fact "increase fred's cuddle humger"

  (swap! fred
       (fn [current-state]
         (merge-with + current-state {:cuddle-hunger-level 1})))
  => {:cuddle-hunger-level 1, :percent-deteriorated 0} )


(fact "dereferencing fred:"

  @fred
  => {:cuddle-hunger-level 1, :percent-deteriorated 0} )


(fact "update both 'attributes' at the same time:"

  (swap! fred
       (fn [current-state]
         (merge-with + current-state {:cuddle-hunger-level 1
                                      :percent-deteriorated 1})))
  => {:cuddle-hunger-level 2, :percent-deteriorated 1} )



(defn increase-cuddle-hunger-level
  [zombie-state increase-by]
  (merge-with + zombie-state {:cuddle-hunger-level increase-by}))


(fact "doesn't updat state returns a new state - a function"

  (increase-cuddle-hunger-level @fred 10)
  => {:cuddle-hunger-level 12, :percent-deteriorated 1}  )


(fact "now call 'swap!' with the additional arguments"

  (swap! fred increase-cuddle-hunger-level 10)
  => {:cuddle-hunger-level 12, :percent-deteriorated 1}

  @fred
  => {:cuddle-hunger-level 12, :percent-deteriorated 1} )


(fact "examples of 'update-in'"

  (update-in {:a {:b 3}} [:a :b] inc)
  => {:a {:b 4}}

  (update-in {:a {:b 3}} [:a :b] + 10)
  => {:a {:b 13}} )


(fact "use 'update-in' to change Fred's state:"

  (swap! fred update-in [:cuddle-hunger-level] + 10)
  => {:cuddle-hunger-level 22, :percent-deteriorated 1} )

;; no swap!s ever get lost!


(fact "apply serum with reset!:"

  (reset! fred {:cuddle-hunger-level 0
                :percent-deteriorated 0})
  => {:cuddle-hunger-level 0 :percent-deteriorated 0})


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; watches and validators
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
