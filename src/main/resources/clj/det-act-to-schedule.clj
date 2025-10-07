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

(ns det-act-to-schedule)

(require '[clojure.string :as str]
         '[clojure.java.io :as io]
         '[clojure.pprint :as pprint])
(import 'org.apache.commons.lang3.StringUtils)
(import 'us.bpsm.edn.Keyword)

(def nl (System/getProperty "line.separator"))

(declare process-iteration)
(declare continue-loop?)
(declare run-loop)

(defn гав
  [old-ctx]
  (let [activities-to-schedule (run-loop old-ctx)]
    (println "det-act-to-schedule called")
    old-ctx))

(defn run-loop
  [ctx]
  (loop [state {:counter 0, :mode :root-file-not-read}]
    (if (continue-loop? state) (recur (process-iteration state ctx)) state)))

(defn continue-loop? [state] (< (:counter state) 3))

(declare process-root-diagram)

(defn process-iteration
  [state ctx]
  (let [mode (:mode state)]
    (cond
      (= mode :root-file-not-read) (process-root-diagram state ctx))
    (println "foo")
    (update state :counter inc)))

(defn process-root-diagram
  [state ctx]
  (println "process-root-diagram"))
