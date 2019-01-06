(ns domain-model.event)


(defn conform
  [event]
  (let [ident (:ident event)
        _ (if-not (qualified-keyword? ident)
            (throw (ex-info "Event fact :ident needs to be a qualified-keyword."
                            {:exception/type ::illegal-ident
                             :event event})))
        module-id (str "module/" (namespace ident))
        id (str "event/" (namespace ident) "/" (name ident))]
    ;; TODO conform params
    (-> event
        (assoc :db/id id)
        (assoc :module module-id))))
