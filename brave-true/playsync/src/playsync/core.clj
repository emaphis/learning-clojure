(ns playsync.core
  (:require [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]]
            [midje.sweet :refer :all]))


(def echo-chan (chan))

(go (println (<! echo-chan)))

(fact "process that simply prints the meassage it receives:"
  (>!! echo-chan "ketchup")
  => true)

;;  you can create buffered channels:
(def echo-buffer (chan 2))

(fact
  (>!! echo-buffer "ketchup")
  => true)

(fact
  (>!! echo-buffer "ketchup")
  => true)

;;(fact
;;  (>!! echo-buffer "ketchup")
;;  => java.lang.TreadDeath) ;;blocks


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; go blocks, thread, blocking and parking

(def hi-chan (chan))

(doseq [n (range 1000)]
  (go (>! hi-chan (str "hi " n))))

;; when your process will take a long time before putting or taking, and for
;; those occasions you should use thread:
(thread (println (<!! echo-chan)))

(fact
  (>!! echo-chan "mustard")
  => true)
;; => mustard

(fact "When thread's process stops, the process's return value is put on
       the channel that thread returns:"
  (let [t (thread "chili")]
    (<!! t))
  => "chili")


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; the hot dog process
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn hotdog-machine
  []
  (let [in (chan)
        out (chan)]
    (go (<! in)
        (>! out "hotdog"))
    [in out]))


(fact "YES!, pocket lint:"

  (let [[in out] (hotdog-machine)]
    (>!! in "pocket lint")
    (<!! out))
  => "hotdog")


(defn hotdog-machine-v2
  [hotdog-count]
  (let [in (chan)
        out (chan)]
    (go (loop [hc hotdog-count]
          (if (> hc 0)
            (let [input (<! in)]
              (if (= 3 input)
                (do (>! out "hotdog")
                    (recur (dec hc)))
                (do (>! out "wilted lettuce")
                    (recur hc))))
            (do (close! in)
                (close! out)))))
    [in out]))


(fact "here we go:"

  (let [[in out] (hotdog-machine-v2 2)]  ;; two hotdogs
    (>!! in "pocket lint")
    (println (<!! out))     ;; wilted lettus

    (>!! in 3)
    (println (<!! out))     ;; a hotdog

    (>!! in 3)
    (println (<!! out))     ;; a hotdog

    (>!! in 3)
    (<!! out))    => nil)   ;; nut'in


(fact "a pipeline of processes:"

  (let [c1 (chan)
        c2 (chan)
        c3 (chan)]
    (go (>! c2 (clojure.string/upper-case (<! c1))))
    (go (>! c3 (clojure.string/reverse (<! c2))))
    (go (println (<! c3)))
    (>!! c1 "redrum"))
  ;; => MURDER
  => true)

;; States:  "ready to receive money"  "dispensed item"


;;;;;;;;;;;;;;;;;;;;;;;;;;
;; choice
;;;;;;;;;;;;;;;;;;;;;;;;;

;; alts!

(defn upload
  [headshot c]
  (go (Thread/sleep (rand 100))
      (>! c headshot)))

(let [c1 (chan)
      c2 (chan)
      c3 (chan)]
  (upload "serious.jpg" c1)
  (upload "fun.jpg" c2)
  (upload "sassy.jpg" c3)
  (let [[headshot channel] (alts!! [c1 c2 c3])]
    (println "Sending headshot notification for" headshot)))
; => Sending headshot notification for sassy.jpg


;; timeout, should succede 20% of the time
(let [c1 (chan)]
  (upload "serious.jpg" c1)
  (let [[headshot channel] (alts!! [c1 (timeout 20)])]
    (if headshot
      (println "Sending headshot notification for" headshot)
      (println "Timed out!"))))
;; => Timed out!

;; You can also use alts!! to specify "put" operations. To do that, put a
;; vector inside the vector you pass to alts, like this:
(fact
  (let [c1 (chan)
        c2 (chan)]
    (go (<! c2))
    (let [[value channel] (alts!! [c1 [c2 "put!"]])]
      (println value)
      (= channel c2)))
  ;; => true
  => true)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; queues
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn append-to-file
  [filename s]
  (spit filename s :append true))

(defn format-quote
  [quote]
  (str "=== BEGIN QUOTE ===\n" quote "=== END QUOTE ===\n\n"))

(defn random-quote
  []
  (format-quote (slurp "http://www.iheartquotes.com/api/v1/random")))

(defn snag-quotes
  [filename num-quotes]
  (let [c (chan)]
    (go (while true (append-to-file filename (<! c))))
    (dotimes [n num-quotes] (go (>! c (random-quote))))))

;; (snag-quotes "/tmp/quotes" 2)


;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; callbacks
;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn upper-caser
  [in]
  (let [out (chan)]
    (go (while true (>! out (clojure.string/upper-case (<! in)))))
    out))

(defn reverser
  [in]
  (let [out (chan)]
    (go (while true (>! out (clojure.string/reverse (<! in)))))
    out))

(defn printer
  [in]
  (go (while true (println (<! in)))))

(def in-chan (chan))
(def upper-caser-out (upper-caser in-chan))
(def reverser-out (reverser upper-caser-out))
(printer reverser-out)

(>!! in-chan "redrum")
; => MURDER

(>!! in-chan "repaid")
; => DIAPER
