(ns blog.util
  (:require [noir.io :as io]
            [clojure.string :as cstr]
            [clj-time.format :as ctime]
            [clojure.set :as cset]
            [markdown.core :as md]))

(defn md->html
  "reads a markdown file from public/md and returns an HTML string"
  [filename]
  (->>
    (io/slurp-resource filename)
    (md/md-to-html-string)))

(def file-date-formatter 
  (ctime/formatter "yyyyMMddHHmm"))

(def display-date-formatter
  (ctime/formatter "MMM d, yyyy HH:mm"))

(defn strip-extension [thing] 
  (cstr/replace thing #"\.[0-9a-z]+$" ""))

(defn get-title [file]
  (cstr/join " "
    (-> (.getName file)
        (strip-extension)
        (cstr/split #"\-")
        (subvec 1))))

(defn get-raw-date [file]
  (-> (.getName file)
      (strip-extension)
      (cstr/split #"\-")
      (first)))

(defn get-display-date [file]
  (->> (get-raw-date file)
       (ctime/parse file-date-formatter)
       (ctime/unparse display-date-formatter)))

(defn get-slug [file]
  (-> (.getName file)
      (strip-extension)))

(defn load-post [file]
  (hash-map :title (get-title file)
            :date (get-raw-date file)
            :slug (get-slug file)
            :content (->> file
                          (.getAbsolutePath)
                          (slurp)
                          (md/md-to-html-string))))

(def post-files 
  "Sorted by date, most recent first"
  (->> 
    (clojure.java.io/file "resources/public/posts")
    (file-seq)
    (reverse)
    (filter #(.isFile %))))

(defn load-post-by-slug [slug]
  (-> slug
      #(= (get-slug %))
      (cset/select post-files)
      (first)
      (load-post)))

(defn load-latest-posts 
  ([] (load-latest-posts 5))
  ([top] (->> post-files
           (take top)
           (map #(load-post %)))))
