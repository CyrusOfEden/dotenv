(ns dotenv.core
  (:require [clojure.edn :as edn]
            [clojure.string :as str]
            [clojure.java.io :as io])
  (:import [java.io PushbackReader]
           [java.lang RuntimeException]))

(defn- keywordize [s]
  (-> (str/lower-case s)
      (str/replace #"[\.|_]" "-")
      (keyword)))

(defn- read-env-map [m]
  (let [xf (fn [[k v]] [(keywordize k) v])]
    (into {} (map xf) m)))

(defn- print-err [& args]
  (binding [*out* *err*]
    (apply println args)))

(defn- make-syntax-err [fname err]
  (str "There is a syntax error in the file: " fname ".\n"
       "As such, dotenv.core/env does not contain variables defined in " fname ".\n\n"
       "The full error has been is visible below:\n" err))

(defn- read-env-file [fname]
  (when (.exists (io/file fname))
    (try
      (edn/read (PushbackReader. (io/reader fname)))
      (catch RuntimeException err
        (print-err (make-syntax-err fname err))))))

(defn get-env []
  (let [env-map (merge (read-env-map (System/getenv))
                       (read-env-map (System/getProperties))
                       (read-env-file ".env"))
        cur-env (or (env-map :lein-env)
                    (env-map :boot-env)
                    "development")
        cur-map (if-let [res (env-map (keyword cur-env))]
                  res
                  (read-env-file (str ".env." cur-env)))
        util-map (assoc {}
                        :current-env cur-env
                        (keyword (str cur-env "?")) true)]
    (merge env-map cur-map util-map)))

(def env (get-env))

