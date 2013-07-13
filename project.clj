(defproject myapp "0.1.0"
  :description "FIXME: write description"
  :url "http://github.com/wallrat/clojure-prodweb-example"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [log4j/log4j "1.2.16"]
                 [compojure "1.1.5"]
                 [ring/ring-core "1.1.8"]
                 [ring/ring-devel "1.1.8"]
                 [ring/ring-jetty-adapter "1.1.8"]]

  :dev-dependencies [[clojure-source "1.5.1"]]

  :aot [myapp.server]
  :main myapp.server
  )
