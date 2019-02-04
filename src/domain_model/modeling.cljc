(ns domain-model.modeling
  (:require
   [bindscript.api :refer [def-bindscript]]
   [facts-db.api :as db]

   [domain-model.module :as module]
   [domain-model.entity :as entity]
   [domain-model.event :as event]
   [domain-model.reading :as r]))


(defn new-model
  []
  {})


(defn def-module
  [model facts]
  (-> facts
      module/conform
      (->> (db/++ model))))


(defn def-entity
  [model facts]
  (-> facts
      entity/conform
      (->> (db/++ model))))


(defn def-event
  [model facts]
  (-> facts
      event/conform
      (->> (db/++ model))))


(def-bindscript ::example
  model     (new-model)
  model     (def-module model {:ident :example})
  model     (def-entity model {:ident :example/person})
  model     (def-event  model {:ident :example/friendship-created
                               :params {:friend-1 :entity/person
                                        :friend-2 :entity/person}}))
