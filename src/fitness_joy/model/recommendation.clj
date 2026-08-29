(ns fitness-joy.model.recommendation)

(def recommendations
  {[:blood-sugar :low]    "Your blood sugar is low — eat some complex carbs before training."
   [:blood-sugar :high]   "Your blood sugar spiked. Wait 30-40 minutes — an energy crash is coming."
   [:hydration :low]      "You're dehydrated — drink water with electrolytes before you head out."
   [:stress-level :high]  "Your stress level is high. Take a 15-minute walk instead of an intense session."
   [:sleep-quality :low]  "You slept poorly last night — go lighter today, or take a 20-minute nap first."
   [:heart-rate :high]    "Your resting heart rate is elevated. Your body is asking for a rest day."
   [:caffeine-mg :high]   "Too much caffeine in your system. Wait for it to drop before training."
   [:pressure-upper :high]  "Your blood pressure is up — skip the heavy session, go for a walk instead."
   [:pressure-upper :low]   "Your blood pressure dropped. Drink water with a pinch of salt and don't stand up too fast."
   [:pressure-lower :high]  "Your lower blood pressure reading is elevated — avoid heavy lifting today."
   [:pressure-lower :low]   "Your lower reading is low — top up on fluids and electrolytes."})