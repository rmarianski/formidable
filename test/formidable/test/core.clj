(ns formidable.test.core
  (:use [formidable.core] :reload)
  (:use [clojure.test]))

(deftest render-text-field
  (is (= [:input {:type :text :name :mcfly :value ""}]
         (render {:type :text :name :mcfly} nil))))

(deftest render-button
  (is (= [:input {:type :submit :name :submit :value "submit"}]
           (render {:type :submit :name :submit} nil))))

(deftest render-form-no-values
  (is (= [:form {:method :post :action nil} nil nil
          '([:div nil [:span.required]
             ([:label "Mcfly"] nil
                [:input {:type :text, :name :mcfly, :value ""}])])
          [:div '([:input {:type :submit, :name "submit", :value "submit"}])]]
           (render {:type :form
                    :fields [{:type :text :name :mcfly}]
                    :buttons [{:type :submit}]}
                   {}))))

(deftest render-form-values
  (is (= [:form {:method :post :action nil} nil nil
          '([:div nil [:span.required]
             ([:label "Mcfly"] nil
                [:input {:type :text, :name :mcfly, :value "george"}])])
          [:div '([:input {:type :submit, :name "submit", :value "submit"}])]]
         (render {:type :form
                  :fields [{:type :text :name :mcfly}]
                  :buttons [{:type :submit}]}
                 {:mcfly "george"}))))
