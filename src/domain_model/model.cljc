(ns domain-model.model
  (:require
   [facts-db.api :as db]))


(defonce !db (atom {}))

(defn db
  []
  @!db)

(defn update-model
  [model]
  (swap! !db db/update-entity model))
