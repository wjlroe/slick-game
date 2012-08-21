(ns slick-game.swing-game
  (:use
   slick-game.game-interface
   clojure.java.io)
  (:import [java.awt.image BufferedImage]
           [java.awt.event KeyAdapter KeyEvent MouseAdapter WindowAdapter]
           [java.awt Canvas Color Dimension Graphics2D]
           [javax.imageio ImageIO]
           [javax.swing JFrame JPanel JLabel]))

;; ## Some config vars

(def window-width 800)
(def window-height 600)

;; ## State vars

(def graphics (ref nil))
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
  [event]
  (do
    (println "key event:" event)
    (if (= KeyEvent/VK_Q (.getKeyCode event))
      (stop-game))))

(defn handle-mouse
  [event])

(defn paint-world
  [world]
  (let [g @graphics
        falling-shapes (:falling world)]
    (doseq [{:keys [origin blocks block-width block-height]} falling-shapes]
      (doseq [[incx incy] blocks]
       (let [x (+ incx (:x origin))
             y (+ incy (:y origin))]
         (.setColor g (Color. 60 80 160))
         (.fillRect g x y block-width block-height))))))

;; ## Bootstrapping and setup

(defn setup-swing
  "Bootstrap all the Swing interface elements. Requires title of the window to be passed in"
  [title]
  (let [#^JFrame frame (doto (JFrame. title)
                         (.addWindowListener (proxy [WindowAdapter] []
                                               (windowClosing [e]
                                                 (stop-game))))
                         (.addKeyListener (proxy [KeyAdapter] []
                                            (keyPressed [e] (handle-keypress e))))
                         (.addMouseListener (proxy [MouseAdapter] []
                                              (mouseClicked [e] (handle-mouse e)))))
        background (doto (proxy
                             [JPanel]
                             []
                           (paintComponent [g]
                             (proxy-super paintComponent g)
                             ;; paint the background pic
                             ))
                     (.setBounds 0 0 window-width window-height))
        canvas (doto (proxy [JPanel] []
                       (paintComponent [g]
                         (proxy-super paintComponent g))))]
    (doto frame
      (.add background)
      (.add canvas)
      (.setSize window-width window-height)
      (.setResizable false)
      (.setVisible true))
    (dosync
     (ref-set graphics (.getGraphics canvas)))))
