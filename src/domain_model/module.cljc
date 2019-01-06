(ns domain-model.module)


(defn conform
  [module]
  (let [ident (:ident module)
        _ (if-not (simple-keyword? ident)
            (throw (ex-info "Module fact :ident needs to be a simple-keyword."
                            {:exception/type ::illegal-ident
                             :module module})))
        id (str "module/" (name ident))]
    (assoc module :db/id id)))
