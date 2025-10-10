;
; This file is part of Governor.
;
; Governor is free software: you can redistribute it and/or modify it under
;  the terms of the GNU General Public License as published by the Free
;  Software Foundation, either version 3 of the License, or (at your option)
;  any later version.
;
; Governor is distributed in the hope that it will be useful, but WITHOUT
;  ANY WARRANTY; without even the implied warranty of
;  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
;  GNU General Public License for more details.
;
; You should have received a copy of the GNU General Public License
; along with Governor. If not, see <https://www.gnu.org/licenses/>.
;

(ns compose-time-slot-data)

(require '[clojure.string :as str]
         '[clojure.java.io :as io]
         '[clojure.pprint :as pprint])

(import 'org.apache.commons.lang3.time.DateUtils)
(import 'java.text.SimpleDateFormat)

(def nl (System/getProperty "line.separator"))
(def sdf (java.text.SimpleDateFormat. "yyyy-MM-dd"))
(def tf (java.text.SimpleDateFormat. "HH:mm"))


(def days-of-week-conv-table
  {:monday "MON",
   :tuesday "TUE",
   :wednesday "WED",
   :thursday "THU",
   :friday "FRI",
   :saturday "SAT",
   :sunday "SUN"})

(declare traverse-days)
(declare compose-time-slots-for-day)
(declare extract-from-through-rules)
(declare extract-after-rules)
(declare extract-duration-rules)

(defn гав
  [ctx]
  (let [low-code (get ctx "low-code")
        first-day (->> low-code
                       (filter #(= :schedule-from (first %)))
                       (first)
                       (second))
        number-of-weeks-to-schedule (->> low-code
                                         (filter #(= :schedule-from (first %)))
                                         (first)
                                         (last)
                                         (#(str/replace % "w" ""))
                                         (Integer/parseInt))
        last-day (.format sdf
                          (DateUtils/addWeeks (.parse sdf first-day)
                                              number-of-weeks-to-schedule))
        from-through-rules (extract-from-through-rules ctx)
        after-rules (extract-after-rules ctx)
        duration-rules (extract-duration-rules ctx)
        ;;        rules-by-day-of-week
        ;;        (merge-with concat from-through-rules after-rules
        ;;        duration-rules)
        ;; rules-by-day-of-week (apply merge from-through-rules)
        rules-by-day-of-week from-through-rules
        days-traversal-result
          (traverse-days
            first-day
            last-day
            #(compose-time-slots-for-day rules-by-day-of-week %1 %2))]
    (println "compose-time-slot-data")
    (println "first-day: " first-day)
    (println "number-of-weeks-to-schedule: " number-of-weeks-to-schedule)
    (println "last-day: " last-day)
    (println "from-through-rules: " from-through-rules)
    (println "rules-by-day-of-week: " rules-by-day-of-week)
    ctx))

(defn traverse-days
  [first-day last-day process-day]
  (let [first-day-date (.parse sdf first-day)
        last-day-date (.parse sdf last-day)]
    (loop [state {:time-slots []}
           current-day first-day-date]
      (when-not (.after current-day last-day-date)
        (let [new-state (process-day state current-day)]
          (recur new-state (DateUtils/addDays current-day 1)))))))

(defn compose-time-slots-for-day
  [rules state current-day]
  (let [current-day-txt (.format sdf current-day)
        day-of-week (.toUpperCase (.format (java.text.SimpleDateFormat. "EEE")
                                           current-day))
        applicable-rules (get rules day-of-week)
        


        ]
    ;; Print the current day from state
    (println "compose-time-slots-for-day: " day-of-week " " current-day-txt)
    (println "applicable-rules (size):" (count applicable-rules))
    (println "applicable-rules:" applicable-rules)
    (println "(first applicable-rules):" (first applicable-rules))
    ;; Return state (potentially modified)
    state))

(declare is-from-through-rule)

(declare transform-from-through-rule)

;; Function for extracting rules like these:
;;
;;    [:on [:monday :wednesday :friday]
;;     :i-can-work-from "06:00"
;;     :through "08:00"]
(defn extract-from-through-rules
  [ctx]
  (let [low-code (get ctx "low-code")
        x (->> low-code
               (filter is-from-through-rule)
               (mapcat transform-from-through-rule)
               (apply merge))]
    (println "x:")
    (pprint/pprint x)
    (->> low-code
         (filter is-from-through-rule)
         (mapcat transform-from-through-rule)
         (apply merge-with concat))))

(defn is-from-through-rule
  [clex]
  (if (= 6 (count clex))
    (let [i0 (first clex)
          i2 (nth clex 2)
          i4 (nth clex 4)]
      (and (= :on i0) (= :i-can-work-from i2) (= :through i4)))
    false ;; (count clex) != 6
  ))

;; Function for extracting rules like these:
;;
;; [:on [:monday :tuesday
;;       :wednesday :thursday :friday]
;;      :i-can-work-for "1h"
;;      :after "17:00"]
(defn extract-after-rules [ctx] {})

;; Function for extracting rules like these:
;;
;; [:on [:saturday :sunday]
;;   :i-can-work-for "5h"]
(defn extract-duration-rules [ctx] {})

(defn transform-from-through-rule
  [rule]
  (let [dict (apply hash-map rule)
        days-of-week (:on dict)
        start-time (->> dict
                        (:i-can-work-from)
                        (.parse tf)
                        (.getTime))
        end-time (->> dict
                      (:through)
                      (.parse tf)
                      (.getTime))
        duration-millis (- end-time start-time)
        duration-hours (/ duration-millis 1000.0 60.0 60.0)]
    (->> days-of-week
         (map #(get days-of-week-conv-table %))
         (map (fn [day]
                {day [{:duration-hours duration-hours, :rule (str rule)}]})))))

