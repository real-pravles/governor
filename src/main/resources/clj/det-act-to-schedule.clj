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

(defn continue-loop?
  [state]
  (let [root-file-found (not (:root-file-not-found? state))]
    (and (< (:counter state) 3) root-file-found)))

(declare process-root-diagram-if-possible)

(defn process-iteration
  [state ctx]
  (let [mode (:mode state)]
    (cond (= mode :root-file-not-read) (process-root-diagram-if-possible state
                                                                         ctx))
    (println "foo")
    (update state :counter inc)))

(defn process-root-diagram-if-possible
  [state ctx]
  (let [low-code (get ctx "low-code")
        root-file-expr (->> low-code
                            (filter #(= :write-schedule-to-file (first %)))
                            (first))
        base-dir (get old-ctx "baseDir")
        root-file-name (if (not (nil? root-file-expr))
                         (-> root-file-expr
                             (second)
                             (str/replace "@{basedir}" base-dir))
                         nil)
        root-file-readable (if (not (nil? root-file-name))
                             (let [file (new java.io.File)]
                               (and (.exists file) (.isFile file) (.canRead)))
                             (false))]
    ;; TODO: Implement the following logic
    ;; TODO: If no root diagram found, add error to state
    ;; TODO: Modify continue-loop so that it stops, if no root diagram was
    ;; found
    ;;    (if (nil? root-file-expr))
    (if root-file-readable
      state
      (-> state
          (update :counter inc)
          (assoc :root-file-not-found? true)))
    (println "process-root-diagram")
    (println "root-file-expr: " root-file-expr)))
