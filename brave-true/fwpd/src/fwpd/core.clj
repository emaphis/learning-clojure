(ns fwpd.core
  (:require [clojure.string :as s]
            [midje.sweet :refer :all]))


(def filename "../fwpd/suspects.csv")

;; Later on we're going to be converting each row in the CSV into a
;; map, like {:name "Edward Cullen" :glitter-index 10}.
;; Since CSV can't store Clojure keywords, we need to associate the
;; textual header from the CSV with the correct keyword.
(def headers->keywords {"Name" :name
                        "Glitter Index" :glitter-index})

(defn str->int
  [str]
  (Integer. str))

;; CSV is all text, but we're storing numberic data. So we
;; convet it back to actual numbers.
(def conversions {:name identity
                  :glitter-index str->int})

(defn parse
  "convert a csv into rows of columns"
  [string]
  (map #(s/split % #",")
       (s/split string #"\n")))

(defn mapify
  "Retrun a seq of maps like {:name \"Edward Cullen\" :glitter-indenx 10}"
  [rows]
  (let [;; headers becomes the seq (:anem :glitter-index)
        headers (map #(get headers->keywords %) (first rows))
        ;; unmapped-rows becomes the seq
        ;; (["Edward Cullen" "10"] ["Bella Swan" "0] ...)
        unmapped-rows (rest rows)]
    ;; Now let's return a seq of {:name "X" :glitter-index 10}
    (map (fn [unmapped-row]
           ;; We're going to use map to associate each header wit its
           ;; column. Since map returns a seq, we use "into" to convert
           ;; it into a map.
           (into {}
                 ;; notice we're passing multiple collection to map
                 (map (fn [header column]
                        ;; associate the header with the converted column
                        [header ((get conversions header) column)])
                      headers
                      unmapped-row)))
         unmapped-rows)))

(defn glitter-filter
  [minimum-glitter records]
  (filter #(>= (:glitter-index %) minimum-glitter) records))

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
