(ns slick-game.controller
  (:require [slick-game.swing-game :as swing]))

;; ## Utility functions

(defn get-time
  []
  (. System (nanoTime)))

;; stubbed logic for now
(defn game-logic
  [world delta]
  world)

;; ## The game loop

(defn game-loop
  [interface world]
  (loop [last-time (get-time)]
    (let [curr-time (get-time)
          delta (- curr-time last-time)
          world (game-logic world delta)]
      (swing/render interface world)
      (when (swing/running?)
        (Thread/sleep 10)
        (recur curr-time)))))

(defn start-game
  []
  (let [world {} ;; We need to create an actual game
        interface (swing/new-interface)]
    (do
      (swing/init interface "ZOMG Game!!11!")
      ;; game loop
      (game-loop interface world))))

;; ## Main method to run the game from the command line

(defn -main
  [& args]
  (start-game))
