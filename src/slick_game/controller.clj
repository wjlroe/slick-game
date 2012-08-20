(ns slick-game.controller
  (:use
   slick-game.game-interface)
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
  world)

;; ## The game loop

(defn game-loop
  [interface world]
  (loop [last-time (get-time)]
    (let [curr-time (get-time)
          delta (- curr-time last-time)
          world (game-logic world delta)]
      (render interface world)
      (when (swing/running?)
        (Thread/sleep 10)
        (recur curr-time)))))

(defn start-game
  []
  (let [world {:falling [(falling/make-shape {:x 30 :y 30})]} ;; We need to create an actual game
        interface (swing/new-interface)]
    (do
      (init interface "ZOMG Game!!11!")
      ;; game loop
      (game-loop interface world))))

;; ## Main method to run the game from the command line

(defn -main
  [& args]
  (do
    (start-game)
    (println "Game quit")
    (System/exit 0)))
