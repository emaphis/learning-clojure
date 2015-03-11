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

(defn shuffle-speed
  [zombie]
  (* (:cuddle-hunger-level zombie)
     (- 100 (:percent-deteriorated zombie))))

(defn shuffle-alert
  [key watched old-state new-state]
  (let [sph (shuffle-speed new-state)]
    (if (> sph 5000)
      (do
        (println "Run, you fool!")
        (println "The zombie's SPH is now " sph)
        (println "This message brought to your courtesy of " key))
      (do
        (println "All's well with " key)
        (println "Cuddle hunger: " (:cuddle-hunger-level new-state))
        (println "Percent deteriorated: " (:percent-deteriorated new-state))
        (println "SPH: " sph)))))

;; General form of add-watch is (add-watch ref key watch-fn)
(add-watch fred :fred-shuffle-alert shuffle-alert)

(reset! fred {:cuddle-hunger-level 22
              :percent-deteriorated 2})

(fact
  (swap! fred update-in [:percent-deteriorated] + 1)
  ;; => All's well with  :fred-shuffle-alert
  ;; => Cuddle hunger:  22
  ;; => Percent deteriorated:  3
  ;; => SPH:  2134
  => {:percent-deteriorated 3, :cuddle-hunger-level 22}  )

(fact
  (swap! fred update-in [:cuddle-hunger-level] + 30)
  ;; => Run, you fool!
  ;; => The zombie's SPH is now  5044
  ;; => This message brought to your courtesy of  :fred-shuffle-alert
  => {:percent-deteriorated 3, :cuddle-hunger-level 52})


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; validators
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn percent-deteriorated-validator
  [{:keys [percent-deteriorated]}]
  (and (>= percent-deteriorated 0)
       (<= percent-deteriorated 100)))

(def bobby
  (atom
   {:cuddle-hunger-level 0 :percent-deteriorated 0}
   :validator percent-deteriorated-validator))

(fact "fail the validator"

  (swap! bobby update-in [:percent-deteriorated] + 200)
  => (throws IllegalStateException "Invalid reference state") )


;; throw an exception with a message

(defn percent-deteriorated-validator
  [{:keys [percent-deteriorated]}]
  (or (and (>= percent-deteriorated 0)
           (<= percent-deteriorated 100))
      (throw (IllegalStateException. "That's not mathy!"))))

(def bobby
  (atom
   {:cuddle-hunger-level 0 :percent-deteriorated 0}
   :validator percent-deteriorated-validator))

(fact "exception with custom message"

  (swap! bobby update-in [:percent-deteriorated] + 200)
  => (throws IllegalStateException "That's not mathy!"))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; refs

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; modeling sock transfers
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def sock-varieties
  #{"darned" "argyle" "wool" "horsehair" "mulleted"
    "passive-aggressive" "striped" "polka-dotted"
    "athletic" "business" "power" "invisible" "gollumed"})

(defn sock-count
  [sock-variety count]
  {:variety sock-variety
   :count count})

