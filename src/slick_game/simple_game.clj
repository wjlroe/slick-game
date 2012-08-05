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

(defn go-left
  [gc delta]
  (.rotate @plane (float (* -0.2 delta))))

(defn go-right
  [gc delta]
  (.rotate @plane (float (* 0.2 delta))))

(defn go-forward
  [gc delta]
  (let [hip (* 0.4 delta)
        rotation (. @plane getRotation)]
    (dosync
     (alter plane-x float+
            (* hip (Math/sin (Math/toRadians rotation))))
     (alter plane-y float-
            (* hip (Math/cos (Math/toRadians rotation)))))))

(defn zoom-in
  [gc delta]
  (dosync
   (alter scale float+
          (if (>= @scale 5.0)
            0
            0.1))
   (.setCenterOfRotation @plane
                         (middle-scale (.getWidth @plane) @scale)
                         (middle-scale (.getHeight @plane) @scale))))

(defn zoom-out
  [gc delta]
  (dosync
   (alter scale float-
          (if (<= @scale 1.0)
            0
            0.1))
   (.setCenterOfRotation @plane
                         (middle-scale (.getWidth @plane) @scale)
                         (middle-scale (.getHeight @plane) @scale))))

(def keyboard-mappings
  {Input/KEY_A go-left
   Input/KEY_D go-right
   Input/KEY_W go-forward
   Input/KEY_2 zoom-in
   Input/KEY_1 zoom-out})

(defn input-event
  [gc delta]
  (let [input (.getInput gc)]
    (doseq [[key fun] (filter (fn [[key fun]] (.isKeyDown input key))
                              keyboard-mappings)]
      (fun gc delta))))

(defn -main
  []
  (let [app (AppGameContainer. (proxy [BasicGame] ["SlickGame - SimpleGame from Clojure"]
                                 (init [gc]
                                   (dosync
                                    (ref-set land (Image. "data/land.jpg"))
                                    (ref-set plane (Image. "data/plane.png"))))
                                 (update [gc delta]
                                   (input-event gc delta))
                                 (render [gc g]
                                   (.draw @land 0 0)
                                   (.draw @plane @plane-x @plane-y @scale))))]
    (doto app
      (.setDisplayMode 800 600 false)
      (.start))))
