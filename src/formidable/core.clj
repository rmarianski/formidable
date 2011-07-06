(ns formidable.core
  (:use [clojure.string :only (capitalize)]
        [clojure.contrib.string :only (as-str)]))

(defn wrap-errors
  "add in errors for form fields puts the error in opts if it's a map"
  [f errors]
  (fn [type attrs name opts value]
    (if (nil? opts)
      (recur type attrs name {} value)
      (f type attrs name
         (if (map? opts)
           (merge {:error (errors name)} opts)
           opts)
         value))))

; uniform specific, this might just go into a formidable.uniform file
(defn wrap-form-field
  "wraps a particular field with a label, description, error which is
  fetched from opts which must be a map. options for the field itself
  go under the :opts key"
  [f]
  (fn [type attrs name opts value]
    [:div.ctrlHolder
     (list
      [:label {:required (:required opts)}
       (or (:label opts) (capitalize (as-str name)))]
      (when-let [desc (:description opts)]
        [:p.formHint desc])
      (f type attrs name (:opts opts) value)
      (when-let [error (:error opts)]
        [:div.error error]))]))
