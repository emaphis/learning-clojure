(ns the-divine-cheese-code.visualization.svg
  (:require [clojure.string :as s])
  (:refer-clojure :exclude [min max]))

;; ~~~2~~~
(defn comparator-over-maps
  [comparison-fn keys]
  (fn [maps]
    ;; ~~~2.3~~~
    (reduce (fn [result current-map]
              ;; ~~~2.2~~~
              (reduce merge
                      ;; ~~~2.1~~~
                      (map (fn [key]
                             {key (comparison-fn (key result) (key current-map))})
                           keys)))
            maps)))

;; ~~~3~~~
(def min-1 (comparator-over-maps clojure.core/min [:lat :lng]))
(def max-1 (comparator-over-maps clojure.core/max [:lat :lng]))

;; ~~~4~~~
(defn translate-to-00
  [locations]
  (let [mincoords (min-1 locations)]
    (map #(merge-with - % mincoords) locations)))

;; ~~~5~~~
(defn scale
  [width height locations]
  (let [maxcoords (max-1 locations)
        ratio {:lat (/ height (:lat maxcoords))
               :lng (/ width (:lng maxcoords))}]
    (map #(merge-with * % ratio) locations)))


(defn latlng->point
  "Convert lat/lng map to comma-separated string"
  [latlng]
  (str (:lat latlng) "," (:lng latlng)))

(defn points
  [locations]
  (s/join " " (map latlng->point locations)))


