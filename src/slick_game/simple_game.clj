(ns slick-game.simple-game
  (:import
   [org.newdawn.slick AppGameContainer BasicGame GameContainer Graphics SlickException Image Input]))

(def land (ref nil))
(def plane (ref nil))
(def plane-x (ref (float 400)))
(def plane-y (ref (float 300)))
(def scale (ref (float 1.0)))

(defn middle-scale
  [value scale]
  (/ value
     (* 2 scale)))

(defn -main
  []
  (let [app (AppGameContainer. (proxy [BasicGame] ["SlickGame - SimpleGame from Clojure"]
                                 (init [gc]
                                   (dosync
                                    (ref-set land (Image. "data/land.jpg"))
                                    (ref-set plane (Image. "data/plane.png"))))
                                 (update [gc delta]
                                   (let [input (. gc getInput)]
                                     (cond
                                      (. input isKeyDown Input/KEY_A)
                                      (. @plane rotate (float (* -0.2 delta)))
                                      (. input isKeyDown Input/KEY_D)
                                      (. @plane rotate (float (* 0.2 delta)))
                                      (. input isKeyDown Input/KEY_W)
                                      (let [hip (float (* 0.4 delta))
                                            rotation (. @plane getRotation)]
                                        (dosync
                                         (ref-set plane-x (float (+ @plane-x (* hip (. Math sin (. Math toRadians rotation))))))
                                         (ref-set plane-y (float (- @plane-y (* hip (. Math cos (. Math toRadians rotation))))))))
                                      (. input isKeyDown Input/KEY_2)
                                      (dosync
                                       (ref-set scale (float
                                                       (+ @scale (if (>= @scale 5.0)
                                                                   0
                                                                   0.1))))
                                       (. @plane setCenterOfRotation
                                          (middle-scale (. @plane getWidth) @scale)
                                          (middle-scale (. @plane getHeight) @scale)))
                                      (. input isKeyDown Input/KEY_1)
                                      (dosync
                                       (ref-set scale (float
                                                       (- @scale (if (<= @scale 1.0)
                                                                   0
                                                                   0.1))))
                                       (. @plane setCenterOfRotation
                                          (middle-scale (. @plane getWidth) @scale)
                                          (middle-scale (. @plane getHeight) @scale))))))
                                 (render [gc g]
                                   (. @land draw 0 0)
                                   (. @plane draw @plane-x @plane-y @scale))))]
    (doto app
      (.setDisplayMode 800 600 false)
      (.start))))
