(ns db0.fault.interface)

(defn fault?
  "Check if a value is a fault object"
  [value]
  (= :fault (:status value)))

(defn ok?
  "Check if a value is a ok object"
  [value]
  (not (fault? value)))

(defn fault
  "Create a fault object with a error information"
  [message {:keys [context exception trace] :as _fault-info}]
  {:message message
   :status :fault
   :context context
   :exception exception
   :trace (conj [] trace)})

(defmacro ok->
  "Apply a sequence of functions to a value, if no fault is found"
  [value & forms]
  (loop [v value, forms forms]
    (if (and forms (ok? v))
      (let [form (first forms)
            threaded (if (seq? form)
                       `(~(first form) ~v ~@(next form))
                       (list form v))]
        (recur threaded (next forms)))
      v)))

(defmacro ok->>
  "Apply a sequence of functions to a value, if no fault is found"
  [value & forms]
  (loop [v value, forms forms]
    (if (and forms (ok? v))
      (let [form (first forms)
            threaded (if (seq? form)
                       `(~(first form) ~@(next form) ~v)
                       (list form v))]
        (recur threaded (next forms)))
      v)))

(defmacro try-ok->
  "Apply a sequence of functions to a value inserting it as a first argument
   with exception handling, if no fault is found"
  [& body]
  `(try
     (ok-> ~@body)
     (catch Exception e#
       (fault "error" {:exception e#}))))

(defmacro try-ok->>
  "Apply a sequence of functions to a value inserting it as a last argument
   with exception handling, if no fault is found"
  [& body]
  `(try
     (ok->> ~@body)
     (catch Exception e#
       (fault "exception occurred"
              {:exception e#}))))