# Fitness Joy — Live Fitness Coaching (CLI) · Clojure / Leiningen

Fitness Joy is a console application written in Clojure (Leiningen) that turns raw
biometric readings into concrete, actionable advice. It is a prototype of a
"Whoop+" concept: instead of summarising your day *after* it ends, the system
evaluates each incoming reading as it arrives and tells the user what to do
**right now** — train, walk, eat, hydrate, or rest.

The focus of the project is on **decision logic**, **immutable state management**,
and **testability** — not on a GUI or a database.

---

## 1) Project Goal and Scope

### Goal

Build a tool that reads a stream of biometric measurements, decides which
parameters are outside a healthy range, and produces plain-language
recommendations for the user.

### The problem it addresses

Consumer wearables are good at reporting and bad at advising. A user is told
"your HRV was low last night" and left to interpret it. This project inverts
that: every reading is evaluated against reference ranges, and the output is a
sentence the user can act on.

### Scope (what the program covers)

- Model the domain (user, biometric event, healthy ranges, recommendations) as
  plain data, validated with Malli schemas.
- Classify each parameter of an incoming reading as `:low`, `:ok`, `:high`, or
  `:unknown`.
- Aggregate per-parameter statuses into an analysis of the whole reading.
- Map that analysis to human-readable recommendations.
- Hold the current user state in an `atom` and advance it as new readings arrive,
  keeping a history of past readings.
- Generate synthetic readings locally, so the system runs and can be tested
  without any external service.
- Keep the core logic pure, so it can be tested deterministically with Midje.

### Out of scope (deliberately)

- No GUI — the project is a REPL/CLI prototype.
- No real device integration — readings are simulated (see section 7).
- Persistence and asynchronous ingestion are documented as future work
  (see section 11).

---

## 2) Domain Model

Every measurement ("event") is a flat map of numeric parameters:

| Parameter         | Unit    | Meaning                            |
|-------------------|---------|------------------------------------|
| `:heart-rate`     | bpm     | resting heart rate                 |
| `:pressure-upper` | mmHg    | upper (systolic) pressure reading  |
| `:pressure-lower` | mmHg    | lower (diastolic) pressure reading |
| `:blood-sugar`    | mmol/L  | blood glucose                      |
| `:hydration`      | 0–1     | hydration level                    |
| `:caffeine-mg`    | mg      | active caffeine in the system      |
| `:sleep-quality`  | 0–1     | quality of last night's sleep      |
| `:stress-level`   | 0–1     | current stress level               |

Blood pressure is intentionally stored as **two flat parameters** rather than a
nested map. This keeps every parameter comparable by the same generic function,
with no special cases in the analysis code.

### Two kinds of bounds — and why they are separate

The project distinguishes two questions that are easy to conflate:

| | `model/event.clj` (Malli schema) | `model/range.clj` (reference ranges) |
|---|---|---|
| Question | *Is this a physically possible reading?* | *Is this a healthy value?* |
| `:pressure-upper` | 60–250 | 90–180 |
| If it fails | the data is corrupt — reject it | the user gets a recommendation |

A systolic reading of 190 is a real measurement that deserves a warning; a
reading of 600 is a broken sensor. The schema guards the boundary of the system;
the ranges drive the advice. Mixing them would mean either rejecting valid
warnings or acting on garbage data.

---

## 3) Core Decision Logic

The pipeline is three pure functions, each doing one job:

```
event (numbers)  →  analyze-event  →  analysis (statuses)  →  recommend  →  messages (strings)
```

### Step 1 — `check-parameter`

Classifies a single value against its reference range:

```clojure
(check-parameter :blood-sugar 9.2)   ;=> :high
(check-parameter :blood-sugar 5.5)   ;=> :ok
(check-parameter :blood-sugar 3.0)   ;=> :low
(check-parameter :unknown-key 42)    ;=> :unknown
```

### Step 2 — `analyze-event`

Reduces a whole reading down to **only the parameters that need attention**.
Values that are `:ok` and parameters with no defined range (`:unknown`) are
dropped, so the result is a compact statement of what is wrong:

