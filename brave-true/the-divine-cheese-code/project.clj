(defproject the-divine-cheese-code "0.1.0-SNAPSHOT"
  :description "Soving the divine cheese caper"
  :url "www.github.com/emapis/learning-clojures/brave-true/the-divine-cheese-code"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :main ^:skip-aot the-divine-cheese-code.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
              :dev {:dependencies [[midje "1.6.3"]]}})
