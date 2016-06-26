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
  (str "There is a syntax error in the file: " fname ".\n\n"
       "The full error is below:\n" err))

(defn- read-config-file [fname]
  (when (.exists (io/file fname))
    (try
      (edn/read (PushbackReader. (io/reader fname)))
      (catch RuntimeException err
        (print-err (make-syntax-err fname err))))))

(defn current-env [env]
  (or (env :lein-env)
      (env :boot-env)
      "development"))

(defn load-env []
  (let [srcs [(System/getenv) (Systme/getProperties)]]
    (into {} (map read-env-map) srcs)))

(defn load-config []
  (let [env (load-env)
        cur (current-env env)]
    (merge (read-config-file "config.edn")
           (read-config-file (str "config." cur ".edn")))))

(def env (load-env))
(def config (load-config))

