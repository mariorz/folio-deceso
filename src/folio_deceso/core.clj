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

  (def cy2020c (edn/read-string (slurp "resources/cy2020.edn")))
  (def cy2019c (edn/read-string (slurp "resources/cy2019.edn")))
  (def cy2018c (edn/read-string (slurp "resources/cy2018.edn")))
  (def cy2017c (edn/read-string (slurp "resources/cy2017.edn")))
  (def cy2016c (edn/read-string (slurp "resources/cy2016.edn"))))

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
  ;; for 2020 on all the series.
  ;; this is so that we can use a vega-lite temporal axis on
  ;; Chart 1, with the different series for each year aligned.

  (def month-numbers-2019
    {1 "Jan/31/2020" 2 "Feb/28/2020" 3 "Mar/31/2020" 4 "Apr/30/2020"
     5 "May/31/2020" 6 "June/30/2020" 7 "July/31/2020" 8 "Aug/31/2020"
     9 "Sep/30/2020" 10 "Oct/31/2020" 11 "Nov/30/2020" 12 "Dec/31/2020"})


  (def month-numbers-2020
    {1 "Jan/31/2020" 2 "Feb/28/2020" 3 "Mar/31/2020" 4 "Apr/30/2020"
     4.5 "May/20/2020" 5 "May/31/2020" 6 "June/30/2020"})


  (defn homogenize-year
    [s]
    (f/unparse (f/formatter "MM/dd/2020") (f/parse (f/formatter "dd/MM/yyyy") s)))

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
     (drop-last 1 (filter #(and (= (:year %) "2016") (= (:last-doy %) 1)) inegi-days))
     [(last (drop-last 1 (filter #(= (:year %) "2016") inegi-days)))]))

  (def inegi2017
    (filter #(and (= (:year %) "2017") (= (:last-doy %) 1)) inegi-days))

  (def inegi2018
    (concat
     (drop-last 1 (filter #(and (= (:year %) "2018") (= (:last-doy %) 1)) inegi-days))
     [(last (filter #(= (:year %) "2018") inegi-days))]))

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
     "June/7/2020"])


  (def weeks2019
    (map (fn [date count]
           {:date date :count count :year "2019" :predicted false})
         week-dates
         (filter #(> % 0) (map :2019-accum weeks))))


  (def weeks2020
    (map (fn [date count]
           {:date date :count count :year "2020" :predicted false})
         week-dates
         (filter #(> % 0) (map :2020-accum weeks))))



  (def accumulated-points
    (concat
     inegi2016
     inegi2017
     inegi2018
     weeks2019
     weeks2020
     (map (fn [x] (into x {:predicted false
                            :date (get month-numbers-2019 (:month x))}))
          (filter #(<= (:month %) 1)  months2019))
     (map (fn [x] (into x {:date (get month-numbers-2020 (:month x))}))
          (filter #(< (:month %) 1) months2020))
     [{:year "2019", :count 0, :date "Jan/1/2020"
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


  (def weekly-chart-data
    (->> weeks
     (map (fn [w] (let [avg (year-avg [(:2016 w)
                                       (:2017 w)
                                       (:2018 w)
                                       (if (and (>= (:week w) 10) (<= (:week w) 23))
                                         (:2019 w))])]
                    [{:week (:week w) :count (:2016 w) :year "2016"}
                     {:week (:week w) :count (:2017 w) :year "2017"}
                     {:week (:week w) :count (:2018 w) :year "2018"}
                     {:week (:week w) :count (:2019 w) :year "2019"}
                     {:week (:week w) :count (:2020 w) :year "2020"}
                     {:week (:week w) :year "Promedio" :count  avg}
                     {:week (:week w) :area (:2020 w)  :year "area" :avgy avg}])))
     (apply concat)
     (filter #(or (not= (:year %) "2019")
                  (and (>= (:week %) 10) (<= (:week %) 23))))
     (filter #(not (and (or (= (:year %) "2020") (= (:year %) "area"))
                        (>= (:week %) 24))))
     (filter #(not (and (or (= (:year %) "2020") (= (:year %) "area"))
                        (<= (:week %) 9))))
     (filter #(>= (:week %) 10))
     (filter #(<= (:week %) 52))
     doall))


  (def deaths-week-2020
    (apply + (map :count (filter #(and (<= (:week %) 23)
                                       (>= (:week %) 14)
                                       (= (:year %) "2020"))
                                 weekly-chart-data))))
  (def deaths-week-avg
    (apply + (map :count (filter #(and (<= (:week %) 23)
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
     24 "14 junio"})



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
     "06/07/2020" 23))

  (def cdmx-confirmed-deaths
    (->> (slurp "https://raw.githubusercontent.com/mariorz/covid19-mx-time-series/master/data/full/by_hospital_state/deaths_confirmed_by_death_date_mx.csv")
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
                 (filter #(and (<= (:week %) 23)
                               (>= (:week %) 10)) weekly-chart-data))
         (filter #(= (:year %) "Promedio")
                 (filter #(and (<= (:week %) 23)
                               (>= (:week %) 10)) weekly-chart-data))))


  (def weekly-avg
    (filter #(= (:year %) "Promedio")
            (filter #(and (<= (:week %) 23)
                          (>= (:week %) 10)) weekly-chart-data)))

  (def weekly-xss-pct
    (map (fn [xss base]
           (into xss {:pct (- (/ (:count xss) (:count base)) 0)
                      :week-title (str "Semana " (:week xss) " (" (get weekly-titles (:week xss)) ")")}))
         (filter #(>= (:week %) 10) weekly-xss)
         weekly-avg))

  ;; chart 3
  (oz/view! (viz/grouped-bar-chart-diff-p weekly-xss-pct :week-title :pct :year))


  ;; net bar chart (unpublished)
  (oz/view! (viz/grouped-bar-chart-diff weekly-xss :week :count :year))



  ;; chart 4
  (oz/view! (viz/chart-stacked-cofirmed-xss (concat
                                             (filter #(>= (:week %) 12) weekly-confirmed)
                                             (filter #(>= (:week %) 12) weekly-xss))
                                            :week :count :year))


  ;; values for confirmados and sospechosos
  ;; from db published at june 12
  ;; with fecha_def at or before june 7
  (def total-items
    (let [june7-confirmed (- 4189 15)
          june7-suspects 471]
      [{:count (- deaths-week-2020 deaths-week-avg)
        :cat "Exceso de Mortalidad"}
       {:count june7-confirmed :cat "Confirmados"}
       {:count (+ june7-confirmed june7-suspects) :cat "Confirmados+Sospechosos"}]))

  ;; Chart 5, total excess mortality
  (oz/view! (viz/chart4-bar-chart total-items :cat :count)))




(defn -main
  [& args]
  (oz/start-server! 8888))
