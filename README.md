# dotenv [![Clojars Project](https://img.shields.io/clojars/v/com.knrz/dotenv.svg)](https://clojars.org/com.knrz/dotenv)

A souped-upped version of what you've come to expect in a `.env` file loader, as per the [12-factor app methodology for configs](http://12factor.net/config). What's different? `dotenv` expects the env files to be in the amazing [EDN format](https://github.com/edn-format/edn) for Clojure apps. Long story short: You've got a Clojure map as your config.

Store your configuration in an env file, and keep it checked out of source control. All there is to it.

## Installation

Include the following dependency in your `project.clj` file:

```clojure
:dependencies [[com.knrz/dotenv "1.0.0"]]
```

## Usage

It's simpler than you think!

```clojure
(require '[dotenv.core :refer (env)])
;; env is now a map of every. single. environment. variable. simple.
;; Check out the Read This section for stuff you should read.
```

If you ever need to reload the env, a simple call to `dotenv.core/get-env` reloads everything. For example:

```clojure
(require '[dotenv.core :as dotenv])

(def env (atom dotenv/env))

(defn get-env []
  @env)

(defn reload-env! []
  (reset! env (dotenv/get-env)))
```

(P.S. Experienced Clojurians, let me know if I've done anything wrong in the above example.)

## Read This

The env map contains any variables set from the following sources, with later sources overwriting earlier ones:

1. `(System/getenv)`. Values are strings.
2. `(System/getProperties)`. Values are strings.
3. `.env` file in root project directory. Values are whatever they are in edn.
4. `.env.(current-env)` file in the root project directory, say, `.env.development`. Values are whatever they are in edn.

For convenience's sake, all env var names are normalized. They are lowercased, underscores (`_`) and periods (`.`) in variable names are replaced by dashes (`-`), and keywordized. For example, if you were to `echo $HOME` in bash, you'd `(println (:home env))` with dotenv. Whereas you would `System.getProperty("user.dir")` in Java, you would `(:user-dir env)` with dotenv.

current-env` is a string resolved through the `LEIN_ENV` or `BOOT_ENV` environment variables. It defaults to `"development"`.

The `:current-env` key houses the current environment. Also, `:(current-env)?` (e.g. `:test?`) is set to `true` for the current environment.

## Why this? Why not environ?

This was born out of frustration with not understanding how [environ](https://github.com/weavejester/environ), another environment management system, worked. That, and its interop with Leiningen had a certain bug I can't seem to remember.

## Is it any good?

Yes.

