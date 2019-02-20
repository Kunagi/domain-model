(ns domain-model.api
  (:require
   [bindscript.api :refer [def-bindscript]]
   [facts-db.api :as db]))


(defn new-model
  []
  (-> (db/new-db)
      (db/++ [{:db/id :model/config}
              {:db/id :model
               :modules #{}}])))


(defn on-module-created
  [model id]
  (-> model
      (db/++ :model :modules {:db/id id
                              :entities #{}})))


(defn on-entity-created
  [model module-id container-id id]
  (-> model
      (db/++ [[module-id :entities]
              [container-id :components]]
             {:db/id id
              :module module-id
              :container container-id
              :components #{}
              :facts #{}})))


(def-bindscript ::full-stack
  model (new-model)
  model (on-module-created model :kunagi)

  model (on-entity-created model :kunagi nil :kunagi/pbl)
  model (on-entity-created model :kunagi :kunagi/pbl :kunagi/pbl-item)

  tree  (db/tree model
                 :model
                 {:modules {:entities {:components {}}}}))
