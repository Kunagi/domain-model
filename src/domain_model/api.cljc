(ns domain-model.api
  (:require
   [domain-model.model :as model]
   [domain-model.module :as module]
   [domain-model.modeling :as modeling]))


(defn def-module
  [ident & {:as facts}]
  (-> facts
      (assoc :ident ident)
      module/conform
      (model/update-model)))


(defn model
  []
  (model/db))
