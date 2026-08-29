(ns fitness-joy.model.user)

(def user
  [:map
   [:first-name :string]
   [:last-name :string]
   [:age :int]
   [:level [:enum :beginner :intermediate :advanced]]])

(def sample-user
  {:first-name "David"
   :last-name  "Stepanic"
   :age        26
   :level      :beginner})