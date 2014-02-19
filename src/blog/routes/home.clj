(ns blog.routes.home
  (:use compojure.core)
  (:require [immutant.jobs :as jobs]
            [immutant.cache :as cache]
            [blog.views.layout :as layout]
            [blog.util :as util]))

(def posts-cache
  (cache/lookup-or-create "posts" :seed {:front-page (util/load-latest-posts 10)}))

(def posts-archive-cache
  (cache/lookup-or-create "posts-archive" :ttl 24, :idle 8, :units :hours))

(jobs/schedule "cache-front-page-posts"
               #(cache/put posts-cache :front-page (util/load-latest-posts 10))
               :every [15 :minutes])


(defn home-page []
  (layout/render
    "home.html" 
    {:posts (:front-page posts-cache)}))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page)))
