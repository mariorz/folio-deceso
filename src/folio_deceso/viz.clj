(ns folio-deceso.viz
  (:require [oz.core :as oz]))


(defn chart1-line-plot
  [data-points x-key y-key z-key]
  {:data {:values data-points}
   :width 600
   :height 300
   :encoding {:x {:field x-key
                  :type "temporal"
                  :axis {:format "%b"
                         :tickBand "extent"
                         :tickCount 7
                         :labelFlush "10"}
                  :title nil}
              :y {:field y-key :type "quantitative"
                  :title nil
                  :axis {}}
              :strokeDash {:field :predicted :type "nominal" :title nil
                           :legend nil}
              :color {:field z-key
                      :type "nominal"
                      :scale {:domain ["2016" "2017" "2018" "2019" "2020"]
                              :range ["lightgray" "lightgray""lightgray" "gray" "crimson"]}
                      :title nil}}
   :mark {:type "line" :point true}
   :config {:axis {:grid true}
            :backgrounr "white"}})



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
                              :range ["lightgray" "lightgray""lightgray" "gray" "crimson"]}}}
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
   :width 800
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
              :text {:field y-key :type "quantitative" :format ","}
              :column {:field z-key :type "ordinal" :spacing 10
                       :label nil
                       :labels nil
                       :title nil}}
   :layer [{:mark {:type  "bar"
                   :color "crimson"}}
           {:mark {:type "text"
                   :fontSize 14
                   :align "center"
                   :baseline "bottom"
                   :dy -23
                   :fontWeight "bold"
                   :color "black"}}]
   :config {:views {:stroke "transparent"}
            :axis {:grid true, :tickBand "extent"
                   :domainWidtn 1}
            :background "white"}})



(defn grouped-bar-chart-diff-p
  [data-points x-key y-key z-key]
  {:data {:values data-points}
   :width 800
   :height 500
   :encoding {:x {:field x-key :type "ordinal"
                  :title nil
                  :axis {:labelFontSize 14}
                  :sort ["Jan/31/2020"
                         "Feb/28/2020"
                         "Mar/31/2020"
                         "Apr/30/2020"
                         "May/31/2020"]}
              :y {:field y-key :type "quantitative"
                  :axis {:tickCount 8
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
                   :dy -23
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
   :layer [{:mark {:type "bar" :color "crimson"}}
           {:mark {:type "text"
                   :fontSize 14
                   :align "center"
                   :baseline "middle"
                   :dx 24
                   :color "black"
                   :fontWeight "bold"}}]
   :config {:axis {:grid true, :tickBand "extent"}
            :background "white"
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
                       :color {:field z-key
                               :type "nominal"
                               :scale {:domain ["Confirmados+Sospechosos" "Exceso de Mortalidad"]
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
  (let [headers ["" "Decesos" "Exceso de mortalidad" "Decesos oficiales por COVID-19" "Decesos oficiales COVID-19 como proporción del exceso de mortalidad (%)"]]
    [:html
     [:head
      [:style (slurp "resources/style.css")]]
     [:body
      [:table {:class "calendar"}
       [:thead
        [:tr
         (map (fn [el] [:th el]) headers)]]
       (map (fn [p] [:tbody
                     [:tr
                      [:td (:place p)]
                      [:td (format "%,d" (:deaths p))]
                      [:td (format "%,d" (:xssdeaths p))]
                      [:td (format "%,d" (:covid-deaths p))]
                      [:td [:vega-lite
                            (single-bar-percent {:place (:place p)
                                                 :proportion
                                                 (int (* 100.0 (/ (:covid-deaths p)
                                                                  (:xssdeaths p))))})]]]])
            places)]]]))
