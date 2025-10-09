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
        rules-by-day-of-week
          (merge-with concat from-through-rules after-rules duration-rules)
        days-traversal-result
          (traverse-days first-day last-day compose-time-slots-for-day)]
    (println "compose-time-slot-data")
    (println "first-day: " first-day)
    (println "number-of-weeks-to-schedule: " number-of-weeks-to-schedule)
    (println "last-day: " last-day)
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
  [state current-day]
  (let [current-day-txt (.format sdf current-day)
        day-of-week (.toUpperCase (.format (java.text.SimpleDateFormat. "EEE")
                                           current-day))]
    ;; Print the current day from state
    (println "compose-time-slots-for-day: " day-of-week " " current-day-txt)
    ;; Return state (potentially modified)
    state))
