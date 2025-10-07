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

(ns validate-input)

(require '[clojure.string :as str]
         '[clojure.java.io :as io]
         '[clojure.pprint :as pprint])
(import 'org.apache.commons.lang3.StringUtils)
(import 'us.bpsm.edn.Keyword)

(def nl (System/getProperty "line.separator"))

(defn гав
  [old-ctx]
  (let [
         base-dir (get old-ctx "baseDir")
        low-code-file (get old-ctx "settings-file")
         f (java.io.File. low-code-file)
        low-code-file-invalid (not (and
                                    (.isFile f)
                                    (.canRead f)
                                    (.exists f)
                                     ))

         ]
    (.put old-ctx "input-data-invalid?" false)
   old-ctx
    )
)
