(ns folio-deceso.core
  (:require [oz.core :as oz]
            [semantic-csv.core :as sc]
            [clj-http.client :as http]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-time.local :as l]
            [clojure.data.csv :as csv]
            [folio-deceso.viz :as viz]))


(defn locate-acta
  [year juzgado acta]
  (let [map-url "http://www.rcivil.cdmx.gob.mx/solicitudactas/busqueda/resultados"
        headers {:form-params
                 {:clase_acta "DEFUNCION"
                  :anio year
                  :juzgado juzgado
                  :libro ""
                  :nu_acta acta}}
        _ (println "locating acta:" acta "in juzgado:" juzgado)
        data (http/post map-url headers)
        reason-msg (:reason-phrase data)
        date-regex #"\d{1,2}/\d{1,2}/\d{4}"
        date (clojure.core/re-find date-regex (:body data))
        found (= reason-msg "OK")]
    #_data
    (Thread/sleep 3000)
    {:year year
     :date_def date
     :juzgado juzgado
     :acta acta
     :found found}))

(comment
  (def relevant-juzgados
    (filter #(:found %)
            (map #(locate-acta "2020" (str %) "1") (range 1 52))))


  (def relevant-juzgados-2019
    (filter #(:found %)
            (map #(locate-acta "2019" (str %) "1") (range 1 52))))


  (def relevant-juzgados-2018
    (filter #(:found %)
            (map #(locate-acta "2018" (str %) "1") (range 1 52))))


  (def relevant-juzgados-2017
    (filter #(:found %)
            (map #(locate-acta "2017" (str %) "1") (range 1 52))))

  (def relevant-juzgados-2016
    (filter #(:found %)
            (map #(locate-acta "2016" (str %) "1") (range 1 52))))


  (with-open [w (clojure.java.io/writer "rj.edn")]
    (binding [*print-length* false
              *out* w]
      (pr relevant-juzgados)))

  (with-open [w (clojure.java.io/writer "resources/rj2019.edn")]
    (binding [*print-length* false
              *out* w]
      (pr relevant-juzgados-2019)))

  (with-open [w (clojure.java.io/writer "resources/rj2018.edn")]
    (binding [*print-length* false
              *out* w]
      (pr relevant-juzgados-2018)))

  (with-open [w (clojure.java.io/writer "resources/rj2017.edn")]
    (binding [*print-length* false
              *out* w]
      (pr relevant-juzgados-2017)))


  (def rj (edn/read-string (slurp "resources/rj.edn")))
  (def rj-2018 (edn/read-string (slurp "resources/rj2018.edn")))
  (def rj-2019 (edn/read-string (slurp "resources/rj2019.edn"))))


(defn last-acta
  ([year juzgado end]
   (last-acta year juzgado 0 end))
  ([year juzgado start end]
   (println "start:" start "end:" end)
   (if (= start (- end 1))
     start
     (let [half (int (/ (+ start end) 2))
           c (locate-acta year juzgado half)]
       (if (:found c)
         (recur year juzgado half end)
         (recur year juzgado start half))))))


(defn last-acta-for-month
  ([year month juzgado end]
   (last-acta-for-month year month juzgado 0 end))
  ([year month juzgado start end]
   (println "start:" start "end:" end "month:" month)
   (if (= start (- end 1))
     start
     (let [half (int (/ (+ start end) 2))
           c (locate-acta year juzgado half)]
       (if (and (:found c)
                (= (count (:date_def c)) 10)
                (<= (Integer. (re-find  #"\d+" (subs (:date_def c) 3 5))) month))
         (recur year month juzgado half end)
         (recur year month juzgado start half))))))


(defn counts-monthly
  [year rj]
  (map (fn [j] (map (fn [m] {:juzgado (:juzgado j)
                             :month m
                             :count (last-acta-for-month
                                     year m (:juzgado j) 20000)})
                    (range 1 12)))
       (filter :found rj)))


(comment
  (def cm2020 (counts-monthly "2020" rj))
  (def cm2019 (counts-monthly "2019" rj-2019))

  (with-open [w (clojure.java.io/writer "resources/cm2020.edn")]
      (binding [*print-length* false
                *out* w]
        (pr cm2020)))

  (with-open [w (clojure.java.io/writer "resources/cm2019.edn")]
      (binding [*print-length* false
                *out* w]
        (pr cm2019))))


(defn counts-year
  [year rj]
  (map (fn [j] {:juzgado (:juzgado j)
                :count (last-acta year (:juzgado j) 50000)})
       (filter #(:found %) rj)))


(comment
  (def cy2020 (counts-year "2020" rj))

  (with-open [w (clojure.java.io/writer "resources/cy2020.edn")]
    (binding [*print-length* false
              *out* w]
      (pr cy2020)))

  (def cy2019 (counts-year "2019" rj-2019))
  (with-open [w (clojure.java.io/writer "resources/cy2019.edn")]
    (binding [*print-length* false
              *out* w]
      (pr cy2019)))


  (def cy2018 (counts-year "2018" rj-2018))
  (with-open [w (clojure.java.io/writer "cy2018.edn")]
    (binding [*print-length* false
              *out* w]
      (pr cy2018)))

  (def cy2017 (counts-year "2017" relevant-juzgados-2017))

  (with-open [w (clojure.java.io/writer "resources/cy2017.edn")]
    (binding [*print-length* false
              *out* w]
      (pr cy2017)))


  (def cy2016 (counts-year "2016" relevant-juzgados-2016))

  (with-open [w (clojure.java.io/writer "resources/cy2016.edn")]
    (binding [*print-length* false
              *out* w]
      (pr cy2016)))

  (def cy2020c (edn/read-string (slurp "resources/cy2020.edn")))
  (def cy2019c (edn/read-string (slurp "resources/cy2019.edn")))
  (def cy2018c (edn/read-string (slurp "resources/cy2018.edn")))
  (def cy2017c (edn/read-string (slurp "resources/cy2017.edn")))
  (def cy2016c (edn/read-string (slurp "resources/cy2016.edn"))))


;; for good measure
(defn expand-count
  [m]
  (let [fwd (range (+ (:count m) 1)
                   (+ (:count m) 6))]
    (filter :found
            (map #(locate-acta "2018" (:juzgado m) %)
                 fwd))))

#_(map expand-count cy2018c)

;;;;;;;; samples
(comment
  ;; sample sizes for 95% CI, 2.5% Error
  ;; given count found for each juzgado
  (def sample-2020-14
    (let [limit (:count (first (filter #(= (:juzgado %) "14") cy2020)))]
      (take 375 (repeatedly #(rand-int limit)))))

  (def sample-2020-51
    (let [limit (:count (first (filter #(= (:juzgado %) "51") cy2020)))]
      (take 369 (repeatedly #(rand-int limit)))))

  (def sample-2020-18
    (let [limit (:count (first (filter #(= (:juzgado %) "18") cy2020)))]
      (take 367 (repeatedly #(rand-int limit)))))


  (def sample-result-14
    (map #(locate-acta "2020" "14" (str %)) sample-2020-15))

  (def sample-result-51
    (map #(locate-acta "2020" "51" (str %)) sample-2020-51))

  (def sample-result-18
    (map #(locate-acta "2020" "18" (str %)) sample-2020-18)))


(defn make-sample-map
  [r]
  {:year (nth r 0)
   :found (not (= "" (nth r 1)))
   :date_def (if (= "" (nth r 1))
               nil
               (nth r 1))
   :juzgado (nth r 2)
   :acta (nth r 3)})


(comment
  (def saved-sample-14
    (with-open [in-file (clojure.java.io/reader "resources/sample-juzgado14.csv")]
      (doall
       (map make-sample-map
            (rest (csv/read-csv in-file))))))

  (def saved-sample-18
    (with-open [in-file (clojure.java.io/reader "resources/sample-juzgado18.csv")]
      (doall
       (map make-sample-map
            (rest (csv/read-csv in-file))))))


  (def saved-sample-51
    (with-open [in-file (clojure.java.io/reader "resources/sample-juzgado51.csv")]
      (doall
       (map make-sample-map
            (rest (csv/read-csv in-file)))))))


(defn proportion-not-found
  [sample]
  (let [sample-size (count sample)
        nf (count (filter (comp not :found) sample))]
    (println "nf:" nf)
    (* 100.0 (/ nf sample-size))))

(defn proportion-wrong-year
  [sample]
  (let [sample-size (count sample)
        wy (count (filter (fn [a]
                            (and (not (nil? (:date_def a)))
                                 (not= "2020" (subs (:date_def a) 6))))
                          sample))]
    (println "wy:" wy)
    (* 100.0 (/ wy sample-size))))


(defn proportion-errors
  [sample]
  {:wrong-year (proportion-wrong-year sample)
   :skipped (proportion-not-found sample)})

;;;;;;;;;;


(defn write-csv-year [path row-data]
  (let [columns [:year :date_def :juzgado :acta]
        headers (map name columns)
        rows (mapv #(mapv % columns) row-data)]
    (with-open [file (io/writer path)]
      (csv/write-csv file (cons headers rows)))))

(defn write-csv-monthly [path row-data]
  (let [columns [:juzgado :month :count]
        headers (map name columns)
        rows (mapv #(mapv % columns) row-data)]
    (with-open [file (io/writer path)]
      (csv/write-csv file (cons headers rows)))))

(comment
  (def cm2019c (apply concat (edn/read-string (slurp "resources/cm2019.edn"))))
  (write-csv-monthly "resources/months2019.csv" cm2019c)
  (def cm2020c (apply concat (edn/read-string (slurp "resources/cm2020.edn"))))
  (write-csv-monthly "resources/months2020.csv" cm2020c)



  (def months2019
    (map (fn [[k v]] {:predicted false
                      :year "2019" :month k :count (apply + (map :count v))})
         (group-by :month cm2019c)))

  (def months2020
    (map (fn [[k v]] {:predicted false
                      :year "2020" :month k :count (apply + (map :count v))})
         (group-by :month cm2020c)))


  ;; Note we intentionally set year in the :date value here
  ;; for 2020 on all the series.
  ;; this is so that we can use a vega-lite temporal axis on
  ;; Chart 1, with the different series for each year aligned.

  ;;7,944
  ;;7,944+6,764=> 14,708
  ;;7,944+6,764+6,319=> 21,027
  ;;7,944+6,764+6,319+5,895=> 26,922
  ;;7,944+6,764+6,319+5,895+6,138=> 33,060
  (def imss2018
    [{:year "2018", :month 0, :count 0, :date "Jan/1/2020" :predicted false}
     {:year "2018", :month 1, :count 7944, :date "Jan/31/2020" :predicted false}
     {:year "2018", :month 2, :count 14708, :date "Feb/28/2020" :predicted false}
     {:year "2018", :month 3, :count 21027, :date "Mar/31/2020" :predicted false}
     {:year "2018", :month 4, :count 26922, :date "Apr/30/2020" :predicted false}
     {:year "2018", :month 5, :count 33060, :date "May/31/2020" :predicted false}])


  ;;6,779
  ;;6,779+6,462=> 13,241
  ;;6,779+6,462+7,115=> 20,356
  ;;6,779+6,462+7,115+6,204=> 26,560
  ;;6,779+6,462+7,115+6,204+6,148=> 32,708
  (def imss2017
    [{:year "2017", :month 0, :count 0, :date "Jan/1/2020" :predicted false}
     {:year "2017", :month 1, :count 6779, :date "Jan/31/2020" :predicted false}
     {:year "2017", :month 2, :count 13241, :date "Feb/28/2020" :predicted false}
     {:year "2017", :month 3, :count 20356, :date "Mar/31/2020" :predicted false}
     {:year "2017", :month 4, :count 26560, :date "Apr/30/2020" :predicted false}
     {:year "2017", :month 5, :count 32708, :date "May/31/2020" :predicted false}])


  ;; 7,115
  ;; 7,115+7,284=>14,399
  ;; 7,115+7,284+6,793=> 21,192
  ;; 7,115+7,284+6,793+5,902=> 27,094
  ;; 7,115+7,284+6,793+5,902+5,860=> 32,954
  (def imss2016
    [{:year "2016", :month 0, :count 0, :date "Jan/1/2020" :predicted false}
     {:year "2016", :month 1, :count 7115, :date "Jan/31/2020" :predicted false}
     {:year "2016", :month 2, :count 14399, :date "Feb/28/2020" :predicted false}
     {:year "2016", :month 3, :count 21192, :date "Mar/31/2020" :predicted false}
     {:year "2016", :month 4, :count 27094, :date "Apr/30/2020" :predicted false}
     {:year "2016", :month 5, :count 32954, :date "May/31/2020" :predicted false}])


  (def month-numbers-2019
    {1 "Jan/31/2020" 2 "Feb/28/2020" 3 "Mar/31/2020" 4 "Apr/30/2020" 5 "May/31/2020"})


  (def month-numbers-2020
    {1 "Jan/31/2020" 2 "Feb/28/2020" 3 "Mar/31/2020" 4 "Apr/30/2020" 4.5 "May/20/2020" 5 "May/31/2020"})



  (def month-items
    (concat
     imss2016
     imss2017
     imss2018
     (map (fn [x] (into x {:date (get month-numbers-2019 (:month x))}))
          (filter #(<= (:month %) 5) months2019))
     (concat (map (fn [x] (into x {:date (get month-numbers-2020 (:month x))})) months2020)
             [{:year "2019", :month 0,
               :count 0, :date "Jan/1/2020" :predicted false}
              {:year "2020", :month 0,
               :count 0, :date "Jan/1/2020" :predicted false}
              {:year "2020" :month 4.5 :count 39142 ;; TODO check this number
               :predicted true :date "May/20/2020"}])))


  ;;; Chart 1 (time series)
  (oz/view! (viz/chart1-line-plot month-items :date :count :year)))


(comment

  (def avg2016-2019
    (->> month-items
         (filter #(not= (:year %) "2020"))
         (group-by :month)
         (map (fn [[k v]] (into {:month (:month (first v))
                                 :date (:date (first v))
                                 :year "Promedio 2016-2019"
                                 :predicted false
                                 :count (int (* 1.0 (/ (apply + (map :count v))
                                                       (count v))))})))))

  (defn only-diff
    [all-prev current-val]
    (if (= (count all-prev) 0)
      [current-val]
      (conj all-prev
            (into current-val
                  {:count (- (:count current-val)
                             (apply + (map :count  all-prev)))}))))


  (def month-items-bars
    (concat
     (reduce only-diff [] imss2016)
     (reduce only-diff [] imss2017)
     (reduce only-diff [] imss2018)
     (reduce only-diff []
             (sort-by :month
                      (map (fn [x] (into x {:date (get month-numbers-2019 (:month x))}))
                           (filter #(<= (:month %) 5) months2019))))
     (reduce only-diff []
             (sort-by :month
                      (filter #(not= (:date %) "May/20/20202")
                              (map (fn [x] (into x {:date (get month-numbers-2020 (:month x))}))
                                   months2020))))))

  (def month-items-bars-avg
    (concat
     (reduce only-diff [] avg2016-2019)
     (reduce only-diff []
             (sort-by :month
                      (filter #(not= (:date %) "May/20/20202")
                              (map (fn [x] (into x {:date (get month-numbers-2020 (:month x))}))
                                   months2020))))))

  ;; unpublished chart
  (oz/view! (viz/grouped-bar-chart-avg (filter #(not= (:date %) "May/20/2020")
                                               month-items-bars-avg)
                                       :date :count :year))

  ;; unpublished net deaths per months by years
  (oz/view! (viz/grouped-bar-chart (filter #(and (not= (:date %) "May/20/2020")
                                                 (not= (:date %) "Jan/1/2020"))
                                           month-items-bars)
                                   :date :count :year)))


(comment
  (def months-spanish
    {1 "Enero"
     2 "Febrero"
     3 "Marzo"
     4 "Abril"
     4.5 "Mayo"
     5 "Mayo"})

  (def month-items-bars-diff
    (->> month-items-bars-avg
         (filter #(not= (:month %) 0))
         (group-by :month)
         (map (fn [[k v]] {:month (:month (first v))
                           :month-spanish (get months-spanish
                                               (:month (first v)))
                           :date (:date (first v))
                           :year "2020"
                           :diff (- (:count (second v))
                                    (:count (first v)))
                           :diff-p  (* 1.0 (- (/ (:count (second v))
                                                 (:count (first v)))
                                              1))
                           :predicted false}))))

  ;; Chart 2 net difference per month (without prediction)
  (oz/view!
   (viz/grouped-bar-chart-diff month-items-bars-diff :month-spanish :diff :year))


  ;; Chart 3 percentage diff per month (with prediction)
  (oz/view!
   (viz/grouped-bar-chart-diff-p month-items-bars-diff :month-spanish :diff-p :year)))


(comment

  ;; last two points from avg2016-2019:
  ;; (- 33146 27011)
  ;; (+ (/ (* 20.0 6135) 30) 27011)
  ;; we "crop" may's average to the first 20 day only.
  (def avg2016-2019-cropped
    (concat [{:month 0, :date "Jan/1/2020", :year "Promedio 2016-2019",
              :predicted false, :count 0}]
            (take 4 avg2016-2019)
            [{:month 5, :date "May/20/2020", :year "Promedio 2016-2019",
              :predicted false, :count (+ (/ (* 20.0 6135) 30) 27011)}]))


  (def avg2016-2019-full
    (concat [{:month 0, :date "Jan/1/2020", :year "Promedio 2016-2019",
              :predicted false, :count 0}]
            avg2016-2019))




  ;; values for confirmados and sospechosos
  ;; from db published at june 3
  ;; with fecha_def at or before May 31
  (def total-items
    [{:count (- (:count (last months2020))
                (:count (last avg2016-2019-full)))
      :cat "Exceso de Mortalidad"}
     {:count 3174 :cat "Confirmados"}
     {:count (+ 3174 265) :cat "Confirmados+Sospechosos"}])

  ;; Chart 4, total excess mortality
  (oz/view! (viz/chart4-bar-chart total-items :cat :count))

  (defn parse-int [s]
    (Integer/parseInt (re-find #"\A-?\d+" s)))


  (defn parse-mx-date
    [s]
    (f/parse (f/formatter "dd/MM/yy") s))

  (defn format-utcdates
    [s]
    (f/unparse (f/formatter "MMMM/dd/yyyy") (parse-mx-date s)))


  (def soft-chart-data
    (with-open [in-file (io/reader "softchart.csv")]
      (->>
       (csv/read-csv in-file)
       (sc/remove-comments)
       (sc/mappify)
       (sc/cast-with {:xssmortality parse-int
                      :confirmed parse-int
                      :suspects parse-int
                      :cases parse-int
                      :day parse-int
                      :date format-utcdates})
       (map (fn [x] [{:date (:date x)
                      :count (:cases x)
                      :type "Confirmados+Sospechosos"}
                     {:date (:date x)
                      :count (:xssmortality x)
                      :type "Exceso de Mortalidad"}]))
       (apply concat)
       (drop 110)
       doall)))

  (defn accum
    [all-prev current-val]
    (if (= (count all-prev) 0)
      [current-val]
      (conj all-prev
            (into current-val
                  {:diff (+ (:diff current-val)
                            (:diff (last all-prev)))}))))


  (def accumulated-points-rc
    (concat [{:year "2020" :month 4.5 :diff 8072 ;; May 20 number from first article
              :type "xssmortality-points" :date "May/20/2020"}]
            (drop 1 (map (fn [r] {:date (:date r) :diff (:diff r) :type "xssmortality-points"})
                         (reduce accum []
                                 (filter #(= (:year %) "2020") month-items-bars-diff))))))

  ;; chart 5 unpublished
  (oz/view! (viz/chart5-line-plot (concat
                                   accumulated-points-rc
                                   soft-chart-data) :date :count :type))

  ;; us state data from
  ;; https://www.washingtonpost.com/graphics/2020/investigations/coronavirus-excess-deaths-may/
  (def table-data
    [{:place "Indiana" :deaths 14386 :xssdeaths 1730 :covid-deaths 1482}
     {:place "Nuevo Hampshire" :deaths 2590 :xssdeaths 168 :covid-deaths 132}
     {:place "Nueva York (Cuidad)" :deaths 33897 :xssdeaths 23615 :covid-deaths 17135}
     {:place "Utah" :deaths 3922 :xssdeaths 102 :covid-deaths 73}
     {:place "Florida" :deaths 44306 :xssdeaths 2750 :covid-deaths 1838}
     {:place "Oregon" :deaths 7209 :xssdeaths 247 :covid-deaths 159}
     {:place "California" :deaths 56590 :xssdeaths 4753 :covid-deaths 2771}
     {:place "Texas" :deaths 40978 :xssdeaths 2870 :covid-deaths 1129}
     {:place "Carolina del Sur" :deaths 10514 :xssdeaths 1087 :covid-deaths 326}
     {:place "CDMX"
      :deaths (:count (last months2020))
      :xssdeaths (:diff (last accumulated-points-rc))
      :covid-deaths (:count (last total-items))}])

  (oz/view! (viz/single-bar-percent {:proportion "75"
                                     :place "CDMX"}))

  ;; table 1
  (oz/view! (viz/covid-xss-table table-data)))



(defn -main
  [& args]
  (oz/start-server! 8888))
