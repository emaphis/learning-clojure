(defproject clojure-programming "0.1.0"
  :description "My work for the text 'Clojure Programming' by Chas Emerick, Brian Carper, and Christopeh Grande"
  :url "http://github.com/emaphis/learning-clojure"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :profiles {:dev {:dependencies [[midje "1.6.3"]]
                   :plugins [[lein-midje "3.1.3"]]}}
  :documentation
  {:files
   {"chapter01"
    {:input "src/clojure_programming/chapter01.clj"
     :title "Clojure Programming Chapter 01" 
     :sub-title "Down the Rabbit Hole"
     :author "Ed Maphis"}

    "chapter02"
    {:input "src/clojure_programming/chp02.clj"
     :title "Clojure Programming Chapter 02" 
     :sub-title "Functional Programming"
     :author "Ed Maphis"}}}
  )