```clojure
(analyze-event {:blood-sugar 9.2 :hydration 0.3 :heart-rate 70})
;=> {:blood-sugar :high, :hydration :low}
```

### Step 3 — `recommend`

Translates the analysis into user-facing text:

```clojure
(recommend {:blood-sugar :high})
;=> ("Your blood sugar spiked. Wait 30-40 minutes — an energy crash is coming.")
```

### Design decision: rules as data, not as code

Neither the ranges nor the messages are hard-coded into `cond` branches. Both
live in plain maps, which are then interpreted by a single generic function.

```clojure
;; model/range.clj
{:blood-sugar {:low 3.9 :high 7.8 :unit "mmol/L"} ...}

;; model/recommendation.clj — note the composite vector key
{[:blood-sugar :high] "Your blood sugar spiked. ..."
 [:blood-sugar :low]  "Your blood sugar is low — ..." ...}
```

Adding a new tracked parameter means adding two map entries, not editing any
decision code. Using a vector `[:parameter :status]` as the map key works
because Clojure collections compare by value, so no wrapper type or string
concatenation is needed to build a composite key.

### Design decision: injected configuration via multiple arities

Every decision function accepts its configuration as an explicit argument, with
a convenience arity that supplies the default:

```clojure
(check-parameter :heart-rate 165)                 ; default reference ranges
(check-parameter :heart-rate 165 athlete-ranges)  ; caller-supplied ranges
```

This keeps the functions **pure** — the result depends only on the arguments,
never on hidden global state. Two consequences follow:

- **Tests supply their own ranges and messages**, so they neither depend on nor
  break when the production values change.
- **Per-user calibration becomes possible** — an athlete and a sedentary user
  can be evaluated by the same code with different ranges.

---

## 4) State Management

Current user state lives in a single `atom`:

```clojure
{:current-event   {...}   ; the latest reading
 :analysis        {...}   ; what is out of range
 :recommendations [...]   ; messages for the user
 :history         [...]   ; every reading so far
 :updated-at      ...}    ; when the state last advanced
```

### Design decision: pure transition separated from mutation

The state transition is a pure function; the mutation is a one-line wrapper
around it:

```clojure
(defn apply-event [state event] ...)          ; pure: (state, event) -> state
(defn ingest! [event] (swap! app-state apply-event event))
```

`apply-event` never touches the atom, which makes it directly testable and
composable. Because its shape is `(accumulator, input) -> accumulator`, state
can also be rebuilt from a list of readings with no atom involved at all:

```clojure
(reduce apply-event initial-state [e1 e2 e3])
```

The trailing `!` marks functions that mutate state, per Clojure convention.

Data inside the atom stays immutable — `swap!` computes a new state map and
swaps the reference, rather than mutating the old map in place. A reader holding
the previous state keeps a consistent snapshot, which is what makes concurrent
access safe without locking.

---

## 5) Example Run

```clojure
(require '[fitness-joy.mock.generator :as gen]
         '[fitness-joy.state.state :as state])

(state/reset-state!)

(dotimes [i 3]
  (state/ingest! (gen/random-event))
  (println (str "\n--- reading " (inc i) " ---"))
  (if (state/healthy?)
    (println "  All good — go train.")
    (doseq [m (state/current-recommendations)]
      (println "  •" m))))
```

Output (values are random, so runs differ):

```
--- reading 1 ---
  • Your blood sugar spiked. Wait 30-40 minutes — an energy crash is coming.
  • You're dehydrated — drink water with electrolytes before you head out.

--- reading 2 ---
  All good — go train.

--- reading 3 ---
  • Your stress level is high. Take a 15-minute walk instead of an intense session.
  • Too much caffeine in your system. Wait for it to drop before training.
```

---

## 6) Validation (Malli)

Domain structures are validated against Malli schemas before entering the
decision pipeline:

```clojure
(event/valid? some-map)     ;=> true / false
(event/explain some-map)    ;=> nil if valid, otherwise a description of the failure
```

