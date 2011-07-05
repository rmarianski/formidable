(ns formidable.core
  (:use [clojure.string :only (capitalize)]
        [clojure.contrib.string :only (as-str)]))

; uniform specific
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

  ;; [:form.uniForm {:method (:method form-opts :post) :action (:action form-opts)}
  ;;    (when-let [label (:label form)] [:h2 label])
  ;;    (when-let [description (:description form)] [:p description])
  ;;    [:fieldset
  ;;     (for [field fields]
  ;;       (let [field-name (:name field)
  ;;             error-message (:error field)
  ;;             required (:required field true)
  ;;             renderer (:renderer field render)
  ;;             value (get (or params {}) field-name)]
  ;;         [:div.ctrlHolder
  ;;          (let [label (:label field (capitalize (as-str field-name)))
  ;;                description (:description field)]
  ;;            (list
  ;;             [:label label (when required [:span.required "*"])]
  ;;             (renderer field value)
  ;;             (when description [:p.formHint description])
  ;;             (when error-message [:div.error error-message])))]))
  ;;     [:div.buttonHolder (map #(render % params) (:buttons form))]]]
  ;;    (throw (IllegalArgumentException. "render form: no fields"))))
