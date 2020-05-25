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
              :column {:field z-key :type "ordinal" :spacing 10
                       :label nil
                       :labels nil
                       :title nil}
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
                  :axis {:tickCount 5
                         :labelFontSize 15}
                  :title ""}
              :column {:field z-key :type "ordinal" :spacing 10
                       :label nil
                       :labels nil
                       :legend nil
                       :title nil}
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




(defn chart4-bar-chart
  [data-points x-key y-key]
  {:data {:values data-points}
   :width 900
   :height 300
   :encoding {:y {:field x-key :type "nominal" :title nil
                  :axis {:labelFontSize 14}}
              :x {:field y-key :type "quantitative" :title ""
                  :axis {:labelFontSize 15}}}
   
   :mark {:type "bar" :color "crimson"}
   :config {:axis {:grid true, :tickBand "extent"}
            ;;:background "#fcf8e8"
            :background "white"
            :color "crimson"}})