Unlike annotation-driven validation, a Malli schema is **ordinary data** — a
vector that can be inspected, transformed, or reused at runtime:

```clojure
event/Event
;=> [:map [:heart-rate [:int {:min 30 :max 220}]] ...]
```

Validation is also explicit rather than implicit: nothing is checked until
`valid?` is called, so it is always clear where in the pipeline validation
happens.

---

## 7) Simulated Data

No open dataset of live wearable telemetry was available, so readings are
generated locally in `mock/generator.clj`:

```clojure
(gen/random-event)
;=> {:heart-rate 88, :blood-sugar 9.4, :hydration 0.4, ...}
```

### Why local generation instead of a mock HTTP service

A hosted mock (mocki.io and similar) returns a **fixed** payload — the same
values on every call — which is useless for exercising the decision logic. The
alternatives were a dynamic mock service or local generation; local generation
was chosen because it:

- requires no network, so tests never fail because a service is down,
- keeps the generator in the repository as reviewable code rather than as
  third-party configuration,
- is fast enough to produce hundreds of readings for property-style tests.

`clj-http` and `cheshire` remain as dependencies for an HTTP-backed source, so
the generator can be swapped for a real feed behind the same interface.

### Generator bounds and distribution

The generator deliberately produces values **on both sides of every reference
range**, so that `:low`, `:ok`, and `:high` all occur. An earlier version
generated only values inside the healthy range for blood pressure, which meant
the corresponding recommendation could never fire — a bug found by checking
status frequencies rather than by reading the code:

```clojure
(->> (repeatedly 3000 gen/random-event)
     (map #(rules/check-parameter :blood-sugar (:blood-sugar %)))
     frequencies)
;=> {:low 1010, :ok 995, :high 995}
```

---

## 8) Project Structure

```
src/fitness_joy/
├── core.clj                    ; entry point (-main)
├── model/
│   ├── user.clj                ; User schema
│   ├── event.clj                ; Event schema + valid? / explain
│   ├── range.clj                ; reference ranges per parameter
│   └── recommendation.clj       ; [parameter status] -> message
├── decision/
│   └── rules.clj                ; check-parameter, analyze-event, recommend
├── state/
│   └── state.clj                ; atom, apply-event, ingest!, readers
└── mock/
    └── generator.clj            ; synthetic reading generation

test/fitness_joy/
├── rules_test.clj               ; decision logic
├── state_test.clj               ; state transitions
└── generator_test.clj           ; generator properties
```

The split follows a single rule: `model/` holds **what the data is**,
`decision/` holds **how it is judged**, `state/state.clj` holds **how it changes
over time**. Reference ranges are configuration, not domain shape, so they sit
in `model/` as data but are never interpreted there.

---

## 9) Setup and Running

