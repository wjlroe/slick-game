(ns slick-game.texture
  (:import [org.lwjgl.opengl
            GL11]))


(defrecord Texture [^int target
                    ^int texture-id
                    ^int height
                    ^int width
                    ^int tex-width
                    ^int tex-height
                    ^float width-ratio
                    ^float height-ratio])

(defn new-texture
  [target texture-id]
  (Texture. target texture-id nil nil nil nil nil nil))

(defn bind
  [t]
  (GL11/glBindTexture (:target t)
                      (:texture-id t)))

(defn calc-width-ratio
  [t]
  (if (not (= 0 (:tex-width t)))
    (assoc t :width-ratio (/ (float (:width t)) (:tex-width t)))
    t))

(defn calc-height-ratio
  [t]
  (if (not (= 0 (:tex-height t)))
    (assoc t :height-ratio (/ (float (:height t)) (:tex-height t)))
    t))

(defn set-height
  [t height]
  (let [t' (assoc t :height height)]
    (calc-height-ratio t')))

(defn set-width
  [t width]
  (let [t' (assoc t :width width)]
    (calc-width-ratio t')))

(defn set-texture-width
  [t width]
  (let [t' (assoc t :tex-width width)]
    (calc-width-ratio t')))

(defn set-texture-height
  [t height]
  (let [t' (assoc t :tex-height height)]
    (calc-height-ratio t')))
