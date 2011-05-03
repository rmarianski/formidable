(ns formidable.test.core
  (:use [formidable.core] :reload)
  (:use [clojure.test])
  (:import [formidable.core Invalid]))

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

(deftest validate-field-default
  (is (= 42
         (validate
          {:type :text
           :name "foo"
           :validator (constantly (Invalid. "validation failure"))}
          42))))

(deftest validate-form-nofields
  (is (= {:type :form :fields []}
         (validate
          {:type :form
           :fields []}
          {}))))

(deftest validate-form-failure-required
  (is (= "Missing required field"
         (->
          (validate
           {:type :form
            :fields [{:type :text
                      :name :greatscott
                      :validator (constantly (Invalid. "1.21 gigawatts!"))}]}
           {})
          :fields
          first
          :error))))

(deftest validate-form-failure-empty-value
  (is (= "1.21 gigawatts!"
         (->
          (validate
           {:type :form
            :fields [{:type :text
                      :name :greatscott
                      :validator (constantly (Invalid. "1.21 gigawatts!"))}]}
           {:greatscott ""})
          :fields
          first
          :error))))

(deftest validate-form-failure-empty-value
  (is (= "1.21 gigawatts!"
         (->
          (validate
           {:type :form
            :fields [{:type :text
                      :name :greatscott
                      :validator (constantly (Invalid. "1.21 gigawatts!"))}]}
           {:greatscott "heavy"})
          :fields
          first
          :error))))

(deftest validate-form-success
    (is (= nil
         (->
          (validate
           {:type :form
            :fields [{:type :text
                      :name :car}]}
           {:car "delorean"})
          :fields
          first
          :error))))
