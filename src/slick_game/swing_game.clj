(ns slick-game.swing-game
  (:use
   slick-game.game-interface
   clojure.java.io)
  (:import [java.awt.image BufferedImage]
           [java.awt.event KeyAdapter MouseAdapter WindowAdapter]
           [java.awt Canvas Color Dimension Graphics2D]
           [javax.imageio ImageIO]
           [javax.swing JFrame JPanel JLabel]))

;; ## Some config vars

(def window-width 800)
(def window-height 600)

;; ## State vars

;;(def canvas (ref nil))
(def running (ref true))

;; ## Utility methods

(defn stop-game
  []
  (dosync
   (ref-set running false)))

(defn running?
  []
  @running)

;; ## Input handling methods

(defn handle-keypress
  [event])

(defn handle-mouse
  [event])

(defn paint-world
  [graphics]
  (.setColor graphics (Color. 60 80 160))
  (.fillRect graphics 30 30 100 100))

(def canvas (proxy [JPanel] []
              (paintComponent [g]
                (proxy-super paintComponent g)
                (paint-world g))))

;; ## Bootstrapping and setup

(defn setup-swing
  "Bootstrap all the Swing interface elements. Requires title of the window to be passed in"
  [title]
  (let [#^JFrame frame (doto (JFrame. title)
                         (.addWindowListener (proxy [WindowAdapter] []
                                               (windowClosing [e]
                                                 (stop-game)))))]
    (doto canvas
      (.setBounds 0 0 window-width window-height)
      (.addKeyListener (proxy [KeyAdapter] []
                         (keyPressed [e] (handle-keypress e))))
      (.addMouseListener (proxy [MouseAdapter] []
                           (mouseClicked [e] (handle-mouse e)))))
    (doto frame
      (.add canvas)
      (.setSize window-width window-height)
      (.setResizable false)
      (.setVisible true))))

(defrecord SwingInterface []
  GAMEINTERFACE
  (init [interface game-name]
    (setup-swing game-name))
  (render [interface world]))

(defn new-interface
  []
  (SwingInterface.))
