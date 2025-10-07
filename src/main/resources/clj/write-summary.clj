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

(ns write-summary)

(require '[clojure.string :as str]
         '[clojure.java.io :as io]
         '[clojure.pprint :as pprint])
(import 'org.apache.commons.lang3.StringUtils)
(import 'us.bpsm.edn.Keyword)

(def nl (System/getProperty "line.separator"))


(defn гав
  [old-ctx]
  (let [summary (get old-ctx "summary")
        low-code (get old-ctx "low-code")
        target-path-template (->> low-code
                                  (filter #(= :write-schedule-to-file
                                              (first %)))
                                  (first)
                                  (second))]
    (println "write-summary (start)")
    (println "target-path-template:" target-path-template)
    (println "low-code (start)")
    (println low-code)
    (println "low-code (end)")
    (println summary)
    (println "write-summary (end)")
    old-ctx))
