(ns clojure-noob.brews
  (require [midje.sweet :refer :all]))


(def order-details-validations
  {:name
   ["Please enter a name" not-empty]

   :email
   ["Please enter an email address" not-empty

    "Your email address doesn't look like an email address"
    #(or (empty? %) (re-seq #"@" %))]})

(def order-details
  {:name "Mitchard Blimmons"
   :email "mitchard.blimmonsgmail.com"})


(def shipping-details-validation
  {:name
   ["Please enter a name" not-empty]

   :address
   ["Please enter an address" not-empty]

   :city
   ["Please enter a city" not-empty]

   :postal-code
   ["Please enter a postal code" not-empty

    "Please enter a postal code that looks like a postal code"
    #(or (empty? %)
         (not (re-seq #"[^0-9-]" %)))]

   :email
   ["Please enter an email address" not-empty

    "Your email address doesn't look like an email address"
    (or #(empty? %)
        #(re-seq #"@" %))]})


(defn error-messages-for
  "return a seq of error messages"
  [to-validate message-validator-pairs]
  (map first (filter #(not ((second %) to-validate))
                     (partition 2 message-validator-pairs))))

(fact
  (error-messages-for "" ["Please enter a city" not-empty])
  => '("Please enter a city"))

(fact
  (error-messages-for "shine bright like a diamond"
                      ["Please enter a postal code" not-empty
                       "Please enter a postal code that looks like a US postal code"
                       #(or (empty? %)
                            (not (re-seq #"[^0-9-]" %)))])
  => '("Please enter a postal code that looks like a US postal code"))




(def shipping-details-validations
  {:name
   ["Please enter a name" not-empty]

   :address
   ["Please enter an address" not-empty]

   :city
   ["Please enter a city" not-empty]

   :postal-code
   ["Please enter a postal code" not-empty

    "Please enter a postal code that looks like a postal code"
    #(or (empty? %)
         (not (re-seq #"[^0-9-]" %)))]

   :email
   ["Please enter an email address" not-empty

    "Your email address doesn't look like an email address"
    (or #(empty? %)
        #(re-seq #"@" %))]})

(defn validate
  "returns a map with a vec of errors for each key"
  [to-validate validations]
  (reduce (fn [errors validation]
            (let [[fieldname validation-check-groups] validation
                  value (get to-validate fieldname)
                  error-messages (error-messages-for value validation-check-groups)]
              (if (empty? error-messages)
                errors
                (assoc errors fieldname error-messages))))
          {}
          validations))




(fact "test 'validate'"
  
  (validate {:address "123 Clinkenbeard Ct"})
  => {:name ["Please enter a name"]})

(fact
  (validate order-details order-details-validations)
  => {:email '("Your email address doesn't look like an email address")})


(def shipping-details
  {:name "Mitchard Blimmons"
   :address "134 Wonderment Ln"
   :city ""
   :state "FL"
   :postal-code "32501"
   :email "mitchard.blimmonsgmail.com"})

(fact "example of validate function"
  
  (validate shipping-details shipping-details-validations)
  =>
  {:email ["Your email address doesn't look like an email address"]
   :city ["Please enter a city"]} )


(comment "can be used like:"

  (defn render [field &] (println field))
         
  (let [errors (validate shipping-details shipping-details-validation)]
    (if (empty? errors)
      (render :success)
      (render :failure errors)))

  (let [errors (validate shipping-details shipping-details-validation)]
    (if (empty? errors)
      (do (save-shipping-details shipping-details)
          (redirect-to (url-for :order-confirmation)))
      (render "shipping-details" {:errors errors})))
  )

(defmacro if-valid
  "Handle validation more concisely"
  [to-validate validations errors-name & then-else]
  `(let [~errors-name (validate ~to-validate ~validations)]
     (if (empty? ~errors-name)
       ~@then-else)))


(comment

  (if-valid shipping-details shipping-details-validation errors
            (render :success)
            (render :failure errors))

  (if-valid shipping-details shipping-details-validation errors
            (do (save-shipping-details shipping-details)
                (redirect-to (url-for :order-confirmation)))
            (render "shipping-details" {:errors errors}))

  )