(defn generate-sock-gnome
  "Create an initial sock gnome state with no socks"
  [name]
  {:name name
   :socks #{}})

;; Here are our actual refs
(def sock-gnome (ref (generate-sock-gnome "Barumpharumph")))

(def dryer (ref {:name "LG 1337"
                 :socks (set (map #(sock-count % 2) sock-varieties))}))

(fact
  (:socks @dryer)
  =>
  #{{:variety "passive-aggressive", :count 2} {:variety "power", :count 2}
    {:variety "athletic", :count 2} {:variety "business", :count 2}
    {:variety "argyle", :count 2} {:variety "horsehair", :count 2}
    {:variety "gollumed", :count 2} {:variety "darned", :count 2}
    {:variety "polka-dotted", :count 2} {:variety "wool", :count 2}
    {:variety "mulleted", :count 2} {:variety "striped", :count 2}
    {:variety "invisible", :count 2}})

;; sock transfer
(defn steal-sock
  [gnome dryer]
  (dosync
   (when-let [pair (some #(if (= (:count %) 2) %) (:socks @dryer))]
     (let [updated-count (sock-count (:variety pair) 1)]
       (alter gnome update-in [:socks] conj updated-count)
       (alter dryer update-in [:socks] disj pair)
       (alter dryer update-in [:socks] conj updated-count)))))

(steal-sock sock-gnome dryer)

(fact "Your gnome may have stolen a different sock because socks are
        stored in an unordered set"

  (:socks @sock-gnome)
  => #{{:variety "gollumed", :count 1}})


;; Make sure all gollumed socks are accounted for
(defn similar-socks
  [target-sock sock-set]
  (filter #(= (:variety %) (:variety target-sock)) sock-set))

(fact
  (similar-socks (first (:socks @sock-gnome)) (:socks @dryer))
  => '({:variety "gollumed", :count 1}))


(comment
  "toy ref example:"

  (def counter (ref 0))
  (future
    (dosync
     (alter counter inc)
     (println @counter)
     (Thread/sleep 500)
     (alter counter inc)
     (println @counter)))
  (Thread/sleep 250)
  (println @counter)
  )


;; refs have one more trick up their suspiciously long sleeve:    :-)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; commute
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; alter:
;; 1 reach outside the transaction and read the ref's current state
;; 2 compare the current stat to the stat the ref started within the transaction
;; 3 if the two differ, make the entire transaction retry
;; 4 otherwise commit the altered ref state.

;; cummute:
;; 1 reach outside the transaction and read the ref's current state.
;; 2 run the commute function again using the current state.
;; 3 commit the result.

(defn sleep-print-update
  [sleep-time thread-name update-fn]
  (fn [state]
    (Thread/sleep sleep-time)
    (println (str thread-name ": " state))
    (update-fn state)))

(def counter (ref 0))
(future (dosync (commute counter (sleep-print-update 100 "Thread A" inc))))
(future (dosync (commute counter (sleep-print-update 150 "Thread B" inc))))

;;@counter


;; unsafe commute

(def receiver-a (ref #{}))
(def receiver-b (ref #{}))
(def giver (ref #{1}))

(future (dosync (let [gift (first (seq @giver))]
                  (Thread/sleep 10)
                  (commute receiver-a conj gift)
                  (commute giver disj gift))))

(future (dosync (let [gift (first (seq @giver))]
                  (Thread/sleep 15)
                  (commute receiver-b conj gift)
                  (commute giver disj gift))))
;; after 15 ms...
@receiver-a
; => #{1}

@receiver-b
; => #{1}

@giver
; => #{}


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; vars

;;;;;;;;;;;;;;;;;;;;;;;;
;; dynamic binding
;;;;;;;;;;;;;;;;;;;;;;;
;; association between symbols and objects - def

(def ^:dynamic *notification-address* "dobby@elf.org")

(fact "you can temporarily change the value of dynamic vars by using binding"

  (binding [*notification-address* "test@elf.org"]
  *notification-address*)
  => "test@elf.org")

(fact "You can also stack bindings, just like you can with let:"

(binding [*notification-address* "tester-1@elf.org"]
  (println *notification-address*)
  (binding [*notification-address* "tester-2@elf.org"]
    (println *notification-address*))
  (println *notification-address*))
;; prints:
;; => tester-1@elf.org
;; => tester-2@elf.org
;; => tester-1@elf.org
=> nil)

(println *notification-address*) ;; still the same


;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; dynamic var uses
;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn notify
  [message]
  (str "TO: " *notification-address* "\n"
       "MESSAGE: " message))

(fact "mocking:"

  (notify "I fell.")
  => "TO: dobby@elf.org\nMESSAGE: I fell." )

(fact "mock but don't send spam"

  (binding [*notification-address* "test@elf.org"]
  (notify "test!"))
  => "TO: test@elf.org\nMESSAGE: test!" )


(binding [*out* (clojure.java.io/writer "print-output")]
  (println "A man who carries a cat by the tail learns
something he can learn in no other way.
-- Mark Twain"))

(fact "read redirected output"
  (slurp "print-output")
  => "A man who carries a cat by the tail learns \nsomething he can learn in no other way.\n-- Mark Twain\n")
; => A man who carries a cat by the tail learns
; => something he can learn in no other way.
; => -- Mark Twain

;; using set!
(def ^:dynamic *troll-thought* nil)

(defn troll-riddle
  [your-answer]
  (let [number "man meat"]
    (when (thread-bound? #'*troll-thought*)
      (set! *troll-thought* number))
    (if (= number your-answer)
      "TROLL: You can cross the bridge!"
      "TROLL: Time to eat you, succulent human!")))


(fact "using set!:"

  (binding [*troll-thought* nil]
    (println (troll-riddle 2))
    (println "SUCCULENT HUMAN: Oooooh! The answer was" *troll-thought*))

  ;; => TROLL: Time to eat you, succulent human!
  ;; => SUCCULENT HUMAN: Oooooh! The answer was man meat
  => nil)

(fact  "The var returns to its original value outside of binding:"

  *troll-thought*
  => nil)

(fact "correct answer"

  (troll-riddle "man meat")
  => "TROLL: You can cross the bridge!")


;;;;;;;;;;;;;;;;;;;;;;;
;; per-thread binding
;;;;;;;;;;;;;;;;;;;;;;

;; the REPL
;; (binding [*out* repl-printr]
;;     your-code .....)

(fact " prints out to repl:"

  (.write *out* "prints out to repl")
  => nil)  ;; and it does.


(fact "doesn't print output to repl because *out* is not bound to repl printer:"

  (.start (Thread. #(.write *out* "prints to start out")))
  => nil )


(fact "You can work around this, though, with this goofy code:"

  (let [out *out*]
    (.start
     (Thread. #(binding [*out* out]
                 (.write *out* "prints to repl from thread")))))
  => nil )

;; bindings don't get passed to manually created threads.
;; they 'do' get passed to futures.  'binding conveyance'


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; altering the 'var' root
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; intial value is root:
(def power-source "hair")

;; Clojure lets you permanently change this root value with the function
;; 'alter-var-root':
(alter-var-root #'power-source (fn [_] "7-eleven parking lot"))

(fact
  power-source
  => "7-eleven parking lot")

;; You especially don't want to do this to perform simple variable assignment
;; in the same way you would in a language like Ruby or Javascript.

;; You can also temporarily alter a var's root with with-redefs. This works
;; similarly to binding, except the alteration will appear in child threads.
(fact "for example:"

  (with-redefs [*out* *out*]
    (doto (Thread. #(println "with redefs allows me to show up in the REPL"))
      .start
      .join))

;; with redefs allows me to show up in the REPL
  => anything)

;; agents are not covered.


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; stateless concurrency and parallelism
;;
;;;;;;;;;;;;;;;;;;;;;;;;
;; 'pmap'
;;;;;;;;;;;;;;;;;;;;;;;;

(defn always-1
  []
  1)

(fact "now take 5:"

  (take 5 (repeatedly always-1))
  => '(1 1 1 1 1) )

(fact "Lazy seq of random numbers between 0 and 9:"

  (take 5 (repeatedly (partial rand-int 10)))
  => anything
  ;;'(1 5 0 3 4)
  )


(def alphabet-length 26)

;; vector of chars, A-Z
(def letters (mapv (comp str char (partial + 65)) (range alphabet-length)))

(defn random-string
  "returns a random string of specified length"
  [length]
  (apply str (take length (repeatedly #(rand-nth letters)))))

(defn random-string-list
  [list-length string-length]
  (doall (take list-length (repeatedly (partial random-string string-length)))))

(def orc-names (random-string-list 3000 7000))

;; Use `dorun` to realize the lazy seq returned by map without
;; printing the results in the REPL
(time (dorun (map clojure.string/lower-case orc-names)))
;; => "Elapsed time: 270.182 msecs"
(time (dorun (pmap clojure.string/lower-case orc-names)))
;; => "Elapsed time: 147.562 msecs"

(def orc-name-abbrevs (random-string-list 20000 300))

(time (dorun (map clojure.string/lower-case orc-name-abbrevs)))
; => "Elapsed time: 78.23 msecs"
(time (dorun (pmap clojure.string/lower-case orc-name-abbrevs)))
; => "Elapsed time: 124.727 msecs"

;; fix with partition

(fact "partition-all takes a seq and divides it into seqs
       of the specified length:"

  (def numbers [1 2 3 4 5 6 7 8 9 10])

  (partition-all 3 numbers)
  => '((1 2 3) (4 5 6) (7 8 9) (10)) )

(fact "grain of 1:"

  (pmap inc numbers)
  => '(2 3 4 5 6 7 8 9 10 11) )

(fact "grain of 3:"

  (pmap (fn [number-group] (doall (map inc number-group)))
        (partition-all 3 numbers))
  => '((2 3 4) (5 6 7) (8 9 10) (11)) )

(fact "ungroup:"

  (apply concat
       (pmap (fn [number-group] (doall (map inc number-group)))
             (partition-all 3 numbers)))
  => '(2 3 4 5 6 7 8 9 10 11) )

(defn ppmap
  "Partitioned pmap, for grouping map ops together to make parallel
  overhead worthwhile"
  [grain-size f & colls]
  (apply concat
   (apply pmap
          (fn [& pgroups] (doall (apply map f pgroups)))
          (map (partial partition-all grain-size) colls))))

(time (dorun (ppmap 1000 clojure.string/lower-case orc-name-abbrevs)))x
; => "Elapsed time: 44.902 msecs"

;; fun.


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; clojure.core.reducers
;;
;;;;;;;;;;;;;;;;;;;;;;;;
;; parallel reduce
;;;;;;;;;;;;;;;;;;;;;;;;

(require '[clojure.core.reducers :as r])


;; vector consisting of 1 through 1000000
(def numbers (vec (range 1000000)))

(time (reduce + numbers))
"Elapsed time: 43.264 msecs"

(time (r/fold + numbers))
"Elapsed time: 23.145 msecs"

;; lazy seq consisting of 1 through 1000000
(def numbers (range 1000000))

(time (reduce + numbers))
"Elapsed time: 94.991 msecs"

(time (r/fold + numbers))
"Elapsed time: 95.237 msecs"

(facts "identity values:"

  (fact "addition"
    (+) => 0)

  (fact "multiplication"
    (*) => 1)

  (fact "strings"
    (str) => "") )

;; In general, you don't have anything to lose by using 'fold' instead of
;; 'reduce'. If a collection is foldable, you'll get a performance boost.
;; Otherwise, it's just like using 'reduce'.


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; faster collection functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def numbers (vec (range 1000000)))

(time (dorun (map inc numbers)))
"Elapsed time: 82.601 msecs"

(time (dorun (into [] (r/map inc numbers))))
"Elapsed time: 88.726 msecs"

;; Returns a "collection recipe"
(r/map inc numbers)

;; Returns a new "collection recipe"
(r/filter odd? (r/map inc numbers))

;; "Bakes" recipe
(reduce + (r/filter odd? (r/map inc numbers)))


;; Now let's look at the performance of r/map and r/filter used together:

(time (dorun (filter odd? (map inc numbers))))
"Elapsed time: 120.625 msecs"

(time (dorun (into [] (r/filter odd? (r/map inc numbers)))))
"Elapsed time: 93.937 msecs"


;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; summanry
;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;  Hah! assuming I learned all of that. :-)
