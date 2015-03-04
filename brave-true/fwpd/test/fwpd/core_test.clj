(ns fwpd.core-test
  (:require [midje.sweet :refer :all]
            [fwpd.core :refer :all]))

(def file (slurp filename))

(fact "test 'file'"
  file  =>
  "Name,Glitter Index\nEdward Cullen,10\nBella Swan,0\nCharlie Swan,0\nJacob Black,3\nCarlisle Cullen,6")

(fact "test 'str->int'"
  (str->int "-10") => -10
  (str->int "0") => 0
  (str->int "0010") => 10
  (str->int "999999999") => 999999999)

(fact "test 'parse'"
  (parse "0,10,100,200") =>
  '(["0" "10" "100" "200"])

  (parse "0,10,100,\"end\",:doggy")
  => '(["0" "10" "100" "\"end\"" ":doggy"])

  (parse "0,10,20,30\n40,50,60,70")
  => '(["0" "10" "20" "30"]
       ["40" "50" "60" "70"])

  (parse file)
  => '(["Name" "Glitter Index"]
       ["Edward Cullen" "10"]
       ["Bella Swan" "0"]
       ["Charlie Swan" "0"]
       ["Jacob Black" "3"]
       ["Carlisle Cullen" "6"]))

(fact "test mapify"
  (mapify (parse file))
  => '({:name "Edward Cullen", :glitter-index 10}
       {:name "Bella Swan", :glitter-index 0}
       {:name "Charlie Swan", :glitter-index 0}
       {:name "Jacob Black", :glitter-index 3}
       {:name "Carlisle Cullen", :glitter-index 6}))

(fact "test 'glitter-filter'"
  (let [records (mapify (parse file))]
    (glitter-filter 3 records))
  => '({:name "Edward Cullen", :glitter-index 10}
       {:name "Jacob Black", :glitter-index 3}
       {:name "Carlisle Cullen", :glitter-index 6}))


(fact "test 'list-of-names"
  (list-of-names (glitter-filter 3 (mapify (parse file))))
  => '("Edward Cullen" "Jacob Black" "Carlisle Cullen"))

(fact "test 'prepend"
  (prepend "Joe Blow" 5 (mapify (parse file)))
  => '({:name "Joe Blow" :glitter-index 5}
       {:name "Edward Cullen", :glitter-index 10}
       {:name "Bella Swan", :glitter-index 0}
       {:name "Charlie Swan", :glitter-index 0}
       {:name "Jacob Black", :glitter-index 3}
       {:name "Carlisle Cullen", :glitter-index 6}))

(fact "test 'validate?' against 'keywords'"
  (validate? keywords {:name "Joe Blow" :glitter-index 5}) => true
  (validate? keywords {:name "Joe Blow" :weight 180})      => false)

(fact "test 'add-record'"
  (let [good-record {:name "Joe Blow" :glitter-index 5}
        bad-record  {:name "Joe Blow" :weight 180}
        data        (mapify (parse file))]
    (add-record good-record data)
    => '({:name "Joe Blow" :glitter-index 5}
         {:name "Edward Cullen", :glitter-index 10}
         {:name "Bella Swan", :glitter-index 0}
         {:name "Charlie Swan", :glitter-index 0}
         {:name "Jacob Black", :glitter-index 3}
         {:name "Carlisle Cullen", :glitter-index 6})
    (add-record bad-record data) => data))
