(ns formidable.core
  (:use [clojure.string :only (capitalize)]
        [clojure.contrib.string :only (as-str)]
        [clojure.contrib.core :only (seqable?)]))

(defmulti render :type)

(defmethod render :text
  [field value]
  [:input {:type :text :name (:name field) :value (or value "")}])

(defmethod render :submit
  [field _]
  [:input {:type :submit
           :name (:name field "submit")
           :value (:value field "submit")}])

(defmethod render :uniform
  [form params]
  (if-let [fields (:fields form)]
    [:form.uniForm {:method (:method form :post) :action (:action form)}
     (when-let [label (:label form)] [:h2 label])
     (when-let [description (:description form)] [:p description])
     [:fieldset
      (for [field fields]
        (let [field-name (:name field)
              error-message (:error field)
              required (:required field true)
              renderer (:renderer field render)
              value (get (or params {}) field-name)]
          [:div.ctrlHolder
           (let [label (:label field (capitalize (as-str field-name)))
                 description (:description field)]
             (list
              [:label label (when required [:span.required "*"])]
              (renderer field value)
              (when description [:p.formHint description])
              (when error-message [:div.error error-message])))]))
      [:div.buttonHolder (map #(render % params) (:buttons form))]]]
     (throw (IllegalArgumentException. "render form: no fields"))))

(defmethod render nil [_ _] nil)
