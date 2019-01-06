(ns domain-model.example.browserapp
  (:require
   [bindscript.api :refer [def-bindscript]]
   [domain-model.api :as dm]
   [domain-model.example.entities]))


(.log js/console "loading example browserapp")


(defn -main []
  (.log js/console "main"))
