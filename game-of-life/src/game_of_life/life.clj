(ns game-of-life.life)

;; the board will be a vector of vectors each item bing either :on or nil

(defn empty-board
  "Creates a rectangular empty board of the specified width
  and height."
  [w h]
  (vec (repeat w (vec (repeat h nil)))))


(defn populate
  "Turns :on each of the cells specified as [y, x] coordinates."
  [board living-cells]
  (reduce (fn [board coordinates]
            (assoc-in board coordinates :on))
          board
          living-cells))

(def glider (populate (empty-board 6 6) #{[2 0] [2 1] [2 2] [1 2] [0 1]}) )

;;(pprint glider)

;;Example 3-6. Implementation and helpers for indexed-step
(defn neighbours
  [[x y]]
  (for [dx [-1 0 1] dy [-1 0 1] :when (not= 0 dx dy)]
    [(+ dx x) (+ dy y)]))

(defn count-neighbours
  [board loc]
  (count (filter #(get-in board %) (neighbours loc)))) ; will not throw errros

(defn indexed-step
  "Yields the next state of the board, using indices to determine neighbors,
  liveness, etc."
  [board]
  (let [w (count board)
        h (count (first board))]
    (loop [new-board board x 0 y 0]
      (cond
        (>= x w) new-board
        (>= y h) (recur new-board (inc x) 0)
        :else
        (let [new-liveness
              (case (count-neighbours board [x y])
                2 (get-in board [x y])
                3 :on
                nil)]
          (recur (assoc-in new-board [x y] new-liveness) x (inc y)))))))

;;(-> (iterate indexed-step glider)(nth 8) pprint)

;; a version that avoids indices. This will help to get rid of manual iteration
;; each 'loop' is replaced by a reduce over a range.
(defn indexed-step2
  [board]
  (let [w (count board)
        h (count (first board))]
    (reduce 
      (fn [new-board x]
        (reduce 
          (fn [new-board y]
            (let [new-liveness
                   (case (count-neighbours board [x y])
                     2 (get-in board [x y])
                     3 :on
                     nil)]
              (assoc-in new-board [x y] new-liveness)))
          new-board (range h)))
      board (range w))))


;; nested reductions can be collapsed to make simpler code that is loopless:
(defn indexed-step3
  [board]
  (let [w (count board)
        h (count (first board))]
    (reduce 
      (fn [new-board [x y]]
        (let [new-liveness
               (case (count-neighbours board [x y])
                 2 (get-in board [x y])
                 3 :on
                 nil)]
           (assoc-in new-board [x y] new-liveness)))
      board (for [x (range h) y (range w)] [x y]))))

;; using partition can get rid of one demension of indices:
(partition 3 1 (range 5))
;;=> '((0 1 2)(1 2 3)(2 3 4))

;; partition with filled in neighbors:
(partition 3 1 (concat [nil] (range 5) [nil]))
;;=> '((nil 0 1) (0 1 2) (1 2 3) (2 3 4) (3 4 n))

;; now use this in a window function: 
(defn window
  "Returns a lazy sequence of 3-item windows centered
   around each item of coll, padded as necessary with
   pad or nil."
  ([coll] (window nil coll))
  ([pad coll]
   (partition 3 1 (concat [pad] coll [pad]))))


;; using the window function to create transpositons:
(defn cell-block 
  "Creates a sequences of 3x3 windows from a triple of 3 sequences."
  [[left mid right]]
  (window (map vector left mid right))) 

;; calculating liveness using destructuring to separate a cell block into
;; it's parts
(defn liveness
  "Returns the liveness (nil or :on) of the center cell for
   the next step."  
  [block]
  (let [[_ [_ center _] _] block]
    (case (- (count (filter #{:on} (apply concat block)))
             (if (= :on center) 1 0))
      2 center
      3 :on
      nil)))

;; the new idexed-step function depending on index free helper functions:
(defn- step-row
  "Yields the next state of the center row."
  [rows-triple]
  (vec (map liveness (cell-block rows-triple)))) 

(defn index-free-step
  "Yields the next state of the board."
  [board]
  (vec (map step-row (window (repeat nil) board))))

;; compare indexed-step the index-free:
(- (nth (iterate indexed-step glider) 8)
   (nth (iterate index-free-step glider) 8))
;;=> true or should be:

;; only the world of living cells is saved as state. we only have to count
;; the neighboring living cells.
(defn step 
 "Yields the next state of the world"
 [cells]
 (set (for [[loc n] (frequencies (mapcat neighbours cells))
            :when (or (= n 3) (and (= n 2) (cells loc)))]
        loc)))

(->> (iterate step #{[2 0[ [2 1] [2 2] [1 2] [0 1]]]
                     (drop 8)
                     first
                     (populate (empty-board 6 6))
                     clojure.pprint/ppring}))

(defn stepper 
  "Returns a step function for Life-like cell automata.
   neighbours takes a location and return a sequential collection
   of locations. survive? and birth? are predicates on the number
   of living neighbours."
  [neighbours birth? survive?] 
  (fn [cells]
    (set (for [[loc n] (frequencies (mapcat neighbours cells))
               :when (if (cells loc) (survive? n) (birth? n))]
           loc))))

(defn hex-neighbours
  [[x y]]
  (for [dx [-1 0 1] dy (if (zero? dx) [-2 2] [-1 1])] 
    [(+ dx x) (+ dy y)]))

(def hex-step (stepper hex-neighbours #{2} #{3 4}))

;= ; this configuration is an oscillator of period 4
(hex-step #{[0 0] [1 1] [1 3] [0 4]})
;= #{[1 -1] [2 2] [1 5]}
(hex-step *1)
;= #{[1 1] [2 4] [1 3] [2 0]}
(hex-step *1)
;= #{[1 -1] [0 2] [1 5]}
(hex-step *1)
;= #{[0 0] [1 1] [1 3] [0 4]}

(defn rect-stepper 
  "Returns a step function for standard game of life on a (bounded) rectangular
   board of specified size."
  [w h]
  (stepper #(filter (fn [[i j]] (and (< -1 i w) (< -1 j h))) 
                    (neighbours %)) #{2 3} #{3}))

(defn draw
  [w h step cells]
  (let [state (atom cells)
        run (atom true)
        listener (proxy [java.awt.event.WindowAdapter] []
                   (windowClosing [_] (reset! run false)))
        pane
          (doto (proxy [javax.swing.JPanel] []
                  (paintComponent [^java.awt.Graphics g]
                    (let [g (doto ^java.awt.Graphics2D (.create g)
                              (.setColor java.awt.Color/BLACK)
                              (.fillRect 0 0 (* w 10) (* h 10))
                              (.setColor java.awt.Color/WHITE))]
                      (doseq [[x y] @state]
                        (.fillRect g (inc (* 10 x)) (inc (* 10 y)) 8 8)))))
            (.setPreferredSize (java.awt.Dimension. (* 10 w) (* 10 h))))] 
    (doto (javax.swing.JFrame. "Quad Life")
      (.setContentPane pane)
      (.addWindowListener listener)
      .pack
      (.setVisible true))
    (future (while @run
              (Thread/sleep 80)
              (swap! state step)
              (.repaint pane)))))

(defn rect-demo []
  (draw 30 30 (rect-stepper 30 30) 
      #{[15 15] [15 17] [16 16] [15 16]}))
