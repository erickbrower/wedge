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

(defn get-date [file]
  (let [date-str (-> (.getName file)
                     (strip-extension)
                     (cstr/split #"\-")
                     (first))]
    (->> date-str
         (ctime/parse file-date-formatter)
         (ctime/unparse display-date-formatter))))

(defn get-slug [file]
  (-> (.getName file)
      (strip-extension)))

(def post-files 
  (->> 
    (clojure.java.io/file "resources/public/posts")
    (file-seq)
    (reverse)
    (filter #(.isFile %))))

(defn load-post [file]
  (hash-map :title (get-title file)
            :date (get-date file)
            :slug (get-slug file)
            :content (slurp (.getAbsolutePath file))))

(defn load-post-by-slug [slug]
  (-> slug
      #(= (get-slug %))
      (cset/select post-files)
      (first)
      (load-post)))

(defn load-latest-posts 
  ([] (->> post-files
           (take 5)
           (map #(load-post %))))
  ([top] (->> post-files
           (take top)
           (map #(load-post %)))))
