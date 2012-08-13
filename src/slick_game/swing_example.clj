(ns slick-game.swing-example
  (:use
   clojure.java.io)
  (:import [java.awt.image BufferedImage]
           [java.awt.event KeyAdapter MouseAdapter WindowAdapter]
           [java.awt Canvas Dimension Graphics2D]
           [javax.imageio ImageIO]
           [javax.swing JFrame JPanel]))

(def REAL-TILE-SIZE 50)
(def REAL-WIDTH 800)
(def REAL-HEIGHT 600)
(def running (ref true))
(def painter (ref nil))
(def world (ref nil))

(defrecord Map [tileset pad tiles walls])

(defn tile-to-real
  [dimen]
  (* REAL-TILE-SIZE dimen))

(defn indexed
  [coll]
  (map-indexed (fn [idx itm] [idx itm]) coll))

(defn get-time
  []
  (. System (nanoTime)))

(defn cache-map [amap]
  (let [height (count (:tiles amap))
        width (count (first (:tiles amap)))

        tileset (:tileset amap)

        #^BufferedImage img (BufferedImage. (tile-to-real width) (tile-to-real height) BufferedImage/TYPE_INT_ARGB)
        #^Graphics2D g (.createGraphics img)

        #^BufferedImage pad (BufferedImage. (tile-to-real width) (tile-to-real height) BufferedImage/TYPE_INT_ARGB)
        #^Graphics2D padg (.createGraphics pad)]
    (doseq [[y row] (indexed (:tiles amap))
            [x tile] (indexed row)]
      (.drawImage padg (:pad amap) (tile-to-real x) (tile-to-real y) REAL-TILE-SIZE REAL-TILE-SIZE nil)
      (if-let [#^Image img (get tileset tile)]
        (.drawImage g img (tile-to-real x) (tile-to-real y) REAL-TILE-SIZE REAL-TILE-SIZE nil)
        (throw (Exception. (str "Missing tile " tile ".")))))
    (assoc amap
      :map-image img
      :pad-image pad
      :max-map-x width
      :max-map-y height)))

;; (defn tile [name]
;;   (ImageIO/read (file (str "img/" name ".png"))))

(def all-tile-names
  (filter
   (fn [f] (.isFile f))
   (.listFiles (file (str "/Users/will/Downloads/TEXTURES")))))

(defn tile [name]
  (ImageIO/read
   (rand-nth all-tile-names)))

(def test-map (cache-map
               (Map. {\  (tile "ground")
                      \/ (tile "ground-shadow-botright")
                      \< (tile "ground-shadow-left")
                      \d (tile "dirt")
                      \| (tile "wall_vertical")
                      \- (tile "wall_horizontal")
                      \1 (tile "wall_topleft")
                      \2 (tile "wall_topright")
                      \3 (tile "wall_bottomleft")
                      \4 (tile "wall_bottomright")
                      \u (tile "below-wall")
                      \v (tile "below-wall-shadow")
                      \w (tile "wood-floor")
                      \c (tile "cobble")
                      \b (tile "bush")}

                     (tile "ground")

                     ["                 1----2                            "
                      "                 |vuuu|                            "
                      "1----------------4<bbb3--------------------------2 "
                      "|vuuuuuuuuuuuuuuuu/   uuuuuuuuuuuuuuuuuuuuuuuuuuu|<"
                      "|<1------------2               cccccccc d        |<"
                      "|<|vuuuuuuuuuuu|<       d      c      c          |<"
                      "|<|<           |< cccccccccccccc    d ccccccc d  |<"
                      "|<3------ -----4< c                         ccccc|<"
                      "|<uuuuuuu/uuuuuu/ c  1-------2    1------------2 |<"
                      "|<1---2 bcb       c  |vuuuuuu|<   |vuuuuuuuuuuu|<|<"
                      "|<|vuu|< c  d     c  |< 1-- -4<   |<           |<|<"
                      "|<3- -4<bcb       c  |< |vucuu/   3------ -----4<|<"
                      "|<uu/uu/ c        c  3--4< c      uuuuuuu/uuuuuu/|<"
                      "|<  c  dbcb       c  uuuu/ cccc         bcb      |<"
                      "|<  cccccccccccccccccccccccc  cccccccc   c   d   |<"
                      "|<  d        d                     d ccccc       |<"
                      "|<                    1--------------------------4<"
                      "3---------------------4uuuuuuuuuuuuuuuuuuuuuuuuuuu/"
                      "uuuuuuuuuuuuuuuuuuuuuuu/                           "]

                     ["                 xxxxxx                            "
                      "                 x    x                            "
                      "xxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx "
                      "x                                                x "
                      "x xxxxxxxxxxxxxx                                 x "
                      "x x            x                                 x "
                      "x x            x                                 x "
                      "x xxxxxxx xxxxxx                                 x "
                      "x                    xxxxxxxxx    xxxxxxxxxxxxxx x "
                      "x xxxxx x x          x       x    x            x x "
                      "x x   x              x  xxx xx    x            x x "
                      "x xx xx x x          x  x         xxxxxxx xxxxxx x "
                      "x                    xxxx                        x "
                      "x       x x                             x x      x "
                      "x                                                x "
                      "x                                                x "
                      "x                     xxxxxxxxxxxxxxxxxxxxxxxxxxxx "
                      "xxxxxxxxxxxxxxxxxxxxxxx                            "]
                     )))

(defn do-logic
  [])

(defn do-render
  [canvas])

(defn game-loop
  [#^Canvas canvas]
  (loop [last-time (get-time)]
    (let [curr-time (get-time)
          delta (- curr-time last-time)]
      (do-logic delta)
      (do-render canvas)
      (when @running
        (Thread/sleep 10)
        (recur curr-time)))))

(defn stop
  []
  (dosync
   (ref-set running false)))

(defn handle-keypress
  [e])

(defn handle-mouse
  [e])

(defn start-world
  [this-world]
  (let [#^JFrame frame (doto (JFrame. "Game")
                         (.addWindowListener (proxy [WindowAdapter] []
                                               (windowClosing [e] (stop)))))
        #^JPanel panel (doto (.getContentPane frame)
                         (.setPreferredSize (Dimension. REAL-WIDTH REAL-HEIGHT))
                         (.setLayout nil))
        #^Canvas canvas (Canvas.)]
    (doto canvas
      (.setBounds 0 0 REAL-WIDTH REAL-HEIGHT)
      (.setIgnoreRepaint true)
      (.addKeyListener (proxy [KeyAdapter] []
                         (keyPressed [e] (handle-keypress e))))
      (.addMouseListener (proxy [MouseAdapter] []
                           (mouseClicked [e] (handle-mouse e)))))
    (.add panel canvas)
    (doto frame
      (.pack)
      (.setResizable false)
      (.setVisible true))
    (.createBufferStrategy canvas 2)
    (dosync (ref-set running true)
            (ref-set painter (agent canvas))
            (ref-set world this-world))
    (send-off @painter game-loop)))
