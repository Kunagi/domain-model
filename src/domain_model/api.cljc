(ns domain-model.api
  (:require
   [bindscript.api :refer [def-bindscript]]
   [facts-db.api :as db]
   [facts-db.ddapi :as ddapi :refer [def-event def-query def-api events> <query new-db]]))


(def-api ::domain-model
  :autocreate-singleton-db? true
  :db-constructor
  (fn [db args]
    (db/++ db
           [{:db/id :model/config
             :constructor-args args}
            {:db/id :model
             :modules #{:kunagi}}
            ;; TODO remove :kunagi
            {:db/id :kunagi
             :ident :kunagi
             :entities #{}}])))


(def-event ::module-created
  (fn [model {:keys [id]}]
    {:db/id id
     :entities #{}
     :db/add-ref-n [:model :modules]}))


(def-event ::entity-created
  (fn [model {:keys [id module-id container-id ident]}]
    (let [ident (or ident :some/entity)]
      [{:db/id id
        :module module-id
        :container container-id
        :ident ident
        :components #{}
        :facts #{}}
       {:db/id module-id
        [:db/add-1 :entities] id}
       {:db/id container-id
        [:db/add-1 :components] id}])))


(def-event ::element-fact-updated
  (fn [model {:keys [element-id fact value]}]
    {:db/id element-id
     fact value}))


(def-query ::modules-ids
  (fn [model {:keys []}]
    (get-in model [:model :modules])))


(def-query ::model-details
  (fn [db _]
    (db/tree db :model {:modules {:entities {}
                                  :types {}}})))


(def-query ::module-details
  (fn [model {:keys [id]}]
    (db/tree model id {:entities {}
                       :types {}})))


(def-query ::entity
  (fn [model {:keys [id]}]
    (db/tree model id {})))


(def-query ::entities
  (fn [model {:keys [ids]}]
    (db/trees model ids {})))



(def-bindscript ::full-stack
  model (new-db :domain-model {})
  model (events> model [[:domain-model/module-created {:id :kunagi}]])

  model (events> model [[:domain-model/entity-created
                         {:module-id :kunagi
                          :container-id nil
                          :id :kunagi/pbl}]
                        [:domain-model/entity-created
                         {:module-id :kunagi
                          :container-id :kunagi/pbl
                          :id :kunagi/pbl-item}]])

  tree  (db/tree model
                 :model
                 {:modules {:entities {:components {}}}}))
