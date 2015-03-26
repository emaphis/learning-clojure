(defproject game-of-life "0.1.0-SNAPSHOT"
  :description "An implementation of Conway's Game of Life from the text Clojure programming by Emerick, Carpenter, and Grande"
  :url "http://github.com/emaphis/learning-clojure/game-of-life"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :repl-options {:timeout 120000}
  :profiles {:dev {:dependencies [[midje "1.6.3"]]
                   :plugins [[lein-midje "3.1.3"]]}})
                             ;;[lein-midje-doc "0.0.24"])
