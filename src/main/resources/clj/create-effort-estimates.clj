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

(ns create-effort-estimates)

(require '[clojure.string :as str]
         '[clojure.java.io :as io]
         '[clojure.pprint :as pprint])
(import 'org.apache.commons.lang3.StringUtils)
(import 'us.bpsm.edn.Keyword)

(def nl (System/getProperty "line.separator"))

(declare extract-assumed-efforts)
(declare extract-min-session-hours)

(defn гав
  [old-ctx]
  (let [
        activities (get old-ctx "activities")
        assumed-efforts (extract-assumed-efforts old-ctx)
        assumed-min-session-hours (extract-min-session-hours old-ctx)
        ]
    (println "create-effort-estimates")
    (println "activities: " activities)
    old-ctx))

(defn extract-assumed-efforts
  [ctx]
  (let []
    (println "extract-assumed-efforts")
    (println "ctx: " ctx)
    nil
    )
  )

(defn extract-min-session-hours
  [ctx]
  nil)
