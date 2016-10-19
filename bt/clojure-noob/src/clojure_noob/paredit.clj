(ns clojure-noob.paredit)

;; () -> round
;; {} -> opencurly
;; [] -> open square


;; typing string char inside a string creates an escape
"this is inside \"a\" string"

;; Deleting

;; C-d   -> sp-delelet-char
;; M-d   -> sp-kill-word
;; DEL   -> sp-backward-delete-char
;; M-DEL -> sp-backward-kill-word
;; C-k   -> sp-kill-hybrid-sexp

;;  (this is an sexp) (this will delete up (to the) ending par then stop now  )

;; Slurping and Barfing
;; C-)   -> sp-forward-slurp-sexp
;; C-}   -> sp-forward-barf-sexp
;; C-()  -> sp-backward-slurp-sexp
;; C-{}  -> sp-backward-barf-sexp
(tom dick hary ((foo bar baz quux zot)) alpha beta gama)


;; Structural Navigation
;; C-M-f  ->  sp-forward-sexp
;; C-M-b  ->  sp-backward-sexp
;; C-M-d  ->  sp-down-sexp
;; C-M-u  ->  sp-backward-up-sexp
;; C-M-p  ->  sp-backward-down-sexp
;; C-M-n  ->  sp-up-sexp
(def process-bags
  (comp
   (mapcatting unbundle-pallet)
   (filtering non-food?)
   (mapping label-heavy)))


;;; Splicing
;; <M-up>     ->  sp-splice-sexp-killing-backward
;; <M-down>   ->  sp-splice-sexp-killing-forward
;; M-s        ->  sp-splice-sexp
(def process-bags-2
  (comp
   (mapcatting "unbundle-pallet")
   (filtering non-food?)
   ((some-fn1 some-fn2 some-fn3) (mapping label-heavy))))


;;; Splitting and Joining
;; M-S    ->  sp-split-sexp
;; nk     ->  sp-join-sexp 
(println "Thanks for reading and")
(println "and enjoy Paredit")

