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
(import 'java.io.File)

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
  (loop [state {:counter 0, :mode :root-file-not-read, :diagrams-to-process []}]
    (if (continue-loop? state) (recur (process-iteration state ctx)) state)))

(defn continue-loop?
  [state]
  (let [root-file-found (not (:root-file-not-found? state))
        are-there-diagrams-to-process? (-> state
                                           (:diagrams-to-process)
                                           (seq)
                                           (nil?)
                                           (not))]
    (and (< (:counter state) 3)
         (or are-there-diagrams-to-process? root-file-found))))

(declare process-root-diagram-if-possible)

(declare process-diagram)
(defn process-iteration
  [state ctx]
  (let [mode (:mode state)
        are-there-diagrams-to-process? (-> state
                                           (:diagrams-to-process)
                                           (seq)
                                           (nil?)
                                           (not))
        next-diagram-to-process (-> state
                                    (:diagrams-to-process)
                                    (first))]
    (println "process-iteration")
    (println "state: " state)
    (println "diagrams: "
             (-> state
                 (:diagrams-to-process)))
    (println "are-there-diagrams-to-process?: " are-there-diagrams-to-process?)
    (println "foo")
    (cond (= mode :root-file-not-read) (process-root-diagram-if-possible state
                                                                         ctx)
          are-there-diagrams-to-process?
            (process-diagram next-diagram-to-process state ctx)
          :else (update state :counter inc))))


(defn process-root-diagram-if-possible
  [state ctx]
  (let [low-code (get ctx "low-code")
        root-file-expr (->> low-code
                            (filter #(= :control-state-root-file-is-located-in
                                        (first %)))
                            (first))
        base-dir (get ctx "baseDir")
        root-file-name (if (not (nil? root-file-expr))
                         (-> root-file-expr
                             (second)
                             (str/replace "@{basedir}" base-dir))
                         nil)
        root-file-readable
          (if (not (nil? root-file-name))
            (let [file (new java.io.File root-file-name)]
              (and (.exists file) (.isFile file) (.canRead file)))
            false)]
    (println "process-root-diagram")
    (println "root-file-expr: " root-file-expr)
    (println "root-file-name: " root-file-name)
    (println "root-file-readable: " root-file-readable)
    (if root-file-readable
      ;; Below we add the diagram file to the list of files to process if
      ;; the diagram can be read
      (-> state
          (update :counter inc)
          (update :mode :root-file-read)
          (update :diagrams-to-process conj root-file-name))
      ;; Below we return an error if the diagram file cannot be read
      (-> state
          (update :counter inc)
          (assoc :root-file-not-found? true)))))

(declare extract-subprocess-files)

(defn process-diagram
  [diagram-file-name state ctx]
  (let [diagram-txt (with-open [rdr (clojure.java.io/reader diagram-file-name)]
                      (doall (line-seq rdr)))
        ;; TODO: Extract subprocesses
        sub-processes (extract-subprocess-files diagram-file-name diagram-txt)
        ;; TODO: Extract activities waiting for scheduling
        relevant-activities nil]
    (println "process-diagram (start)")
    (println "diagram-txt: " diagram-txt)
    (println "diagram-file-name: " diagram-file-name)
    (println "process-diagram (end)")
    ;; TODO: Add subprocesses to the state
    ;; TODO: Remove diagram-file-name from the list of files to process
    ;; TODO: Add relevant-activities to the list of activities to schedule
    (update state :diagrams-to-process #(remove #{diagram-file-name} %))))

(declare graphviz-element-id)

(defn extract-subprocess-files
  [parent-diagram-file-name diagram-lines]
  (let [
        prefix (str/replace 
parent-diagram-file-name #"\.dot$" "")

        sub-process-lines (->> diagram-lines
                               (filter #(str/includes? % "⬤"))
                               (filter #(str/includes? % "shape=box"))
                               (filter #(str/includes? % "style=rounded"))
                               (filter #(str/includes? % "penwidth=5"))
                               (map graphviz-element-id)
                               (map #(str prefix "." % ".dot"))


                               )
        sub-process-ids nil]
    (println "extract-subprocess-files (start)")
    (println "x:" sub-process-lines)
    (println "extract-subprocess-files (end)")))

(defn graphviz-element-id
  [graphviz-line]
  (-> graphviz-line
      (clojure.string/split #"\[")
      first
      (clojure.string/replace #"[\s\"]" "")))
