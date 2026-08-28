(ns fitness-joy.model.user
  (:require [malli.core :as m]))

(def User
  [:map
   [:first-name :string]
   [:last-name :string]
   [:age :int]
   [:level [:enum :beginner :intermediat :advanced]]])

(def sample-user
  {:first-name "David"
   :last-name "Stepanic"
   :age 26
   :level :beginner})