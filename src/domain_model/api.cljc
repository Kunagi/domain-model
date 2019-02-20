(ns domain-model.api
  (:require
   [bindscript.api :refer [def-bindscript]]
   [facts-db.api :as db]))


;;; meta api

(defmulti apply-event (fn [model event args] event))

(defn apply-events
  [model events]
  (reduce
   (fn [model [event-name args]]
     (apply-event model event-name args))
   model
   events))


;;; domain model api implementation


(defn new-model
  []
  (-> (db/new-db)
      (db/++ [{:db/id :model/config}
              {:db/id :model
               :modules #{}}])))


(defmethod apply-event :domain-model/module-created
  [model event {:keys [id]}]
  (-> model
      (db/++ :model :modules {:db/id id
                              :entities #{}})))


(defmethod apply-event :domain-model/entity-created
  [model event {:keys [module-id container-id id]}]
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
  model (apply-events model [[:domain-model/module-created {:id :kunagi}]])

  model (apply-events model [[:domain-model/entity-created
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
