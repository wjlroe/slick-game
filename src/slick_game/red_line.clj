(ns slick-game.red-line
  (:import [org.lwjgl.opengl
            Display
            DisplayMode
            GL11]
           [org.lwjgl.util.glu GLU]
           [org.lwjgl.input Keyboard]))

(def done (ref false))
(def displayMode (ref nil))

(defn mainloop []
  (if (Keyboard/isKeyDown Keyboard/KEY_ESCAPE)
    (dosync (ref-set done true)))
  (if (Display/isCloseRequested)
    (dosync (ref-set done true))))

(defn render []
  (do
    (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT  GL11/GL_DEPTH_BUFFER_BIT))
    (GL11/glLoadIdentity)
    (GL11/glBegin GL11/GL_LINES)
    (do
      (GL11/glColor3f  1.0 1.0 1.0)
      (GL11/glVertex2i    0    0)
      (GL11/glVertex2i  100  100))
    (GL11/glEnd)
    (GL11/glFlush)))

;; (defn get-display-mode
;;   []
;;   (let [modes (Display/getAvailableDisplayModes)]
;;     (do
;;       (println modes)
;;       (first
;;        (filter #(and
;;                  (== (.getWidth %) 640)
;;                  (== (.getHeight %) 480)
;;                  (== (.getBitsPerPixel %) 24))
;;                modes)))))

(defn get-display-mode
  []
  (DisplayMode. 800 600))

(defn createWindow []
  (dosync (ref-set displayMode
                   (get-display-mode)))
  (Display/setDisplayMode @displayMode)
  (Display/setTitle "Red Line")
  (Display/create)
  (Keyboard/create))

(defn initGL []
  (GL11/glClearColor 0.0 0.0 0.0 0.0)
  (GL11/glMatrixMode GL11/GL_PROJECTION)
  (GLU/gluOrtho2D 0.0 (.getWidth @displayMode) 0.0 (.getHeight @displayMode))
  (GL11/glMatrixMode GL11/GL_MODELVIEW))

(defn init []
  (createWindow)
  (initGL))

(defn cleanup []
  (Display/destroy))

(defn main []
  (init)
  (while (not @done)
    (mainloop)
    (render)
    (. Display update))
  (cleanup))

(defn -main
  []
  (main))
