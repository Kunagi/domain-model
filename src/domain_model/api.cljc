(ns domain-model.api
  (:require
   [bindscript.api :refer [def-bindscript]]
   [facts-db.api :as db]
   [facts-db.ddapi :as ddapi :refer [def-event def-query def-api events> <query new-db]]))


(def-api :domain-model
  :autocreate-singleton-db? true
  :db-constructor
  (fn [db args]
    (db/++ db
           [{:db/id :model/config
             :constructor-args args}
            {:db/id :model
             :modules #{}}])))


(def-event :domain-model/module-created
  (fn [model {:keys [id]}]
    (-> model
        (db/++ :model :modules {:db/id id
                                :entities #{}}))))


(def-event :domain-model/entity-created
  (fn [model {:keys [id module-id container-id ident]}]
    (let [ident (or ident :some/entity)]
      (-> model
          (db/++ [[module-id :entities]
                  [container-id :components]]
                 {:db/id id
                  :module module-id
                  :container container-id
                  :ident ident
                  :components #{}
                  :facts #{}})))))


(def-event :domain-model/element-fact-updated
  (fn [model {:keys [entity-id fact value]}]
    (-> model
        (db/++ {:db/id entity-id
                fact value}))))


(def-query :domain-model/modules-ids
  (fn [model {:keys []}]
    (get-in model [:model :modules])))


(def-query :domain-model/model-details
  (fn [db _]
    (db/tree db :model {:modules {:entities {}
                                  :types {}}})))


(def-query :domain-model/module-details
  (fn [model {:keys [id]}]
    (db/tree model id {:entities {}
                       :types {}})))


(def-query :domain-model/entity
  (fn [model {:keys [id]}]
    (db/tree model id {})))


(def-query :domain-model/entities
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
