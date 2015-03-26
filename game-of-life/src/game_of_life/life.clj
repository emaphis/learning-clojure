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

;(pprint glider)
