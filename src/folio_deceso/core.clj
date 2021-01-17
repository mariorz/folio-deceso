
(ns folio-deceso.core
  (:require [clj-time.format :as f]
            [clojure.data.csv :as csv]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [folio-deceso.viz :as viz]
            [oz.core :as oz]
            [semantic-csv.core :as sc]))



(defn parse-int
  [s]
  (try
    (Integer/parseInt (re-find #"\A-?\d+" s))
    (catch Exception e (str "error:" s))))


(defn parse-mx-date
  [s]
  (f/parse (f/formatter "dd/MM/yy") s))

(defn format-utcdates
  [s]
  (f/unparse (f/formatter "MMMM/dd/yyyy") (parse-mx-date s)))


(defn parse-us-date
  [s]
  (f/parse (f/formatter "MM/dd/yyyy") s))

(defn mxts-format
  [s]
  (f/unparse (f/formatter "dd-MM-yyyy") (parse-us-date s)))



(comment
  (oz/start-server! 8888)
  (def current-week 52)

  (def cy2020c (edn/read-string (slurp "resources/cy2020.edn")))
  (def cy2019c (edn/read-string (slurp "resources/cy2019.edn")))
  (def cy2018c (edn/read-string (slurp "resources/cy2018.edn")))
  (def cy2017c (edn/read-string (slurp "resources/cy2017.edn")))
  (def cy2016c (edn/read-string (slurp "resources/cy2016.edn"))))




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
  ;;(write-csv-monthly "resources/months2019.csv" cm2019c)
  (def cm2020c (apply concat (edn/read-string (slurp "resources/cm2020.edn"))))
  ;;(write-csv-monthly "resources/months2020.csv" cm2020c)


  (def months2019
    (map (fn [[k v]] {:predicted false
                      :year "2019" :month k :count (apply + (map :count v))})
         (group-by :month cm2019c)))

  (def months2020
    (map (fn [[k v]] {:predicted false
                      :year "2020" :month k :count (apply + (map :count v))})
         (group-by :month cm2020c)))


  ;; Note we intentionally set year in the :date value here
  ;; to "2020" on all the series.
  ;; this is so that we can use a vega-lite temporal axis on
  ;; Chart 1, with the different series for each year aligned.

  (def month-numbers-2019
    {1 "Jan/31/2020" 2 "Feb/28/2020" 3 "Mar/31/2020" 4 "Apr/30/2020"
     5 "May/31/2020" 6 "June/30/2020" 7 "July/31/2020" 8 "Aug/31/2020"
     9 "Sep/30/2020" 10 "Oct/31/2020" 11 "Nov/30/2020" 12 "Dec/31/2020"})


  (def month-numbers-2020
    {1 "Jan/31/2020" 2 "Feb/28/2020" 3 "Mar/31/2020" 4 "Apr/30/2020"
     4.5 "May/20/2020" 5 "May/31/2020" 6 "June/30/2020" 7 "July/30/2020"
     8 "Aug/30/2020" 9 "Sep/30/2020"})


  (defn homogenize-year
    [s]
    (f/unparse
     (f/formatter "MM/dd/2020")
     (f/parse (f/formatter "dd/MM/yyyy") s)))

  (def inegi-days
    (with-open [in-file (io/reader "resources/inegi-days.csv")]
      (->>
       (csv/read-csv in-file)
       (sc/remove-comments)
       (sc/mappify)
       (sc/cast-with {:year identity
                      :week parse-int
                      :accum parse-int
                      :accum-week parse-int
                      :new parse-int
                      :last-doy parse-int
                      :date homogenize-year})
       (map #(into % {:predicted false :count (:accum %)}))
       doall)))


  (def inegi2016
    (concat
     (drop-last
      1 (filter #(and (= (:year %) "2016") (= (:last-doy %) 1)) inegi-days))
     [(last (drop-last 1 (filter #(= (:year %) "2016") inegi-days)))]))

  (def inegi2017
    (filter #(and (= (:year %) "2017") (= (:last-doy %) 1)) inegi-days))

  (def inegi2018
    (concat
     (drop-last
      1 (filter #(and (= (:year %) "2018") (= (:last-doy %) 1)) inegi-days))
     [(last (filter #(= (:year %) "2018") inegi-days))]))

  (def inegi2019
    (concat
     (filter #(and (= (:year %) "2019") (= (:last-doy %) 1)) inegi-days)
     [(last (filter #(= (:year %) "2019") inegi-days))]))


  (def weeks
    (with-open [in-file (io/reader "resources/weeks.csv")]
      (->> (csv/read-csv in-file)
           (sc/remove-comments)
           (sc/mappify)
           (sc/cast-with {:week parse-int
                          :2016 parse-int
                          :2017 parse-int
                          :2018 parse-int
                          :2019 parse-int
                          :2020 parse-int
                          :2019-accum parse-int
                          :2020-accum parse-int}))))


  (def weeks-adip
    (with-open [in-file (io/reader "resources/weeksadip.csv")]
      (->> (csv/read-csv in-file)
           (sc/remove-comments)
           (sc/mappify)
           (sc/cast-with {:week parse-int
                          :2016 parse-int
                          :2017 parse-int
                          :2018 parse-int
                          :2019 parse-int
                          :2020 parse-int
                          :2019-accum parse-int
                          :2020-accum parse-int}))))



  (def merged-weeks
    (map (fn [w wa] (into w {:2020-adip (:2020 wa)
                             :2020-accum-adip (:2020-accum wa)}))
         weeks
         weeks-adip))

  (def week-dates
    ["March/1/2020"
     "March/8/2020"
     "March/15/2020"
     "March/22/2020"
     "March/29/2020"
     "April/05/2020"
     "April/12/2020"
     "April/19/2020"
     "April/26/2020"
     "May/3/2020"
     "May/10/2020"
     "May/17/2020"
     "May/24/2020"
     "May/31/2020"
     "June/7/2020"
     "June/14/2020"
     "June/21/2020"
     "June/28/2020"
     "July/05/2020"
     "July/12/2020"
     "July/19/2020"
     "July/26/2020"
     "August/2/2020"
     "August/9/2020"
     "August/16/2020"
     "August/23/2020"
     "August/30/2020"
     "September/6/2020"
     "September/13/2020"
     "September/20/2020"
     "September/27/2020"
     "October/4/2020"
     "October/11/2020"
     "October/18/2020"
     "October/25/2020"
     "November/1/2020"
     "November/8/2020"
     "November/15/2020"
     "November/22/2020"
     "November/29/2020"
     "December/6/2020"
     "December/13/2020"
     "December/20/2020"
     "December/27/2020"])

  (def week-dates-adip
    ["January/5/2020"
     "January/12/2020"
     "January/19/2020"
     "January/26/2020"
     "February/2/2020"
     "February/9/2020"
     "February/16/2020"
     "February/23/2020"
     "March/1/2020"
     "March/8/2020"
     "March/15/2020"
     "March/22/2020"
     "March/29/2020"
     "April/05/2020"
     "April/12/2020"
     "April/19/2020"
     "April/26/2020"
     "May/3/2020"
     "May/10/2020"
     "May/17/2020"
     "May/24/2020"
     "May/31/2020"
     "June/7/2020"
     "June/14/2020"
     "June/21/2020"
     "June/28/2020"
     "July/05/2020"
     "July/12/2020"
     "July/19/2020"
     "July/26/2020"
     "August/2/2020"
     "August/9/2020"
     "August/16/2020"
     "August/23/2020"
     "August/30/2020"
     "September/6/2020"
     "September/13/2020"
     "September/20/2020"
     "September/27/2020"
     "October/4/2020"
     "October/11/2020"
     "October/18/2020"
     "October/25/2020"
     "November/1/2020"
     "November/8/2020"
     "November/15/2020"
     "November/22/2020"
     "November/29/2020"
     "December/6/2020"
     "December/13/2020"
     "December/20/2020"
     "December/27/2020"])


  #_(def weeks2019
    (map (fn [date count]
           {:date date :count count :year "2019" :predicted false})
         week-dates-adip
         (filter #(> % 0) (map :2019-accum weeks))))


  #_(def weeks2019
    (map (fn [date count]
           {:date date :count count :year "2019" :predicted false})
         (conj week-dates-adip "December/27/2020")
         (map :2019-accum weeks)))



  (def weeks2020
    (map (fn [date count]
           {:date date :count count :year "2020" :predicted false})
         week-dates
         (filter #(> % 0) (map :2020-accum weeks))))


  (def weeks2020-adip
    (map (fn [date count]
           {:date date :count count :year "2020-adip" :predicted false})
         week-dates-adip
         (filter #(> % 0) (map :2020-accum weeks-adip))))



  (def accumulated-points
    (concat
     inegi2016
     inegi2017
     inegi2018
     inegi2019
     weeks2020
     weeks2020-adip
     #_(map (fn [x] (into x {:predicted false
                           :date (get month-numbers-2019 (:month x))}))
          (filter #(<= (:month %) 1)  months2019))
     (map (fn [x] (into x {:date (get month-numbers-2020 (:month x))}))
          (filter #(< (:month %) 1) months2020))
     #_[{:year "2019", :count 0, :date "Jan/1/2020"
       :predicted false}
      {:year "2019" :date "Dec/31/2020"
       :count (apply + (map :count cy2019c))}]
     [{:year "2020",
       :count 0, :date "Jan/1/2020" :predicted false}]))




  ;;; Chart 1 (time series)
  (oz/view! (viz/chart1-line-plot accumulated-points :date :count :year))


  (defn year-avg
    [numbers]
    (let [numbers' (filter #(not= nil %) numbers)]
      (if (empty? numbers')
        0
        (/ (reduce + numbers') (* 1.0 (count numbers'))))))


  ;;(def weeks weeks-adip)


  (def weekly-chart-data
    (->> weeks
         (map (fn [w]
                (let [avg (year-avg
                           [(:2016 w)
                            (:2017 w)
                            (:2018 w)
                            (:2019 w)
                            #_(if (and (>= (:week w) 10) (<= (:week w) 38))
                              (:2019 w))])]
                  [{:week (:week w) :count (:2016 w) :year "2016"}
                   {:week (:week w) :count (:2017 w) :year "2017"}
                   {:week (:week w) :count (:2018 w) :year "2018"}
                   {:week (:week w) :count (:2019 w) :year "2019"}
                   {:week (:week w) :count (:2020 w) :year "2020"}
                   {:week (:week w) :count (:2020-adip w) :year "2020-adip"}
                   {:week (:week w) :year "Promedio" :count  avg}
                   {:week (:week w) :area (:2020 w) :year "area" :avgy avg}])))
         (apply concat)
         #_(filter #(or (not= (:year %) "2019")
                      (and (>= (:week %) 10) (<= (:week %) 38))))
         (filter #(not (and (or (= (:year %) "2020") (= (:year %) "area"))
                            (>= (:week %) (+ current-week 1)))))
         (filter #(not (and (or (= (:year %) "2020") (= (:year %) "area"))
                            (<= (:week %) 9))))
         (filter #(>= (:week %) 10))
         (filter #(<= (:week %) 52))
         doall))


  (def deaths-week-2020
    (apply + (map :count (filter #(and (<= (:week %) current-week)
                                       (>= (:week %) 14)
                                       (= (:year %) "2020"))
                                 weekly-chart-data))))

  (def deaths-week-avg
    (apply + (map :count (filter #(and (<= (:week %) current-week)
                                       (>= (:week %) 14)
                                       (= (:year %) "Promedio"))
                                 weekly-chart-data))))

  ;; chart 2
  (oz/view! (viz/weekly-line-plot weekly-chart-data :week :count :year))


  (def weekly-titles
    {10 "8 marzo"
     11 "15 marzo"
     12 "22 marzo"
     13 "29 marzo"
     14 "5 abril"
     15 "12 abril"
     16 "19 abril"
     17 "26 abril"
     18 "3 mayo"
     19 "10 mayo"
     20 "17 mayo"
     21 "24 mayo"
     22 "31 mayo"
     23 "7 junio"
     24 "14 junio"
     25 "21 junio"
     26 "28 junio"
     27 "5 julio"
     28 "12 julio"
     29 "19 julio"
     30 "26 julio"
     31 "2 agosto"
     32 "9 agosto"
     33 "16 agosto"
     34 "23 agosto"
     35 "30 agosto"
     36 "6 septiembre"
     37 "13 septiembre"
     38 "20 septiembre"
     39 "27 septiembre"
     40 "4 octubre"
     41 "11 octubre"
     42 "18 octubre"
     43 "25 octubre"
     44 "1 noviembre"
     45 "8 noviembre"
     46 "15 noviembre"
     47 "22 noviembre"
     48 "29 noviembre"
     49 "6 diciembre"
     50 "13 diciembre"
     51 "20 diciembre"
     52 "27 diciembre"})



  (def weekly-dates
    (sorted-map
     "03/29/2020" 13
     "04/05/2020" 14
     "04/12/2020" 15
     "04/19/2020" 16
     "04/26/2020" 17
     "05/03/2020" 18
     "05/10/2020" 19
     "05/17/2020" 20
     "05/24/2020" 21
     "05/31/2020" 22
     "06/07/2020" 23
     "06/14/2020" 24
     "06/21/2020" 25
     "06/28/2020" 26
     "07/05/2020" 27
     "07/12/2020" 28
     "07/19/2020" 29
     "07/26/2020" 30
     "08/02/2020" 31
     "08/09/2020" 32
     "08/16/2020" 33
     "08/23/2020" 34
     "08/30/2020" 35
     "09/06/2020" 36
     "09/13/2020" 37
     "09/20/2020" 38
     "09/27/2020" 39
     "10/04/2020" 40
     "10/11/2020" 41
     "10/18/2020" 42
     "10/25/2020" 43
     "11/01/2020" 44
     "11/08/2020" 45
     "11/15/2020" 46
     "11/22/2020" 47
     "11/29/2020" 48
     "12/06/2020" 49
     "12/13/2020" 50
     "12/20/2020" 51
     "12/27/2020" 52))

  (def cdmx-confirmed-deaths
    (->> (slurp (str
                 "https://raw.githubusercontent.com/"
                 "mariorz/covid19-mx-time-series/master/data/"
                 "full/by_hospital_state/deaths_confirmed_by_death_date_mx.csv"))
         (csv/read-csv)
         (sc/mappify)
         (filter #(= (:Estado %) "Ciudad de México"))
         first))


  (def cdmx-confirmed-weekly-deaths-accum
    (map (comp parse-int #(get cdmx-confirmed-deaths (keyword %)))
         (map mxts-format (keys weekly-dates))))


  (def cdmx-confirmed-weekly-deaths
    (reverse
     (map (fn [[b a]] (- b a))
          (partition 2 1 (reverse cdmx-confirmed-weekly-deaths-accum)))))

  (def weekly-confirmed
    (map (fn [week count]
           {:week week :count count :year "Decesos confirmados"})
         (vals weekly-dates)
         (concat [0] cdmx-confirmed-weekly-deaths)))


  (def weekly-xss
    (map (fn [week2020 weekavg]
           {:week (:week week2020)
            :count (- (:count week2020) (:count weekavg))
            :year "Excedente 2020"})
         (filter #(= (:year %) "2020")
                 (filter #(and (<= (:week %) current-week)
                               (>= (:week %) 10)) weekly-chart-data))
         (filter #(= (:year %) "Promedio")
                 (filter #(and (<= (:week %) current-week)
                               (>= (:week %) 10)) weekly-chart-data))))


  (def weekly-avg
    (filter #(= (:year %) "Promedio")
            (filter #(and (<= (:week %) current-week)
                          (>= (:week %) 10)) weekly-chart-data)))

  (def weekly-xss-pct
    (map (fn [xss base]
           (into xss {:pct (- (/ (:count xss) (:count base)) 0)
                      :week-title
                      (str "Semana "
                           (:week xss)
                           " ("
                           (get weekly-titles (:week xss))
                           ")")}))
         (filter #(>= (:week %) 10) weekly-xss)
         weekly-avg))

  ;; chart 3
    (oz/view!
     (viz/grouped-bar-chart-diff-p weekly-xss-pct :week-title :pct :year))


  ;; net bar chart (unpublished)
    (oz/view!
     (viz/grouped-bar-chart-diff weekly-xss :week :count :year))



  ;; chart 4
    (oz/view! (viz/chart-stacked-cofirmed-xss
               (concat
                (filter #(>= (:week %) 12) weekly-confirmed)
                (filter #(>= (:week %) 12) weekly-xss))
               :week :count :year))


  ;; values for confirmados and sospechosos
  ;; from db published on jan 14
  ;; with fecha_def at or before dec 27
  (def total-items
    (let [confirmed (- 21848 15) ;; 15 confirmed before week 12
          suspects 5632]
      [{:count  (Math/round (- deaths-week-2020 deaths-week-avg))
        :cat "Exceso de Mortalidad"}
       {:count confirmed :cat "Confirmados"}
       {:count (+ confirmed suspects) :cat "Confirmados+Sospechosos"}]))

  ;; Chart 5, total excess mortality
  (oz/view! (viz/chart4-bar-chart total-items :cat :count))


  (def ft-data
    (slurp (str "https://raw.githubusercontent.com/"
                "Financial-Times/coronavirus-excess-mortality-data/master/"
                "data/ft_excess_deaths.csv")))

  (def jhu-data
    (slurp "resources/Covid_19_global_countries.csv"))



  (defn country-deaths
    [country date]
    (let [c (if (= country "UK")
              "United Kingdom" country)]
      (case c
        (->> jhu-data
             (csv/read-csv)
             (sc/mappify)
             (filter #(= (:Country %) c))
             (filter #(= (:date %) date))
             first
             :Deaths
             parse-int))))


  (defn ft-country-data
    [bulk-data country]
    (->> bulk-data
         (csv/read-csv)
         (sc/mappify)
         (filter #(= (:year %) "2020"))

         (filter #(and (= (:country %) country)
                       (= (:region %) country)))
         (sc/cast-with {:week parse-int
                        :excess_deaths_pct edn/read-string
                        :excess_deaths parse-int
                        :expected_deaths parse-int
                        :deaths parse-int})
         (drop-while #(and (< (:excess_deaths_pct %) 10.0)
                           (not (contains? #{"Peru" "Ecuador" "Chile"} (:country %)))))
         (take-while #(or (> (:excess_deaths_pct %) 10.0)
                          (contains? #{"Peru" "Ecuador" "Chile"} (:country %))))

         (map (fn [p] [{:count (:deaths p)
                        :end_date (:date p)
                        :country (:country p)
                        :week (:week p)
                        :series "2020"}
                       {:count (:expected_deaths p)
                        :end_date (:date p)
                        :week (:week p)
                        :country (:country p)
                        :series "expected"}
                       {:area (:deaths p)
                        :avgy (:expected_deaths p)
                        :end_date (:date p)
                        :week (:week p)
                        :country (:country p)
                        :series "area"}]))
         (apply concat)))



  (defn ft-region-data
    [bulk-data region]
    (->> bulk-data
         (csv/read-csv)
         (sc/mappify)
         (filter #(= (:year %) "2020"))
         (filter #(= (:region %) region))
         (sc/cast-with {:week parse-int
                        :excess_deaths parse-int
                        :expected_deaths parse-int
                        :deaths parse-int})
         (map (fn [p] [{:count (:deaths p)
                        :end_date (:date p)
                        :region (:region p)
                        :week (:week p)
                        :series "2020"}
                       {:count (:expected_deaths p)
                        :end_date (:date p)
                        :week (:week p)
                        :region (:region p)
                        :series "expected"}
                       {:area (:deaths p)
                        :avgy (:expected_deaths p)
                        :end_date (:date p)
                        :week (:week p)
                        :region (:region p)
                        :series "area"}]))
         (apply concat)))


  (defn country-pop
    [country]
    (let [c (if (= country "UK")
              "United Kingdom" country)]
      (->> (slurp "resources/WorldPop.csv")
           (csv/read-csv)
           (sc/mappify)
           (filter #(= (:Country %) c))
           first
           :Population
           parse-int)))


  (def cdmx-data
    (->> weeks
         (map (fn [w]
                (let [avg
                      (year-avg [(:2016 w)
                                 (:2017 w)
                                 (:2018 w)
                                 (:2019 w)
                                 #_(if (and (>= (:week w) 10)
                                          (<= (:week w) 38))
                                             (:2019 w))])]
                  [{:week (:week w) :count (:2020 w) :series "2020"
                    :region "CDMX"}
                   {:week (:week w) :series "expected" :count  avg
                    :region "CDMX"}
                   {:week (:week w) :area (:2020 w)  :series "area" :avgy avg
                    :region "CDMX"}])))
         (apply concat)

         (filter #(not (and (or (= (:series %) "2020") (= (:series %) "area"))
                            (>= (:week %) (+ current-week 1)))))
         (filter #(not (and (or (= (:series %) "2020") (= (:series %) "area"))
                            (<= (:week %) 9))))
         (filter #(>= (:week %) 0))
         (filter #(<= (:week %) current-week))
         doall))

  (defn make-week-map
    [region  week end-date count-2020 count-expected]
    [{:count count-2020,
     :end_date end-date,
     :region region,
     :week week,
     :series "2020"}
    {:count count-expected,
     :end_date end-date,
     :week week,
     :region region,
     :series "expected"}
    {:area count-2020,
     :avgy count-expected,
     :end_date end-date,
     :week week,
     :region region,
     :series "area"}])


  (def lima-gov
    (->>
     (slurp "resources/peru.csv")
     (csv/read-csv)
     (filter #(= (first %) "LIMA"))
     (map (fn [r] {:region (first r)
                   :week (edn/read-string (second r))
                   :count-expected (edn/read-string (nth r 2))
                   :count-2020 (edn/read-string (nth r 3))}))
     (drop-while #(> (:week %) 14))
     (take-while #(<= (:week %) 38))
     (map (fn [r] (make-week-map
                   "Lima" (:week r) "na" (:count-2020 r) (:count-expected r))))
     (apply concat)))


  (def lombardia-gov
    (->>
     (slurp "resources/lombardia.csv")
     (csv/read-csv)
     (sc/remove-comments)
     (map (fn [r] {:region "Lombardia region"
                   :week (edn/read-string (first r))
                   :count-expected (edn/read-string (nth r 2))
                   :count-2020 (edn/read-string (nth r 1))}))
     (drop 1)
     (drop-while #(> (:week %) 14))
     (take-while #(<= (:week %) 26))
     (map (fn [r] (make-week-map
                   "Lombardia region" (:week r) "na" (:count-2020 r) (:count-expected r))))
     (apply concat)))


  (def santiago-economist
    (->>
     (slurp (str "https://raw.githubusercontent.com/"
                 "TheEconomist/covid-19-excess-deaths-tracker/master/"
                 "output-data/excess-deaths/chile_excess_deaths.csv"))
     (csv/read-csv)
     (sc/remove-comments)
     (sc/mappify)
     (filter #(= (:region %) "Santiago Metropolitan"))
     (map (fn [r]
            {:region "Metropolitana de Santiago"
             :week (edn/read-string (:week r))
             :count-expected (edn/read-string (:expected_deaths r))
             :count-2020 (edn/read-string (:total_deaths r))}))
     (drop-while #(> (:week %) 18))
     (take-while #(<= (:week %) 38))
     (map (fn [r] (make-week-map
                   "Metropolitana de Santiago" (:week r) "na" (:count-2020 r) (:count-expected r))))
     (apply concat)))




  ;; chart 6
  (oz/view! (viz/multi-weekly-line-plot
             (concat (ft-region-data ft-data "New York City")
                     (ft-region-data ft-data "Guayas")
                     (ft-region-data ft-data "London")
                     (ft-region-data ft-data "Ile-de-France")
                     (ft-region-data ft-data "Metropolitana de Santiago")
                     #_santiago-economist
                     (ft-region-data ft-data "Lima")
                     #_lima-gov
                     (ft-region-data ft-data "Lombardia region")
                     #_lombardia-gov
                     (ft-region-data ft-data "Madrid")
                     cdmx-data)
             :week :count :series))


  (def populations-regions
    {"CDMX" 8918653
     "Guayas" 4327800
     "Lombardia region" 9911665
     "London" 8908081 ;; 2018 wikipedia
     "Madrid" 6587711
     "New York City" 8398748
     "Metropolitana de Santiago" 7112808
     "Lima"   9674755 ;; 2020 wikpedia
     "Ile-de-France" 12174880})

  ;; https://en.wikipedia.org/wiki/List_of_countries_and_dependencies_by_population
  (def populations-nation
    {"Spain" 47329981
     "Italy" 60244639
     "US" 330036464
     "Brazil" 211857447
     "Switzerland" 8619259
     "Portugal" 10295909
     "Austria" 8910696
     "Norway" 5372355
     "South Africa" 59622350
     "Mexico" 127792286
     "Germany" 83166711
     "Chile" 19458310
     "France" 67081000
     "UK" 66796807
     "Belgium" 11528375
     "Ecuador" 17536492
     "Peru" 32824358
     "Sweden" 10348730})


  (def back-to-expected
    {"Spain" true
     "Italy" true
     "US" true
     "Brazil" false
     "Switzerland" true
     "Portugal" true
     "Austria" true
     "Norway" true
     "South Africa" true
     "Mexico" false
     "Germany" true
     "Chile" false
     "France" true
     "UK" true
     "Belgium" true
     "Ecuador" true
     "Peru" false
     "Sweden" true})

  (defn xss-deaths
    [region-data start-week end-week]
    (let [country (:country (first region-data))
          region (:region (first region-data))
          d (if (= start-week -1)
              region-data
              (filter #(and (>= (:week %) start-week)
                            (<= (:week %) end-week))
                      region-data))
          d2020 (apply + (map :count
                              (filter #(= (:series %) "2020") d)))
          dexpected (apply + (map :count
                                  (filter #(= (:series %) "expected") d)))
          net (- d2020 dexpected)
          population (if country
                       (country-pop country)
                       (get populations-regions region))
          xss-pop (* (/ net population) 1000000.0)]
      {:xss-net net
       :back-to-expected (get back-to-expected country)
       :xss-pop xss-pop
       :start-week (if (= start-week -1)
                     (:week (first region-data))
                     start-week)
       :end-week  (if (= start-week -1)
                    (:week (last region-data))
                    end-week)
       :end-date (:end_date (last region-data))
       :country country
       :confirmed-deaths (if country
                           (country-deaths
                            country (:end_date (last region-data))))
       :population population
       :region (:region (first region-data))
       :xss-pct (* 100.0 (- (/ d2020 dexpected) 1))}))


  (def regions
    (->> ft-data
         (csv/read-csv)
         (sc/mappify)
         (filter #(= (:year %) "2020"))
         (map :region)
         distinct
         (filter #(not (contains?
                        #{"Italy" "Spain" "St Petersburg" "US" "UK"
                          "England And Wales" "England" "Brazil" "Peru"
                          "Ecuador" "France" "Netherlands" "Chile"}
                        %)))))


  (def x (map #(xss-deaths (ft-region-data ft-data %) -1 52)
              regions))


  (def cities-xss
    [(xss-deaths cdmx-data 14 current-week)
     (xss-deaths (ft-region-data ft-data "Guayas") 11 41)
     (into
      (xss-deaths (ft-region-data ft-data "Lombardia region") 9 44)
      {:region "Lombardía"})
     (into
      (xss-deaths (ft-region-data ft-data "London") 12 50)
      {:region "Londres"})
     (xss-deaths (ft-region-data ft-data "Madrid") 11 48)
     (into (xss-deaths (ft-region-data ft-data "New York City") 11 48)
           {:region "Nueva York (Ciudad)"})
     (xss-deaths
      (ft-region-data ft-data "Metropolitana de Santiago") 18 50)
     (xss-deaths
      #_lima-gov
      (ft-region-data ft-data "Lima") 14 50)
     (into (xss-deaths (ft-region-data ft-data "Ile-de-France") 12 48)
           {:region "Ile-de-France (región de París)"})])

  (oz/view! (viz/cities-xss-table (reverse (sort-by :xss-pop cities-xss))))

  (def mx20
    (let [;; published by ssa
          xss-net 71315
          ;;; conapo
          population 84495868
          ;; confirmed deaths reported on june 28
          confirmed-deaths 20044]
      {:xss-net xss-net,
       :xss-pop (* (/ xss-net population) 1000000.0),
       :start-week 16,
       :end-week 26,
       :confirmed-deaths confirmed-deaths,
       :country "México (20 estados)",
       :population population,
       :back-to-expected false
       :region nil}))

  (def mxest
    (let [;; covid deaths july 30 46,000
          confirmed-deaths 46000
          ;; covid deaths on june 28
          ;;confirmed-deaths 26648
          ;; 20 state factor * confirmed-deaths
          xss-net (Math/round
                   (* (/ (:xss-net mx20) (:confirmed-deaths mx20))
                      confirmed-deaths 1.0))]
      {:xss-net xss-net,
       :xss-pop (* (/ xss-net
                      (country-pop "Mexico")) 1000000.0),
       :start-week 16,
       :end-week 30,
       :confirmed-deaths confirmed-deaths
       :country "México (Estimación)",
       :population (country-pop "Mexico"),
       :back-to-expected false,
       :region nil}))


  (def brasil
    {:xss-net 62491,
     :xss-pop (* (/ 62491.0
                    (country-pop "Brazil")) 1000000),
     :start-week 12,
     :end-week 23,
     :confirmed-deaths 36455
     :back-to-expected false
     :country "Brasil",
     :population (country-pop "Brazil"),
     :region nil})

  (def us
    {:end-week 26,
     :confirmed-deaths 124416,
     :end-date "2020-05-23",
     :start-week 13,
     :xss-pop (* (/ 182406  (country-pop "US")) 1000000.0)
     :region nil,
     :back-to-expected false,
     :population (country-pop "US"),
     :xss-net 182406,
     :country "Estados Unidos"})


  (def countries-xss
    [mxest
     mx20
     brasil
     us
     (xss-deaths (ft-country-data ft-data "Chile") 19 27)
     (into (xss-deaths (ft-country-data ft-data "Spain") -1 20)
           {:country "España"})
     (into (xss-deaths (ft-country-data ft-data "Switzerland") -1 20)
           {:country "Suiza"})
     (xss-deaths (ft-country-data ft-data "Portugal") -1 20)
     (into (xss-deaths (ft-country-data ft-data "Italy") -1 20)
           {:country "Italia"})
     (into (xss-deaths (ft-country-data ft-data "UK") -1 20)
           {:country "Reino Unido"})
     (into (xss-deaths (ft-country-data ft-data "France") -1 20)
           {:country "Francia"})
     (xss-deaths (ft-country-data ft-data "Peru") 15 27)
     (xss-deaths (ft-country-data ft-data "Ecuador") 12 26)
     (into (xss-deaths (ft-country-data ft-data "Germany") -1 20)
           {:country "Alemania"})
     (into (xss-deaths (ft-country-data ft-data "Belgium") -1 20)
           {:country "Bélgica"})
     (into (xss-deaths (ft-country-data ft-data "Sweden") -1 20)
           {:country "Suecia"})
     ])


  (oz/view!
   (viz/countries-xss-table (reverse (sort-by :xss-net countries-xss))))

  (defn proportion
    [p]
    (/ (:confirmed-deaths p)
       (:xss-net p)))

  (oz/view!
   (viz/covid-xss-table (reverse (sort-by proportion (rest countries-xss)))))



  (defn parse-adip-date
    [s]
    (try
      (f/parse (f/formatter "yyyy-MM-dd") s)
      (catch Exception e (println "error:" s))))


  (require '[clj-time.core :as tm])
  (import '(java.util Calendar))


  (defn week-of-year
    [date]
	  (let [cal (Calendar/getInstance)]
		  (.setTime cal (.toDate date))
		  (.get cal Calendar/WEEK_OF_YEAR)))


  (defn get-year
    [date]
	  (let [cal (Calendar/getInstance)]
		  (.setTime cal (.toDate date))
		  (.get cal Calendar/YEAR)))

  (defn get-month
    [date]
	  (let [cal (Calendar/getInstance)]
		  (.setTime cal (.toDate date))
		  (.get cal Calendar/MONTH)))


  (defn get-day
    [date]
	  (let [cal (Calendar/getInstance)]
		  (.setTime cal (.toDate date))
		  (.get cal Calendar/DAY_OF_MONTH)))


  (defn get-monthday
    [dp]
    (let [date (:fecha_defuncion dp)
          month (+ (get-month date) 1)
          day (get-day date)
          year (get-year date)]
      (str month "/" day "/" year)))

  (def adip-data
    (with-open [in-file (io/reader "resources/data-adip-dic10.csv")]
      (->> (csv/read-csv in-file)
           (sc/remove-comments)
           (sc/mappify)
           (sc/cast-with {:ID parse-int
                          :edad parse-int
                          :fecha_defuncion parse-adip-date})
           (filter #(= (type (:fecha_defuncion %)) org.joda.time.DateTime))
           (map #(into % {:week (week-of-year (:fecha_defuncion %))}))
           (doall))))

  (def adip-data-2020
    (->> adip-data
         (filter #(= (get-year (:fecha_defuncion %)) 2020))))

  (def adip-data-112020
    (->> adip-data
         (filter #(= (get-month (:fecha_defuncion %)) 11))))

  (def adip-data-2020-dec
    (->> adip-data
         (filter #(and (= (get-year (:fecha_defuncion %)) 2020)
                       (= (get-month (:fecha_defuncion %)) 11)))))


  (def adip-data-pre2020-dec
    (->> adip-data
         (filter #(and (not= (get-year (:fecha_defuncion %)) 2020)
                       (= (get-month (:fecha_defuncion %)) 11)
                       (<= (get-day (:fecha_defuncion %)) 10)))))



  (defn adip-week-counts [year]
    (->> adip-data
         (filter #(= (get-year (:fecha_defuncion %)) year))
         (group-by :week)
         (map (fn [[week d]] {:week week :count (count d)}))
         (sort-by :week)))

  (def deaths-cdmx (count adip-data-2020))
  ;; 88,242
  (def deaths-residents
    (count (filter #(= (:estado %) "CIUDAD DE MEXICO") adip-data-2020)))
  ;; 69,428
  (def deaths-non-residents (- deaths-cdmx deaths-residents))
  ;; 18,814


  (defn adip-data-year [year]
    (->> adip-data
         (filter #(= (get-year (:fecha_defuncion %)) year))))

  (defn adip-year-deaths [year]
    (count (adip-data-year year)))

  (defn adip-year-deaths-residents [year]
    (count (filter #(= (:estado %) "CIUDAD DE MEXICO") (adip-data-year year))))


  (defn adip-residency-prop [year]
    (let [total-deaths (adip-year-deaths year)
          residents (adip-year-deaths-residents year)
          non-residents (- total-deaths residents)]
      {:total total-deaths
       :residents residents
       :non-residents non-residents
       :non-resident-prop (* 1.0 (/ non-residents total-deaths))}))

  (def adip-data-2020-weeks
    (->> adip-data
         (filter #(= (get-year (:fecha_defuncion %)) 2020))
         (group-by :week)
         (map (fn [[k v]] (into {:week k :year "2020" :deaths (count v)})))))




  ;;;; adip error
  (def day-grouped (group-by get-monthday adip-data-2020))
  (def day-counts (map (fn [k v] {:date k :count (count v)}) (keys day-grouped) (vals day-grouped)))
  (oz/view! (viz/adip-audit-line-plot day-counts :date :count))
)





(defn -main
  [& args]
  (oz/start-server! 8888))
