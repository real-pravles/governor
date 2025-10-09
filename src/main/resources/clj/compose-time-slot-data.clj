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
        last-day (DateUtils/addWeeks 
  (.parse (java.text.SimpleDateFormat. "yyyy-MM-dd") first-day)
  number-of-weeks-to-schedule)


        ]
    (println "compose-time-slot-data")
    (println "first-day: " first-day)
    (println "number-of-weeks-to-schedule: " number-of-weeks-to-schedule)
    (println "last-day: " last-day)
    ctx))
