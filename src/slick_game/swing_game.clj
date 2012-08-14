(ns slick-game.swing-game
  (:use
   slick-game.game-interface
   clojure.java.io)
  (:import [java.awt.image BufferedImage]
           [java.awt.event KeyAdapter MouseAdapter WindowAdapter]
           [java.awt Canvas Dimension Graphics2D]
           [javax.imageio ImageIO]
           [javax.swing JFrame JPanel]))

;; ## Some config vars

(def window-width 800)
(def window-height 600)

;; ## State vars

(def canvas (ref (Canvas.)))
(def running (ref true))

;; ## Utility methods

(defn stop-game
  []
  (ref-set running false))

;; ## Input handling methods

(defn handle-keypress
  [event])

(defn handle-mouse
  [event])

;; ## Bootstrapping and setup

(defn setup-swing
  "Bootstrap all the Swing interface elements. Requires title of the window to be passed in"
  [title]
  (let [#^JFrame frame (doto (JFrame. title)
                         (.addWindowListener (proxy [WindowAdapter] []
                                               (windowClosing [e] (stop-game)))))
        #^JPanel panel (doto (.getContentPage frame)
                         (.setPreferredSize (Dimension. window-width window-height))
                         (.setLayout nil))
        #^Canvas newcanvas (Canvas.)]
    (doto newcanvas
      (.setBounds 0 0 window-width window-height)
      (.setIgnoreRepaint true)
      (.addKeyListener (proxy [KeyAdapter] []
                         (keyPressed [e] (handle-keypress e))))
      (.addMouseListener (proxy [MouseAdapter] []
                           (mouseClicked [e] (handle-mouse e))))
      (.createBufferStrategy 2))
    (.add panel newcanvas)
    (doto frame
      (.pack)
      (.setResizable false)
      (.setVisible true))
    (dosync
     (ref-set canvas newcanvas))))

(defrecord SwingInterface [running]
  GAMEINTERFACE
  (init [interface game-name]
    (setup-swing game-name))
  (render [interface world])
  (running? [interface]
    @running))
