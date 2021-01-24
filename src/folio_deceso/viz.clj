(ns folio-deceso.viz)


(defn chart1-line-plot
  [data-points x-key y-key z-key]
  {:data {:values data-points}
   :width 900
   :height 400
   :encoding {:x {:field x-key
                  :type "temporal"
                  :scale {:domain ["Jan/01/2020", "Jan/10/2021"]
                          :nice false
                          :clamp true}

                  :axis {:format "%b"
                         :tickBand "extent"
                         :tickCount 14
                         :labelFlush "10"}
                  :title nil}
              :y {:field y-key :type "quantitative"
                  :title nil
                  :scale {:domain [0, 140000]}
                  :axis {}}
              :strokeDash {:field :predicted :type "nominal" :title nil
                           :legend nil}
              :color {:field z-key
                      :type "nominal"
                      :scale {:domain ["2016" "2017" "2018" "2019" "2020" "2020-adip"]
                              :range ["lightgray" "lightgray""lightgray"
                                      "lightgray" "crimson" "green"]}
                      :title nil}}
   :mark {:type "line" :point false}
   :config {:axis {:grid true}
            ;;:backgrounr "white"
            :background "white" #_"#fcf8e8"}})





(defn grouped-bar-chart
  [data-points x-key y-key z-key]
  {:data {:values data-points}
   :width 100
   :height 250
   :encoding {:x {:field x-key :type "ordinal"
                  :sort ["Jan/31/2020"
                         "Feb/28/2020"
                         "Mar/31/2020"
                         "Apr/30/2020"
                         "May/31/2020"]}
              :y {:field y-key :type "quantitative"}
              :column {:field z-key :type "ordinal" :spacing 10}
              :color {:field z-key :type "nominal"
                      :scale {:domain ["2016" "2017" "2018" "2019" "2020"]
                              :range ["lightgray" "lightgray""lightgray"
                                      "gray" "crimson"]}}}
   :mark "bar"
   :config {:views {:stroke "transparent"}
            :axis {:grid true, :tickBand "extent"
                   :domainWidtn 1}
            :background "white"}})


(defn grouped-bar-chart-avg
  [data-points x-key y-key z-key]
  {:data {:values data-points}
   :width 125
   :height 250
   :encoding {:x {:field x-key :type "ordinal"
                  :sort ["Jan/31/2020"
                         "Feb/28/2020"
                         "Mar/31/2020"
                         "Apr/30/2020"
                         "May/31/2020"]}
              :y {:field y-key :type "quantitative"}
              :column {:field z-key :type "ordinal" :spacing 10}
              :color {:field z-key :type "nominal"
                      :title nil
                      :legend nil
                      :scale {:domain ["Promedio 2016-2019" "2020"]
                              :range ["gray" "crimson"]}}}
   :mark "bar"
   :config {:views {:stroke "transparent"}
            :axis {:grid true, :tickBand "extent"
                   :domainWidtn 1}
            :background "white"}})




(defn grouped-bar-chart-diff
  [data-points x-key y-key z-key]
  {:data {:values data-points}
   :width 1400
   :height 500
   :encoding {:x {:field x-key
                  :type "ordinal"
                  :title nil
                  :axis {:labelFontSize 14}
                  :sort ["Jan/31/2020"
                         "Feb/28/2020"
                         "Mar/31/2020"
                         "Apr/30/2020"
                         "May/31/2020"]}
              :y {:field y-key :type "quantitative"
                  :title nil
                  :axis {:tickCount 10
                         :labelFontSize 15}}
              :text {:field y-key :type "quantitative" :format ",.0f"}
              :column {:field z-key :type "ordinal" :spacing 10
                       :label nil
                       :labels nil
                       :title nil}}
   :layer [{:mark {:type  "bar"
                   :color "crimson"}}
           {:mark {:type "text"
                   :fontSize 14
                   :align "center"
                   :baseline "top"
                   :angle 270
                   :dy -5
                   :dx 32
                   :fontWeight "bold"
                   :color "black"}}]
   :config {:views {:stroke "transparent"}
            :axis {:grid true, :tickBand "extent"
                   :domainWidtn 1}
            :background "white"}})



(defn grouped-bar-chart-diff-p
  [data-points x-key y-key z-key]
  {:data {:values data-points}
   :width 1200
   :height 500
   :encoding {:x {:field x-key :type "ordinal"
                  :title nil
                  :axis {:labelFontSize 14
                         :grid false}
                  :sort ["Jan/31/2020"
                         "Feb/28/2020"
                         "Mar/31/2020"
                         "Apr/30/2020"
                         "May/31/2020"]}
              :y {:field y-key :type "quantitative"
                  :axis {:tickCount 8
                         :grid true
                         :format ".0%"
                         :labelFontSize 15}
                  :title ""}
              :text {:field y-key :type "quantitative"
                     :format ".0%"}
              :column {:field z-key :type "ordinal" :spacing 10
                       :label nil
                       :labels nil
                       :legend nil
                       :title nil}}
   :layer [{:mark {:type "bar"
                   :color "crimson"}}
           {:mark {:type "text"
                   :fontSize 12
                   :align "center"
                   :baseline "bottom"
                   :dy -15
                   :color "black"
                   :fontWeight "bold"}}]
   :config {:views {:stroke "transparent"}
            :axis {:grid true, :tickBand "extent"
                   :domainWidtn 1}
            :background "white"}})




(defn chart4-bar-chart
  [data-points x-key y-key]
  {:data {:values data-points}
   :width 900
   :height 300
   :encoding {:text {:field y-key :type "quantitative" :format ","}
              :y {:field x-key :type "nominal" :title nil
                  :axis {:labelFontSize 14}}
              :x {:field y-key :type "quantitative" :title ""
                  :axis {:labelFontSize 15 :tickCount 5}}}
   :layer [{:mark {:type "bar" :color "#ff4d4d"}}
           {:mark {:type "text"
                   :fontSize 14
                   :align "center"
                   :baseline "middle"
                   :dx 24
                   :color "black"
                   :fontWeight "bold"}}]
   :config {:axis {:grid true, :tickBand "extent"}
            :background "white"
            ;;:background "#fcf8e8"
            :color "crimson"}})


(defn chart5-line-plot
  [data-points x-key y-key z-key]
  {:data {:values data-points}
   :width 600
   :height 250
   :layer [
           {:mark {:type "point"
                   :color "crimson"
                   :filled true
                   :shape "circle"
                   :size 60}
            :encoding {:x {:field x-key
                           :type "temporal"
                           :axis {:format " %d/%b "
                                  :tickBand "extent"
                                  :tickCount 7
                                  :labelFlush "10"}
                           :title nil}
                       :y {:field "diff" :type "quantitative"
                           :title nil
                           :axis {}}
                       :strokeDash {:field :predicted :type "nominal" :title nil
                                    :legend nil}}}
           {:mark {:type "line" :point false}
            :encoding {
                       :x {:field x-key
                           :type "temporal"
                           :axis {
                                  :tickBand "extent"
                                  :tickCount 7
                                  :labelFlush "10"}
                           :title nil}
                       :y {:field y-key :type "quantitative"
                           :title nil
                           :axis {}}
                       :color
                       {:field z-key
                        :type "nominal"
                        :scale
                        {:domain
                         ["Confirmados+Sospechosos" "Exceso de Mortalidad"]
                         :range ["gray" "crimson"]}
                        :legend {:orient "top"
                                 :titleFontSize 14
                                 :titleLimit 800
                                 :labelLimit 800
                                 :labelFontSize 14}
                        :title nil}}}
           {:mark {:type "text"
                   :fontSize 12
                   :align "center"
                   :baseline "middle"
                   :color "crimson"
                   :dx 10
                   :dy -10
                   :fontWeight "bold"}
            :encoding {:x {:field x-key
                           :type "temporal"
                           :title nil}
                       :text {:field "diff" :type "quantitative" }
                       :y {:field "diff" :type "quantitative"
                           :title nil
                           :axis {}}
                       :strokeDash {:field :predicted :type "nominal" :title nil
                                    :legend nil}}}]
   :config {:axis {:grid true}
            ;; :background "#fcf8e8"
            :background "white"}})


(defn single-bar-percent
  [d]
  {:data {:values d}
   :width 200
   :height 10
   :config {:axis {:grid true}
            ;; :background "#fcf8e8"
            :background (if (= (:place d) "CDMX") "pink" "white")}
   :layer [{:mark {:type "bar" :height 10 :color "gray"}
            :encoding {:x {:field "proportion"
                           :type "quantitative"
                           :scale {:domain [0, 100]}
                           :axis {:title nil
                                  :tickCount 0}}}}
           {:mark {:type "text" :fontSize 12 :dx 120 :dy 0
                   :fontWeight "normal"}
            :encoding {:text {:field "proportion", :type "quantitative"}
                       :color {:value "#222222"}}}]})


(defn covid-xss-table
  [places]
  (let [headers ["" "Semana inicial" "Semana final" "Exceso de mortalidad"
                 "Decesos oficiales por COVID-19"
                 "Decesos oficiales COVID-19 como proporción del exceso de mortalidad (%)"]]
    [:html
     [:head
      [:style (slurp "resources/style.css")]]
     [:body
      [:table {:class "calendar"}
       [:thead
        [:tr
         (map (fn [el] [:th el]) headers)]]
       (map (fn [p]
              [:tbody
               [:tr
                [:td (:country p)]
                [:td (:start-week p)]
                [:td (:end-week p)]
                [:td (format "%,d" (:xss-net p))]
                [:td (format "%,d" (:confirmed-deaths p))]
                [:td [:vega-lite
                      (single-bar-percent
                       {:place (:country p)
                        :proportion
                        (int (* 100.0 (/ (:confirmed-deaths p)
                                         (:xss-net p))))})]]]])
            places)]]]))



(defn cities-xss-table
  [places]
  (let [headers ["" "Exceso de mortalidad" "Exceso de mortalidad (%)"
                 "Semana inicial" "Semana final"
                 "Población" "Exceso de mortalidad/población millones"]]
    [:html
     [:head
      [:style (slurp "resources/style.css")]]
     [:body
      [:table {:class "cities-xss"}
       [:thead
        [:tr
         (map (fn [el] [:th el]) headers)]]
       (map (fn [p] [:tbody
                     [:tr
                      [:td (:region p)]
                      [:td (format "%,.0f" (float (:xss-net p)))]
                      [:td (format "%,.0f%%" (:xss-pct p))]
                      [:td (:start-week p)]
                      [:td (:end-week p)]
                      [:td (format "%,d" (:population p))]
                      [:td (format "%,.0f" (:xss-pop p))]]])
            places)]]]))



(defn countries-xss-table
  [places]
  (let [headers ["" "Exceso de mortalidad"
                 "Semana inicial" "Semana final" "Población"
                 "Exceso de mortalidad / población millones"
                 "Regreso a niveles esperados"]]
    [:html
     [:head
      [:style (slurp "resources/style.css")]]
     [:body
      [:table {:class "incidence"}
       [:thead
        [:tr
         (map (fn [el] [:th el]) headers)]]
       (map (fn [p] [:tbody
                     [:tr
                      [:td (:country p)]
                      [:td (format "%,.0f" (float (:xss-net p)))]
                      [:td (:start-week p)]
                      [:td (:end-week p)]
                      [:td (format "%,d" (:population p))]
                      [:td (format "%,.0f" (:xss-pop p))]
                      [:td (if (:back-to-expected p)
                             "Sí" "No")]]])
            places)]]]))



(defn weekly-line-plot
  [data-points x-key y-key z-key]
  {:data {:values data-points}
   :width 900
   :height 400
   :layer [ {:mark {:type "line"
                   :point false
                   :interpolate "linear"}
            :encoding {:x {:field x-key
                           :title "Semanas"
                           :type "quantitative"
                           :scale {:domain [10 , 53]
                                   :nice false
                                   ;;:range [10, 52]
                                   :tickMinStep 1}
                           :axis {;;:tickBand "extent"
                                  :grid false
                                  :tickCount 42}}
                       :y {:field y-key
                           :axis {:grid true}
                           :type "quantitative"
                           :scale {:domain [1000,6000]}
                           :title "Decesos semanales"}
                       :color
                       {:field z-key
                        :type "nominal"
                        :scale
                        {:domain
                         [2016, 2017, 2018, 2019, 2020, "Promedio", "area"]
                         :range
                         ["lightgray" "lightgray" "lightgray" "lightgray"
                          "crimson" "gray", "crimson"]}
                        :legend {:orient "right"
                                 :titleFontSize 14
                                 :titleLimit 800
                                 :labelLimit 800
                                 :labelFontSize 14}
                        :title "Año"}}}
           {:mark {:type "line"
                   :point false}
            :encoding {:x {:field x-key
                           :type "quantitative"}
                       :y {:field y-key
                           :type "quantitative"}
                       :color {:field z-key
                               :type "nominal"}}}

           {:mark {:type "area"
                   :point false
                   :color "#222222"
                   :interpolate "linear"}
            :encoding {:opacity {:value 0.25}
                       :x {:field x-key
                           :type "quantitative"}
                       :y {:field :area
                           :axis {:grid true}
                           :type "quantitative"
                           :scale {:domain [1000,4500]}
                           :aggregate "max"}
                       :y2 {:field :avgy
                            :aggregate "min"
                            :type "quantitative"}
                       :color {:field z-key
                               :type "nominal"
                               :scale {:domain ["area"]
                                       :range ["crimson"]}
                               :legend nil
                               :title nil}}}
           {:mark {:type "rule" :color "gray" :size 1
                   :strokeDash [4 4]}
            :encoding {:x {:field "week" :type "quantitative"}}
            :data {:values [{:week 48.14}]}}

           {:mark {:type "rule" :color "crimson" :size 1
                   :strokeDash [4 4]}
            :encoding {:x {:field "week" :type "quantitative"}}
            :data {:values [{:week 50.85}
                            {:week 12.14}]}}
           {:mark {:type "rule" :color "#c65102" :size 1
                   :strokeDash [4 4]}
            :encoding {:x {:field "week" :type "quantitative"}}
            :data {:values [{:week 26.14}]}}
           {:mark {:type "point" :color "black" :size 75
                   :shale "circle" :filled true}
            :encoding {:x {:field "week" :type "quantitative"}
                       :y {:field "val" :type "quantitative"}}
            :data {:values [{:week 50.85 :val 4200}
                            {:week 26.14 :val 2730}
                            {:week 12.15 :val 1430}
                            {:week 48.14 :val 2900}]}}
           {:mark {:type "text" :color "gray" :angle 270
                   :dy -5 :fontSize 12 ;;:fontStyle "regular"
                   :fontWeight 600
                   :align "right"
                   :baseline "bottom"
                   :dx 195}
            :encoding {:x {:field "week" :type "quantitative"}
                       :text {:field "val" :type "nominal"}}
            :data {:values [{:week 50.85 :val "Inicio semáforo rojo"}
                            {:week 26.14 :val "Fin Jornada Nacional de Sana Distancia"}
                            {:week 12.15 :val "Inicio Jornanda Nacional de Sana Distancia"}
                            {:week 48.14 :val "Indicaodres para semáforo rojo"}]}}
           ]
   :config {:axis {}
            :background "white" #_"#fcf8e8"}})


;; red light 1 starts march 23
;; https://www.elfinanciero.com.mx/nacional/jornada-nacional-de-sana-distancia-acaba-el-30-de-mayo-aunque-aun-habra-restricciones-confirma-lopez-gatell
;; red light 1 ends june 29
;; https://adip.cdmx.gob.mx/comunicacion/nota/la-ciudad-de-mexico-pasa-semaforo-naranja
;; red light 2 should have started nov 30
;; red light 2 starts dec 19
;; https://www.animalpolitico.com/2020/12/cdmx-y-edomex-cierran-actividades-no-esenciales-semaforo-rojo/

(defn chart-stacked-cofirmed-xss
  [data-points x-key y-key z-key]
  {:data {:values data-points}
   :width 900
   :height 400
   :layer [{:mark {:type "area"
                   :point false
                   :interpolate "linear"}
            :encoding {:opacity {:value 0.3}
                       :x {:field x-key
                           :title "Semanas"
                           :type "quantitative"
                           :scale {:domain [12, 53]
                                   :nice false
                                   :clamp true}
                           :axis {;;:tickBand "extent"
                                  :grid false
                                  :domain true
                                  :tickCount 15}}
                       :y {:field y-key
                           :axis {:grid true}
                           :sort ["Decesos confirmados" "Excedente 2020"]
                           :type "quantitative"
                           :scale {:domain [0,4500]}
                           :title "Decesos semanales"}
                       :color {:field z-key
                               :type "nominal"
                               :scale {:domain
                                       ["Excedente 2020" "Decesos confirmados"]
                                       :range [ "crimson" "black"]}
                               :legend {:orient "right"
                                        :titleFontSize 14
                                        :titleLimit 800
                                        :labelLimit 800
                                        :labelFontSize 14}
                               :title "Serie"}}}]
   :config {:axis {}
            :background "#fff"
            ;;:background "#fcf8e8"
            }})




(defn multiregion-line-plot
  [data-points x-key y-key z-key]
  {:data {:values data-points}
   :width 200
   :height 100
   :encoding {:x {:field x-key
                  :type "temporal"
                  :axis {:format "%b"
                         :tickBand "extent"
                         :tickCount 12
                         :grid false
                         :labelFlush "10"}
                  :title nil}
              :y {:field y-key :type "quantitative"
                  :title "Fallecimientos"
                  :axis {:tickCount 6
                         :grid true
                         :labelFlush "10"}}
              :strokeDash {:field :predicted :type "nominal" :title nil
                           :legend nil}
              :color {:field z-key
                      :type "nominal"
                      :scale {:domain ["expected" "2020"]
                              :range ["gray" "crimson"]}
                      :title nil}
              :facet {:field "region"
                      :type "nominal"
                      :columns 3
                      :title nil
                      :spacing 20}}
   :mark {:type "line" :point false}
   ;;:resolve {:scale {:y "independent"}}
   :config {:axis {}
            :background "white"}})





(defn multi-weekly-line-plot
  [data-points x-key y-key z-key]
  {:data {:values data-points}
   ;;:repeat {:row [:region]}
   :facet {:field "region"
           :type "nominal"
           :columns 3
           :title nil
           :spacing 20}
   :width 200
   :height 200
   :columns 3
   :spec {:layer [{:mark {:type "line"
                          :point false
                          :interpolate "linear"}
                   :encoding {:x {:field x-key
                                  ;;:title "Semanas"
                                  :type "quantitative"
                                  :scale {;;:domain [10 , 52]
                                          :nice false
                                          ;;:range [10, 52]
                                          :tickMinStep 1}
                                  :axis {;;:tickBand "extent"
                                         :grid true
                                         :tickCount 14}}
                              :y {:field y-key
                                  :axis {:grid true}
                                  :type "quantitative"
                                  ;; :scale {:domain [1000,4500]}
                                  :title "Decesos semanales"}
                              :color {:field z-key
                                      :type "nominal"
                                      :scale {:domain
                                              ["expected", "2020"]
                                              :range
                                              ["gray", "crimson" "crimson"]}
                                      :legend {:orient "right"
                                               :titleFontSize 14
                                               :titleLimit 800
                                               :labelLimit 800
                                               :labelFontSize 14}
                                      :title "Año"}}}
                  {:mark {:type "line"
                          :point false}
                   :encoding {:x {:field x-key
                                  :type "quantitative"}
                              :y {:field y-key
                                  :type "quantitative"}
                              :color {:field z-key
                                      :type "nominal"}}}

                  {:mark {:type "area"
                          :point false
                          :color "#222222"
                          :interpolate "linear"}
                   :encoding {:opacity {:value 0.25}
                              :x {:field x-key
                                  :type "quantitative"}
                              :y {:field :area
                                  :axis {:grid true}
                                  :type "quantitative"
                                  ;;:scale {:domain [000,4500]}
                                  :aggregate "max"}
                              :y2 {:field :avgy
                                   :aggregate "min"
                                   :type "quantitative"}
                              :color {:field z-key
                                      :type "nominal"
                                      :scale {:domain ["area"]
                                              :range ["crimson"]}
                                      :legend nil
                                      :title nil}}}]}
   :config {:axis {}
            :background "#fff"}})





(defn adip-audit-line-plot
  [data-points x-key y-key]
  {:data {:values data-points}
   :width 600
   :height 300
   :encoding {:x {:field x-key
                  :type "temporal"
                  :axis {:format "%b"
                         :tickBand "extent"
                         :tickCount 12
                         :grid false
                         :labelFlush "10"}
                  :title "Fecha de Defunción"}
              :y {:field y-key :type "quantitative"
                  :title "Fallecimientos"
                  :axis {:tickCount 6
                         :grid true
                         :labelFlush "10"}}
              :strokeDash {:field :predicted :type "nominal" :title nil
                           :legend nil}
             }
   :mark {:type "line" :point false}
   ;;:resolve {:scale {:y "independent"}}
   :config {:axis {}
            :background "white"}})
