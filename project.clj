(defproject
  blog
  "0.1.0-SNAPSHOT"
  :repl-options
  {:init-ns blog.repl}
  :dependencies
  [[ring-server "0.3.1"]
   [com.taoensso/timbre "3.0.0"]
   [clj-time "0.6.0"]
   [environ "0.4.0"]
   [markdown-clj "0.9.41"]
   [korma "0.3.0-RC6"]
   [com.taoensso/tower "2.0.1"]
   [org.clojure/clojure "1.5.1"]
   [log4j
    "1.2.17"
    :exclusions
    [javax.mail/mail
     javax.jms/jms
     com.sun.jdmk/jmxtools
     com.sun.jmx/jmxri]]
   [lib-noir "0.8.0"]
   [compojure "1.1.6"]
   [selmer "0.5.9"]]
  :immutant {:context-path "/"}
  :ring
  {:handler blog.handler/app,
   :init blog.handler/init,
   :destroy blog.handler/destroy}
  :profiles
  {:uberjar {:aot :all},
   :production
   {:ring
    {:open-browser? false, :stacktraces? false, :auto-reload? false}},
   :dev
   {:dependencies [[ring-mock "0.1.5"] [ring/ring-devel "1.2.1"]],
    :env {:dev true}}}
  :url
  "http://example.com/FIXME"
  :plugins
  [[lein-ring "0.8.10"] [lein-environ "0.4.0"]]
  :description
  "A simple markdown-powered blog."
  :min-lein-version "2.0.0")
