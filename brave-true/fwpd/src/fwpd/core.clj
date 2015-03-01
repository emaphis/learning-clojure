(ns fwpd.core
  (:require [clojure.string :as s]
            [midje.sweet :refer :all]))

;;(def filename  "../../../suspects.csv")
(def filename "/home/emaphis/src/clojure/fwpd/suspects.csv")

;; Later on we're going to be converting each row in the CSV into a
;; map, like {:name "Edward Cullen" :glitter-index 10}.
;; Since CSV can't store Clojure keywords, we need to associate the
;; textual header from the CSV with the correct keyword.
(def headers->keywords {"Name" :name
                        "Glitter Index" :glitter-index})

(defn str->int
  [str]
  (Integer. str))

;; CSV is all text, but we're sotring numberic data. So we
;; convet it to actual numbers.
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
  [data]
  (map :name data))

(fact (list-of-names (glitter-filter 3 (mapify (parse (slurp filename)))))
  => '("Edward Cullen" "Jacob Black" "Carlisle Cullen"))


(defn prepend
  [name glitter-index lst]
  (conj lst [:name name :glitter-index glitter-index]))

(fact (prepend "Joe Blow" 5 (mapify (parse (slurp filename))))
  => '([:name "Joe Blow" :glitter-index 5]
       {:name "Edward Cullen", :glitter-index 10}
       {:name "Bella Swan", :glitter-index 0}
       {:name "Charlie Swan", :glitter-index 0}
       {:name "Jacob Black", :glitter-index 3}
       {:name "Carlisle Cullen", :glitter-index 6}))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
