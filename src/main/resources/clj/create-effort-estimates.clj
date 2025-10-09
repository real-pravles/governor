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
(declare extract-priorities)

(defn гав
  [old-ctx]
  (let [activities (get old-ctx "activities")
        assumed-efforts (extract-assumed-efforts old-ctx)
        assumed-min-session-hours (extract-min-session-hours old-ctx)
        priorities (extract-priorities old-ctx)
        effort-estimates (->> activities
                              (map (fn [a]
                                     (let []
(Task. "first_naked_post" "p" 5 12.0 0.25)

                                       )))

                              )
        ]
    (println "create-effort-estimates")
    (println "assumed-efforts: " assumed-efforts)
    (println "assumed-min-session-hours: " assumed-min-session-hours)
    (println "priorities: " priorities)
    (println "activities: " activities)
    (println "effort-estimates: " effort-estimates)
    old-ctx))

(defn parse-effort
  [v]
  (let [last-idx (dec (count v))
        last-val (get v last-idx)]
    (if (and (string? last-val) (re-matches #"\d+[hm]" last-val))
      (let [num-str (subs last-val 0 (dec (count last-val)))
            num (Double/parseDouble num-str)
            unit (last last-val)
            hours (if (= unit \m) (/ num 60.0) num)]
        (assoc v last-idx hours)))))

(defn extract-assumed-efforts
  [ctx]
  (let [low-code (get ctx "low-code")]
    (->> low-code
         (filter (fn [t]
                   (and (= :in-process (first t))
                        (= :assume-activity-requires-effort-of (get t 2)))))
         (map parse-effort)
         (into {} (map (fn [[_ name _ effort]] [name effort]))))))

(defn extract-min-session-hours
  [ctx]
  (let [low-code (get ctx "low-code")]
    (->> low-code
         (filter (fn [t]
                   (and (= :in-process (first t))
                        (= :assume-min-session-duration-of (get t 2)))))
         (map parse-effort)
         (into {} (map (fn [[_ name _ effort]] [name effort]))))))

(defn extract-priorities
  [ctx]
  (let [low-code (get ctx "low-code")]
    (->> low-code
         (filter (fn [t]
                   (and (= :process (first t))
                        (= :has-priority (get t 2)))))
         (into {} (map (fn [[_ name _ effort]] [name effort]))))))
