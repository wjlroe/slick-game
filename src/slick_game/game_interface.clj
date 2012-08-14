(ns slick-game.game-interface)

(defprotocol GAMEINTERFACE
  (init [interface game-name])
  (render [interface world]))