Requires [Leiningen](https://leiningen.org/) and Java 21+.

Install dependencies:

```bash
lein deps
```

Run:

```bash
lein run
```

Work interactively (the primary way to use the project):

```bash
lein repl
```

Build a standalone JAR:

```bash
lein uberjar
java -jar target/uberjar/fitness-joy-0.1.0-SNAPSHOT-standalone.jar
```

---

## 10) Testing Strategy (Midje)

```bash
lein midje              # run once
lein midje :autotest    # re-run on file change
```

### What is tested

**Decision logic** (`rules_test.clj`) — classification below, above, and inside
each range; boundary values; unknown parameters; multiple simultaneous issues;
the fallback to default configuration.

**State transitions** (`state_test.clj`) — that `apply-event` fills in analysis
and recommendations, appends to history, and **does not mutate its input**
(immutability is asserted, not assumed).

**Generator properties** (`generator_test.clj`) — rather than asserting fixed
values, which is impossible for random output, the tests assert *properties*:
every generated reading validates against the schema, contains the expected
keys, and stays within its declared bounds.

```clojure
(fact "always produces a valid event"
  (every? event/valid? (repeatedly 500 gen/random-event)) => true)
```

### How test independence is achieved

Tests pass their own ranges and messages instead of using the production maps,
which is what the multiple-arity design in section 3 exists for:

```clojure
(def test-ranges {:blood-sugar {:low 3.9 :high 7.8}})

(fact "value above the upper bound is :high"
  (rules/check-parameter :blood-sugar 9.2 test-ranges) => :high)
```

No test requires network access, API keys, or a database.

---

## 11) Challenges Faced

### 1) Schema bounds and generator bounds drifting apart

The property test `(every? event/valid? (repeatedly 500 random-event))` failed
intermittently. Because the failing reading is discarded immediately, a boolean
result gives nothing to debug.

**Solution:** using `explain` to surface the actual failure instead of a bare
`false`, the culprit was a parameter whose generator range exceeded its schema
maximum. This also clarified the schema/range distinction in section 2 — the
fix belonged in one place, not both.

```clojure
(->> (repeatedly 500 gen/random-event)
     (remove event/valid?)
     first
     event/explain)
```

### 2) Unreachable recommendations

Some recommendations could never be produced, because the generator's range for
that parameter never crossed the corresponding reference bound. The code was
correct in isolation; the combination was not.

**Solution:** verifying **status distribution** rather than individual values
(section 7), and widening the generator so all three statuses occur.

### 3) Rational division

Writing a blood-pressure range as `90/60` did not produce two values or an
integer, but a `Ratio` — Clojure's exact rational type — which silently reduced
to `3/2`. The lower bound ended up larger than the upper one, and comparisons
returned nonsense without raising an error.

**Solution:** representing pressure as two independent scalar parameters
(section 2), and using explicit doubles wherever a non-integer is intended.

### 4) Reproducibility of random tests

A test over randomly generated data cannot be reproduced once it fails, since
the next run produces different data.

**Current status:** the generator is seedable-by-design (the random source is
isolated to one helper function), but seeding is not yet wired through. This is
noted as future work rather than claimed as done.

---

## 12) Future Work

Planned but not implemented within the project timeframe:

- **Persistence** — write history to disk (`spit`/`slurp` with EDN, or Datahike
  for temporal queries), enabling weekly and monthly reports.
- **Asynchronous ingestion** — replace the synchronous loop with a
  `core.async` channel, modelling sensor and consumer as genuinely independent
  processes.
- **Memoisation** — cache expensive aggregate computations such as weekly
  averages with `memoize`.
- **Benchmarking** — measure the effect of memoisation with `criterium`.
- **Combined rules** — recommendations for *co-occurring* conditions, where the
  combination means more than the sum of its parts (for example, high caffeine
  together with poor sleep indicates masked fatigue rather than two separate
  problems).
- **Bounded history** — cap in-memory history and offload older readings to
  persistent storage.

`core.async` and `criterium` are already declared as dependencies in
preparation for this work.

---

## 13) References

- Clojure Documentation — https://clojure.org/reference/documentation
- Leiningen — https://leiningen.org/
- Malli — https://github.com/metosin/malli
- Midje — https://github.com/marick/Midje
- core.async — https://github.com/clojure/core.async
- Criterium — https://github.com/hugoduncan/criterium
- Datahike — https://github.com/replikativ/datahike

---

## Use of AI

AI tools were used as a **learning aid**, in the form of a step-by-step tutorial
dialogue while building the project. Their role was:

- explaining Clojure concepts (`reduce-kv`, `keep`, destructuring, atoms,
  arities, threading macros, metadata) with comparisons to Java/Spring, which is
  my prior background;
- reviewing code I had written and pointing out errors — for example the
  `Ratio` issue in section 11, and passing a namespace alias where a map value
  was required;
- discussing design trade-offs, such as whether configuration should be a
  function argument or a namespace-level constant;
- language assistance for the English strings and this README.

Design decisions, the project concept, and the code in this repository are my
own. AI was consulted the way one would consult a tutor: the explanation was
given, and I wrote the implementation.

---

## Author

David Stepanić
