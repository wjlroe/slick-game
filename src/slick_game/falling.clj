(ns slick-game.falling
  (:import
   [org.newdawn.slick
    AppGameContainer
    BasicGame
    GameContainer
    Graphics
    SlickException
    Image
    Input]))

(def falling-squares (ref []))
(def last-move (ref 0))

(defn basic-square
  []
  (Image. "data/beige_sq.jpg"))

(def sq-width 30)
(def sq-height 30)

(def shapes
  [{:name "O"
    :config
    [[0 0]
     [sq-height sq-width]
     [0 sq-height]
     [sq-width 0]]}
   {:name "J"
    :config
    [[0 (* 2 sq-height)]
     [sq-width 0]
     [sq-width sq-height]
     [sq-width (* 2 sq-height)]]}
   {:name "I"
    :config
    [[0 0]
     [0 sq-height]
     [0 (* 2 sq-height)]
     [0 (* 3 sq-height)]]}
   {:name "L"
    :config
    [[0 0]
     [0 sq-height]
     [0 (* 2 sq-height)]
     [sq-width (* 2 sq-height)]]}
   {:name "S"
    :config
    [[0 sq-height]
     [sq-width 0]
     [sq-width sq-height]
     [(* 2 sq-width) 0]]}
   {:name "Z"
    :config
    [[0 0]
     [sq-width 0]
     [sq-width sq-height]
     [(* 2 sq-width) sq-height]]}
   {:name "T"
    :config
    [[0 0]
     [sq-width 0]
     [sq-width sq-height]
     [(* 2 sq-width) 0]]}])

(defn make-shape
  "Just makes a square atm"
  [origin name]
  {:origin origin
   :graphic (basic-square)
   :blocks
   (first (filter #(= name (:name %)) shapes))})

(defn render-square
  [gc g {:keys [origin graphic blocks]}]
  (doseq [[x y] (:config blocks)]
    (.draw graphic
           (+ (:x origin) x)
           (+ (:y origin) y))))

(defn init-world
  "Nothing to do maybe?"
  [gc]
  (dosync
   (ref-set falling-squares [(make-shape {:x 0 :y 0} "O")
                             (make-shape {:x 70 :y 0} "J")
                             (make-shape {:x 140 :y 0} "I")
                             (make-shape {:x 210 :y 0} "L")
                             (make-shape {:x 280 :y 0} "S")
                             (make-shape {:x 400 :y 0} "Z")
                             (make-shape {:x 500 :y 0} "T")])))

(defn input-event
  "Shove new shape in the world every x seconds"
  [gc delta])

(defn render-world
  "Loop through all placed shapes, rendering them"
  [gc g]
  (doseq [shape @falling-squares]
    (render-square gc g shape)))

(defn -main
  []
  (let [app (AppGameContainer. (proxy [BasicGame] ["Falling blocks"]
                                 (init [gc]
                                   (init-world gc))
                                 (update [gc delta]
                                   (input-event gc delta))
                                 (render [gc g]
                                   (render-world gc g))))]
    (doto app
      (.setDisplayMode 800 600 false)
      (.start))))
