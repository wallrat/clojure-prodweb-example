(ns myapp.server
  (:gen-class)
  (:use ring.adapter.jetty clojure.pprint)
  (:require
   [compojure.route :as route]
   [clojure.string]
   [clojure.java.io]
   [clojure.java.shell]))

;; env
(defn- keywordize [s]
  (-> (clojure.string/lower-case s)
      (clojure.string/replace "_" "-")
      (keyword)))

(defn- read-system-env []
  (->> (System/getenv)
       (map (fn [[k v]] [(keywordize k) v]))
       (into {})))

(defn- read-env-file []
  (let [env-file (clojure.java.io/file "env.clj")]
    (if (.exists env-file)
      (read-string (slurp env-file)))))

(def GIT-SHA1
  (let [r (.getResourceAsStream (-> *ns* .getClass .getClassLoader) "git-sha1")]
    (if r (slurp r) nil)))

(def ^{:doc "A map of environment variables."}
  env
  (merge
   (read-env-file)
   (read-system-env)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn log [t & m] (println (str "[" (name t) "] " (clojure.string/join m))))


;; simple static file server
(def app (route/files "/" {:allow-symlinks? true :root "./public"}))

(def server (atom nil))

(defn start-httpd [port]
  (log :info "starting HTTP on port " port)
  (reset! server (run-jetty #'app {:port port :join? false})))

;; in production
(def production? (env :production false))
(def development? (not production?))


(defn unpack-resources []
  (when production?
    (log :info "Unpacking resources from " (env :jar))

    (log :info "Unpacking public.zip")
    (clojure.java.shell/sh "bash" "-c" (str "unzip -o " (env :jar) " public.zip"))

    (log :info "Unpacking ./public")
    (clojure.java.shell/sh "bash" "-c" "unzip -o public.zip")))

;; main
(defn -main [& args]
  (log :info "running version " GIT-SHA1)
  (unpack-resources)
  (start-httpd (read-string (env :port "9001"))))


;; logging
