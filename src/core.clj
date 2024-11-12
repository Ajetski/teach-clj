(ns core
  (:require
   [clojure.string :as str]))

;;; note, this is interactive. you can't just read. you must evaluate the forms below

;;; using the editor
;h-j-k-l to move
;gw then "aim" to jump to word
;<space>er to evaluate root form under cursor
;<space>lr to clear repl
;<space>lv to add vertically split repl pane
;gcc to toggle comments on a line

;;; cheatsheets
; https://devhints.io/vim
; https://clojure.org/api/cheatsheet

;;; data types

;null
nil

;bool
true
false

;int
1
-5

;float
5.5

;ratio
22/7

;string
"test"

;char
\a

;keyword, like strings that are only ever used as literals by the dev. thing "tags"
:something

;sybmols/idents (can resolve to values, fns, namespaces)
;mostly ignore for now
'+
'core

;vec
[1 "pizza" :pepperoni]

;set
#{4 5 6 7 8 9}

;list, the tick is to prevent evaluation
;LISP = list processor; aka lists get processsed as function calls
'(1 2 3)
(list 1 2 3) ;this is not a data literal, but we can call the list function and pass in args

;map
{:foo "bar"
 "test" true}

;fn literals (lambdas)
#(+ % 5) ;short-hand
#(+ %1 %2 5) ;short-hand w/ multiple-args (not usually recommended, use named args)
(fn [x y]
  (+ x y 5)) ;longer version, but more flexible/readible in complex situations

;;;; function calls
(inc 5)
(dec 5)
(+ 1 2)
(* 3 4)
(/ 4 2)
(mod 4 3)
(float 22/7) ;type conversions are done via function call
(println "Hello, World!")
(str "a" "b" "c")

;;; vars
(def data {:test true
           :foo "bar"
           :baz [1 2 3]
           #{1} #{2}})

;;; using generic data. no classes needed
(keys data)
(vals data)
(:baz data) ;keyword as function
(data :baz) ;map as function
(#{2 3} 5) ;set as fn
(#{2 3} 3) ;set as fn
;use std functions that operate on all collections
(get data #{1}) 
(map inc [1 2 3])
(filter even? [1 2 3])
(filter #{\a \e \i \o \u} "vowels") ; even works on strings
(reduce + [1 2 3 4])
(seq {:foo "bar"})
(concat [1 2] [3 4])
(conj [1 2 3] 4)
(conj {:a "b"} [:c "d"])
(conj (list 1 2 3) 4)

;;; threading macros
;macros are functions that re-write code for us at compile time
;we will get into that more later, this is a little taste of using macros to make
;code simpler to reason about. in this case we are going to remove nesting
(first (map inc (filter even? (:baz data))))
;same as:
(->> data
     :baz
     (filter even?)
     (map inc)
     first)
; thead-last (->>) typed out as "- > >" without spaces (i use font ligatures)
; thread-last will take an form (block of code), evaluate it, then pass that as
; the last argument to the next form
; for illustration purposes here, i've inserted ,,,, where the param would be inserted
; note that commas are whitespace in clojure
(->> data
     (:baz)
     (filter even?)
     (map inc)
     (first))

; thead-first works very similarly
(-> data
    (update ,,,, #{1} conj 3)
    (update ,,,, :foo #(str % "oque"))
    (dissoc ,,,, :test)
    (assoc ,,,, :version "2.0"))
;there are many more helper macros provided. we can also easily add more

;;; functions
(defn foo [a b]
  (+ a b 5))
(foo 1 2)
;defn is just a macro that expands to (def fn-name (fn [args] implementation))

;;; control flow
(if (<= 1
        (first (data #{1}))
        2)
  5
  6)
;clean "switch". no fall-through. linear evaluation, results in 1 value
;optional :else clause
(cond
  (< 5 3) 1
  (< 5 4) 2
  (< 5 5) 3
  (< 5 6) 4
  (< 5 7) 5
  :else :foo)
;plus more if you want, both in terms of macros control flow of code as well
;as dynamic dispatch systems for polymorphic behaviour

;bindings
(let [a 5
      b (+ a 1)
      c (+ b 1)]
  [a b c])
;destructuring
(let [{arr :baz} data ;grab value for :baz from data, name it `arr`
      [head & tail] arr ;grab first from `arr` and name it `head`, take rest of `arr` ...
      [_ b] tail] ;grab second elem of `tail` and name it `b`
  (concat tail (list :separator b)))

;can be used anywhere where bindings occur, not just `let`
(defn test-fn-bindings [{[head & tail :as arr] :baz}]
  (concat tail '(:separator) arr))
(test-fn-bindings data)

;;; useful macros
;do is usually for side effects
(do (println "hi")
    5)
;or can be used for nil coalesce (aka default values)
(or true
    (do (println "ERROR, you shouldn't see this")
        5)) 
(and false
     (do (println "ERROR, you shouldn't see this")
        5))
; similar to walrus operator from python
(if-let [some-val (->> (list 1 2 3 4 5)
                       (filter even?)
                       ; (filter #(< 5 %)) ;; toggle comment
                       not-empty)]
  (map inc some-val)
  "no value found")

;;; define our own macros
;clojure code is defined in terms of its own data structures. feel free to revisit
;the data types above. all code below is either parens (list), bindings (vec),
;idents/symbols, or other data literals like strings and numbers.
;this is why macros are essentially functions in clojure. we can write a function
;that takes in data, where that data represents some code. we can evaluate logic about
;that data and transform it freely using the same generic data manipulation functions
;that we learned about above

(defmacro first-macro
  "takes in a form, prints the form and adds 5 as the last param"
  [form]
  (println form)
  (concat form '(5)))

(first-macro (+ 1 2))
;macroexpand runs macro without evaluating result, useful when debugging while writing macros
(macroexpand '(first-macro (+ 1 2)))
(first-macro (println "test"))

;macros enable us to do arbitrary things in code. this can be good or bad. don't shoot
;yourself in the foot. such magic powers include:
;inventing domain specific langauges that reframe the problem and how to write solutions
;adding logic at compile time
;removing boilerplate

;beware of composibliity. functions nearly always compose well whereas composibliity
;of macros isn't guarenteed and must be evaluated during creation/adoption

;;; aoc day 1 2023
(def input "1abc2
pqr3stu8vwx
a1b2c3d4e5f
treb7uchet")
; get first and last digit to form a two-digit number, return sum
(->> input
     (str/split-lines)
     ; (map (fn [row] (filterv #(Character/isDigit %) row)))
     ; (map #(str (first %) (last %)))
     ; (map parse-long)
     ; (reduce +)
     ;
     )
