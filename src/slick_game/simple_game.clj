(ns slick-game.simple-game
  (:import
   [org.newdawn.slick
    AppGameContainer
    BasicGame
    GameContainer
    Graphics
    SlickException
    Image
    Input]))

(def land (ref nil))
(def plane (ref nil))
(def plane-x (ref (float 400)))
(def plane-y (ref (float 300)))
(def scale (ref (float 1.0)))

(defn middle-scale
  [value scale]
  (/ value
     (* 2 scale)))

(def float+
  (comp float +))
(def float-
  (comp float -))
(def float*
  (comp float *))

(defn isInput
  [key input]
  (.isKeyDown input key))

(defn -main
  []
  (let [app (AppGameContainer. (proxy [BasicGame] ["SlickGame - SimpleGame from Clojure"]
                                 (init [gc]
                                   (dosync
                                    (ref-set land (Image. "data/land.jpg"))
                                    (ref-set plane (Image. "data/plane.png"))))
                                 (update [gc delta]
                                   (let [input (.getInput gc)]
                                     (condp isInput input

                                       Input/KEY_A
                                       (.rotate @plane (float (* -0.2 delta)))

                                       Input/KEY_D
                                       (.rotate @plane (float (* 0.2 delta)))

                                       Input/KEY_W
                                       (let [hip (* 0.4 delta)
                                             rotation (. @plane getRotation)]
                                         (dosync
                                          (alter plane-x float+
                                                 (* hip (Math/sin (Math/toRadians rotation))))
                                          (alter plane-y float-
                                                 (* hip (Math/cos (Math/toRadians rotation))))))

                                       Input/KEY_2
                                       (dosync
                                        (alter scale float+
                                               (if (>= @scale 5.0)
                                                 0
                                                 0.1))
                                        (.setCenterOfRotation @plane
                                                              (middle-scale (.getWidth @plane) @scale)
                                                              (middle-scale (.getHeight @plane) @scale)))

                                       Input/KEY_1
                                       (dosync
                                        (alter scale float-
                                               (if (<= @scale 1.0)
                                                 0
                                                 0.1))
                                        (.setCenterOfRotation @plane
                                                              (middle-scale (.getWidth @plane) @scale)
                                                              (middle-scale (.getHeight @plane) @scale))))))
                                 (render [gc g]
                                   (.draw @land 0 0)
                                   (.draw @plane @plane-x @plane-y @scale))))]
    (doto app
      (.setDisplayMode 800 600 false)
      (.start))))
