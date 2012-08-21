(ns slick-game.controller
  (:require
   [slick-game.swing-game :as swing]
   [slick-game.falling :as falling]))

;; ## Utility functions

(defn get-time
  []
  (. System (nanoTime)))

;; stubbed logic for now
(defn game-logic
  [world delta]
  (falling/simulate-world world delta))

;; ## The game loop

(defn game-loop
  [init-world]
  (loop [world init-world
         last-time (get-time)]
    (let [curr-time (get-time)
          delta (/ (double (- curr-time last-time))
                   1000.0)
          world (game-logic world delta)]
      (swing/paint-world world)
      (when (swing/running?)
        (Thread/sleep 10)
        (recur world curr-time)))))

(defn start-game
  []
  (let [world {:falling [(falling/make-shape {:x 30 :y 30})]}]
    (do
      (swing/setup-swing "ZOMG Game!!11!")
      ;; game loop
      (game-loop world))))

;; ## Main method to run the game from the command line

(defn -main
  [& args]
  (do
    (start-game)
    (println "Game quit")
    (System/exit 0)))
