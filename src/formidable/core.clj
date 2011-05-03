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

(defmethod render :form
  [form params]
  (if-let [fields (:fields form)]
    [:form {:method (:method form :post) :action (:action form)}
     (when-let [label (:label form)] [:h2 label])
     (when-let [description (:description form)] [:p description])
     (for [field fields]
       (let [field-name (:name field)
             error-message (:error field)
             required (:required field true)
             renderer (:renderer field render)
             value (get (or params {}) field-name)]
         ;; maybe custom layout function?
         [:div
          (when error-message [:div.error error-message])
          (when required [:span.required])
          (let [label (:label field (capitalize (as-str field-name)))
                description (:description field)]
            (list
             [:label label]
             (when description [:p description])
             (renderer field value)))]))
     [:div (map #(render % params) (:buttons form))]]
     (throw (IllegalArgumentException. "render form: no fields"))))

(defmethod render nil [_ _] nil)


(deftype Invalid [message])

(defn invalid? [value] (instance? Invalid value))

(defmulti validate :type)

(defmethod validate :default [field value] value)

(defn- validate-fields [fields values]
  (vec
   (for [field fields]
     (let [value (get values (:name field))
           required (:required field true)]
       (if (and required
                (or
                 (nil? value)
                 (and (seqable? value) (empty? value))))
         (assoc field :error "Missing required field")
         (let [validator (:validator field validate)
               validation-result (validator field value)]
           (if (invalid? validation-result)
             (assoc field :error (.message validation-result))
             field)))))))

(defmethod validate :form
  [form values]
  (update-in form [:fields] validate-fields values))
