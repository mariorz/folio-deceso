(defproject folio-deceso "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/data.csv "0.1.4"]
                 [nrepl "0.7.0-beta1"]
                 [metasoarous/oz "1.6.0-alpha6"]]
  :plugins [[cider/cider-nrepl "0.24.0"]]
  :middleware [cider-nrepl.plugin/middleware]
  :main ^:skip-aot folio-deceso.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
