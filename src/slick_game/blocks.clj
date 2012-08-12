(ns slick-game.blocks
    (:import
   [org.lwjgl
    LWJGLException]
   [org.lwjgl.opengl
    Display
    DisplayMode]))

(def ^:dynamic *display-width* 800)
(def ^:dynamic *display-height* 600)
(def ^:dynamic *falling-speed* 500)

(def falling-squares (ref []))
(def last-move (ref 0))
(def delta-countdown (ref 0))
(def capturenum (ref 0))

(defn -main
  []
  (try
    (Display/setDisplayMode (DisplayMode. *display-width* *display-height*))
    (Display/create)
    (while (not (Display/isCloseRequested))
      (Display/update))
    (Display/destroy)
    (catch LWJGLException e
      (.printStackTrace e))))
