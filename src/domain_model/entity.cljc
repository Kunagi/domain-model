(ns domain-model.entity)


(defn conform
  [entity]
  (let [ident (:ident entity)
        _ (if-not (qualified-keyword? ident)
            (throw (ex-info "Entity fact :ident needs to be a qualified-keyword."
                            {:exception/type ::illegal-ident
                             :entity entity})))
        module-id (str "module/" (namespace ident))
        id (str "entity/" (namespace ident) "/" (name ident))]
    (-> entity
        (assoc :db/id id)
        (assoc :module module-id))))
