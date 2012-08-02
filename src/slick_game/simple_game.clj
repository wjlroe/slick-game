(ns slick-game.simple-game
  (:import
   [org.newdawn.slick AppGameContainer BasicGame GameContainer Graphics SlickException Image]))

(def land (ref nil))

(defn -main
  []
  (let [app (AppGameContainer. (proxy [BasicGame] ["SlickGame - SimpleGame from Clojure"]
                                 (init [gc]
                                   (dosync
                                    (ref-set land (Image. "data/land.jpg"))))
                                 (update [gc delta]
                                   )
                                 (render [gc g]
                                   (. @land draw 0 0))))]
    (doto app
      (.setDisplayMode 800 600 false)
      (.start))))
