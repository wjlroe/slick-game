(defn get-native-lib-path
  [os-name]
  (cond
   (re-find #"(?i)mac" os-name) "native/macosx"
   (re-find #"(?i)win" os-name) "native/windows"
   (re-find #"(?i)linux" os-name) "native/linux"
   (re-find #"(?i)solaris" os-name) "native/solaris"))

(defproject slick-game "0.1.0-SNAPSHOT"
  :description "A game written in Clojure, using Slick as a first attempt at 2D game programming"
  :url "https://github.com/wjlroe/slick-game"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [slick/slick "274"]]
  :repositories {"slick" "http://slick.cokeandcode.com/mavenrepo/"}
  :jvm-opts [~(str "-Djava.library.path=" (get-native-lib-path (System/getProperty "os.name")))]
  :main slick-game.simple-game
  :aot :all)
