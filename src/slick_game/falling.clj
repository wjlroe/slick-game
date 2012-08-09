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

(def ^:dynamic *display-width* 800)
(def ^:dynamic *display-height* 600)
(def ^:dynamic *falling-speed* 500)

(def falling-squares (ref []))
(def last-move (ref 0))
(def delta-countdown (ref 0))

(defn basic-square
  []
  (Image. "data/beige_sq.jpg"))

(def sq-width 30)
(def sq-height 30)

(def shapes
  {:o
   [[0 0]
    [sq-height sq-width]
    [0 sq-height]
    [sq-width 0]]
   :j
   [[0 (* 2 sq-height)]
    [sq-width 0]
    [sq-width sq-height]
    [sq-width (* 2 sq-height)]]
   :i
   [[0 0]
    [0 sq-height]
    [0 (* 2 sq-height)]
    [0 (* 3 sq-height)]]
   :l
   [[0 0]
    [0 sq-height]
    [0 (* 2 sq-height)]
    [sq-width (* 2 sq-height)]]
   :s
   [[0 sq-height]
    [sq-width 0]
    [sq-width sq-height]
    [(* 2 sq-width) 0]]
   :z
   [[0 0]
    [sq-width 0]
    [sq-width sq-height]
    [(* 2 sq-width) sq-height]]
   :t
   [[0 0]
    [sq-width 0]
    [sq-width sq-height]
    [(* 2 sq-width) 0]]})

(defn get-shape
  "Get the co-ordinates of a shape. Either returns the required shape or a random shape."
  ([]
     (get-shape (rand-nth (keys shapes))))
  ([shape-name]
     (get shapes shape-name)))

(defn make-shape
  "Make a shape. Either return the shape required or a random one."
  [origin & shape-name]
  {:origin origin
   :graphic (basic-square)
   :blocks
   (apply get-shape shape-name)})

(defn shift-down
  [shape]
  (assoc shape :origin (assoc (:origin shape) :y
                              (+ sq-height
                                 (:y (:origin shape))))))

(defn render-square
  [gc g {:keys [origin graphic blocks]}]
  (doseq [[x y] blocks]
    (.draw graphic
           (+ (:x origin) x)
           (+ (:y origin) y))))

(defn midpoint
  []
  (- (/ *display-width* 2)
     (* 2 sq-width)))

(defn init-world
  "Nothing to do maybe?"
  [gc]
  (dosync
   (ref-set falling-squares [(make-shape {:x (midpoint) :y 0})])))

(defn move-pieces-down
  []
  (ref-set falling-squares (map shift-down @falling-squares)))

(defn input-event
  "Shove new shape in the world every x seconds"
  [gc delta]
  (dosync
    (alter delta-countdown - delta)
    (when (< @delta-countdown 0)
      (move-pieces-down)
      (ref-set delta-countdown *falling-speed*))))

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
      (.setDisplayMode *display-width* *display-height* false)
      (.start))))
