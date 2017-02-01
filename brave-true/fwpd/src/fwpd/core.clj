(ns fwpd.core
  (:require [clojure.string :as s]
            [midje.sweet :refer :all]))


(def filename "../fwpd/suspects.csv")

(slurp filename)

;; Later on we're going to be converting each row in the CSV into a
;; map, like {:name "Edward Cullen" :glitter-index 10}.
;; Since CSV can't store Clojure keywords, we need to associate the
;; textual header from the CSV with the correct keyword.

(def vamp-keys [:name :glitter-index])

(defn str->int
  [str]
  (Integer. str))

;; CSV is all text, but we're storing numberic data. So we
;; convet it back to actual numbers.
(def conversions {:name identity
                  :glitter-index str->int})

(defn convert
  [vamp-key value]
  ((get conversions vamp-key) value))


;; test convert
;(convert :name "Henry")
;; => "Henry"
;(convert :glitter-index "3")
;; => 3


(defn parse
  "convert a csv into rows of columns"
  [string]
  (map #(s/split % #",")
       (s/split string #"\n")))

;; test parse
;(parse (slurp filename))
;; => (["Edward Cullen" "10"] ["Bella Swan" "0"] ["Charlie Swan" "0"]
;;     ["Jacob Black" "3"] ["Carlisle Cullen" "6"])


(defn mapify
  "Return a seq of maps like {:name \"Edward Cullen\" :glitter-indenx 10}"
  [rows]
  (map (fn [unmapped-row]
         (reduce (fn [row-map [vamp-key value]]
                   (assoc row-map vamp-key (convert vamp-key value)))
                 {}
                 (map vector vamp-keys unmapped-row)))
       rows))

;; test mapify
;;(first (mapify (parse (slurp filename))))
;; => {:name "Edward Cullen", :glitter-index 10}


(defn glitter-filter
  [minimum-glitter records]
  (filter #(>= (:glitter-index %) minimum-glitter) records))

;; test
(glitter-filter 3 (mapify (parse (slurp filename))))
;; => ({:name "Edward Cullen", :glitter-index 10}
;;     {:name "Jacob Black", :glitter-index 3}
;;     {:name "Carlisle Cullen", :glitter-index 6})



(defn list-of-names
  "return a list of names given a list of suspect maps"
  [data]
  (map :name data))

(defn prepend
  [name glitter-index data]
  (conj data {:name name :glitter-index glitter-index}))

(def keywords [:name :glitter-index])

(defn validate?
  [keywords record]
  (every? #(not (nil? %)) (map #(get record %) keywords)))

;; wow! two lambdas in one function

(defn add-record
  [rec data]
  (if (validate? keywords rec)
    (prepend (:name rec) (:glitter-index rec) data)
    data))



(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
