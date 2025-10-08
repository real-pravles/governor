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
  (let [
        activities-to-schedule (-> old-ctx
                                   (run-loop)
                                   (:activities))
        ]
    (println "det-act-to-schedule called")
    (.put old-ctx "activities" activities-to-schedule)
    old-ctx))

(defn run-loop
  [ctx]
  (loop [state {
                :mode :root-file-not-read,
                :diagrams-to-process [],
                :activities []}]
    (if (continue-loop? state) (recur (process-iteration state ctx)) state)))

(defn continue-loop?
  [state]
  (let [root-file-found (not (:root-file-not-found? state))
        are-there-diagrams-to-process? (-> state
                                           (:diagrams-to-process)
                                           (seq)
                                           (nil?)
                                           (not))]
(or are-there-diagrams-to-process? root-file-found)

))

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
          :else state)))


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
          (update :mode :root-file-read)
          (update :diagrams-to-process conj root-file-name))
      ;; Below we return an error if the diagram file cannot be read
      (-> state
          (assoc :root-file-not-found? true)))))

(declare extract-subprocess-files)
(declare extract-activities)

(defn process-diagram
  [diagram-file-name state ctx]
  (let [diagram-lines (with-open [rdr (clojure.java.io/reader
                                        diagram-file-name)]
                        (doall (line-seq rdr)))
        sub-process-files (extract-subprocess-files diagram-file-name
                                                    diagram-lines)
        ;; TODO: Extract activities waiting for scheduling
        relevant-activities (extract-activities diagram-file-name
                                                diagram-lines)]
    (println "process-diagram (start)")
    (println "diagram-txt: " diagram-lines)
    (println "diagram-file-name: " diagram-file-name)
    (println "process-diagram (end)")
    (-> state
        (update :diagrams-to-process #(remove #{diagram-file-name} %))
        (update :diagrams-to-process #(into % sub-process-files))
        (update :activities #(into % relevant-activities)))))

(declare graphviz-element-id)

(defn extract-subprocess-files
  [parent-diagram-file-name diagram-lines]
  (let [prefix (str/replace parent-diagram-file-name #"\.dot$" "")]
    (->> diagram-lines
         (filter #(str/includes? % "⬤"))
         (filter #(str/includes? % "shape=box"))
         (filter #(str/includes? % "style=rounded"))
         (filter #(str/includes? % "penwidth=5"))
         (map graphviz-element-id)
         (map #(str prefix "." % ".dot")))))

(defn graphviz-element-id
  [graphviz-line]
  (-> graphviz-line
      (clojure.string/split #"\[")
      first
      (clojure.string/replace #"[\s\"]" "")))

(defn extract-activities
  [parent-diagram-file-name diagram-lines]
  (let [process-id (-> parent-diagram-file-name 
                       (clojure.string/split #"\.")
                       butlast
                       last)]
    (->> diagram-lines
         (filter #(str/includes? % "⬤"))
         (filter #(str/includes? % "▢"))
         (filter #(str/includes? % "shape=box"))
         (filter #(str/includes? % "style=rounded"))
         (map graphviz-element-id)
         (map (fn [activity] {:process process-id, :activity activity})))


    ))
