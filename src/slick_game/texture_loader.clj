(ns slick-game.texture-loader
  (:import [java.awt
            Color
            Graphics
            Image]
           [java.awt.color ColorSpace]
           [java.awt.image
            BufferedImage
            ColorModel
            ComponentColorModel
            DataBuffer
            DataBufferByte
            Raster
            WritableRaster]
           [java.net URL]
           [java.nio
            ByteBuffer
            ByteOrder
            IntBuffer]
           [javax.swing ImageIcon]
           [org.lwjgl BufferUtils]
           [org.lwjgl.opengl GL11]
           ))

(def table (ref {}))

(defrecord TextureLoader [gl-alpha-color-model
                          gl-color-model
                          texture-id-buffer])

(defn texture-loader
  []
  (TextureLoader. {}
                  (ComponentColorModel. (ColorSpace/getInstance ColorSpace/CS_sRGB)
                                        (int-array [8 8 8 8])
                                        true
                                        false
                                        ComponentColorModel/TRANSLUCENT
                                        DataBuffer/TYPE_BYTE)
                  (ComponentColorModel. (ColorSpace/getInstance ColorSpace)
                                        (int-array [8 8 8 0])
                                        false
                                        false
                                        ComponentColorModel/OPAQUE
                                        DataBuffer/TYPE_BYTE)
                  (BufferUtils/createIntBuffer 1)))

(defn create-texture-id
  [tl]
  (do
    (GL11/glGenTextures (:texture-id-buffer tl))
    (.get (:texture-id-buffer tl) 0)))

(defn get-texture
  ([tl resource-name]
     (if (contains? @table resource-name)
       (get @table resource-name)
       (let [tex (get-texture tl
                              resource-name
                              GL11/GL_TEXTURE_2D
                              GL11/GL_RGBA
                              GL11/GL_LINEAR
                              GL11/GL_LINEAR)]
         (dosync (alter table assoc resource-name tex))
         tex)))
  ([tl resource-name target dst-pixel-format min-filter mag-filter]
     (let [texture-id (create-texture-id tl)
           texture (new-texture target texture-id)
           bindtex (GL11/glBindTexture target texture-id)
           ;; What is loadImage?
           buffered-image (loadImage resource-name)
           texture (doto texture
                     (set-width (.getWidth buffered-image))
                     (set-height (.getHeight buffered-image)))])))
