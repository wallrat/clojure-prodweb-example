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

(defn get-git-sha1 []
  (let [r (.getResourceAsStream (-> *ns* .getClass .getClassLoader) "git-sha1")]
    (if r (slurp r) nil)))

(def ^{:doc "A map of environment variables."}
  env
  (merge
   {:git-sha1 (get-git-sha1)}
   (read-env-file)
   (read-system-env)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn log [t & m] (println (str "[" t "]" (clojure.string/join m))))


;; simple static file server
(def app (route/files "/" {:allow-symlinks? true :root "./public"}))

(def server (atom nil))

(defn start-httpd [port]
  (println "starting HTTP on port" port)
  (reset! server (run-jetty #'app {:port port :join? false})))

;; in production
(def production? (env :production false))
(def development? (not production?))


(defn unpack-resources []
  (when production?
    (let [jarfile (env :jar)]
      (log :info "Unpacking resources from" jarfile)

      (if (.isFile (java.io.File. jarfile))
        (do
          (log :info "Unpacking public.zip")
          (clojure.java.shell/sh "bash" "-c" (str "unzip -o " jarfile " public.zip"))

          (log :info "Unpacking ./public")
          (clojure.java.shell/sh "bash" "-c" "unzip -o public.zip"))
        (do
          (log :error jarfile "not found, can't extract public")
          (System/exit 1))))))


;; main
(defn -main [& args]
  (println "Hello, World!")
  ;; (pprint env)
  (unpack-resources)
  (start-httpd (read-string (env :port "9001"))))


;; logging
