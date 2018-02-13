
(ns twilio-sms-reminders.core
  (:require [compojure.core :refer :all]
            [org.httpkit.server :refer [run-server]]
            [clojure.pprint :refer [pprint]]
            [org.httpkit.client :as client]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.params :refer :all]
            [clj-time.format :as f]
            [clj-time.core :as t]
            [twijlio.config :as tw-config]
            [twijlio :as tw]
            [clojure.string :as string]
            [ring.util.codec :as codec]
            
))

;;(def ifttt-dropbox-url (System/getenv "IFTTT_DROPBOX_URL"))
(def my-number (System/getenv "MY_NUMBER"))
(def twilio-number (System/getenv "TWILIO_NUMBER"))
(def temporize-url (System/getenv "TEMPORIZE_URL"))
;;(tw-config/set-account-auth! {:account-sid (System/getenv "TWILIO_ACCOUNT_SID") :auth-token (System/getenv "TWILIO_AUTH_TOKEN")})


(defn return-twilio-message [message]
  {:status 200
   :headers { "Content-Type" "application/xml" }
   :body (str "<?xml version=\"1.0\" encoding=\"UTF-8\" ?> <Response> <Message>" message "</Message> </Response>")})

(def return-unauthorized { :status 401 })

;; (defn send-to-ifttt [content]
;;   (client/post ifttt-dropbox-url
;;                { :form-params { :value1 content } }
;;                (fn [{:keys [status error body headers]}]
;;                 (println status)
;;                  (println error)
;;                  (println body)
;;                  (println headers))))

;;(use 'prettytime-nlp)

(defn to-iso1608 [date]
  (f/unparse (f/formatters :basic-date-time-no-ms) date))



;;(require '[clj-time.local :as l])

;;(to-iso1608 *1)

;;(to-iso1608 l/local-now)

;;(f/parse custom-formatter "10:57AM 2-12-18")

;;(f/unparse (f/formatters :basic-ordinal-date-time-no-mis) t/time-now)

;;(l/format-local-time (l/local-now) :basic-ordinal-date-time-no-ms)
;;(println *1)

;;(f/show-formatters)

;;(parse-message  "Dinner With Andre ; 10:58AM 2-12-18")
;; (schedule-notification *1)

(def callback-url  (codec/url-encode  "https://nstares-org-sms.herokuapp.com/notify"))

(defn schedule-notification
  "Schedules a notification with Temporize, using whatever data should be associated with the event
  define in here a callback url and a notificaion time (perhaps as different from the event time)
    Takes a map like {:event-title :event-time :notification-time}"
  [notification]

  (let [notification-date (to-iso1608 (:notification-date notification))
        post-event-url ("https://api.temporize.net/v1/events/" notification-date "/" callback-url)
        options {:basic-auth [(System/getenv "TEMPORIZE_USER") (System/getenv "TEMPORIZE_PASS")]              
                 :form-params notification
                 :as :text}
        {:keys [status headers body error] :as resp} @(client/post post-event-url options)]
      
      (if error    
        (println "Failed, exception: " error)
        (do
          (println "HTTP GET success: " status)
          (println "HEADERS: " headers)
          (println "BODY: " body)))))




 ;; (let [{:keys [status headers body error] :as resp} @(client/post "https://api.temporize.net/v1/events/00010212T105500Z/https%3A%2F%2Fnstares-org-sms.herokuapp.com%2Fnotify" options)]
  
 ;;  (if error    
 ;;    (println "Failed, exception: " error)
 ;;    (do
 ;;      (println "HTTP GET success: " status)
 ;;      (println "HEADERS: " headers)
 ;;      (println "BODY: " body)
 ;;      )))









(defn to-eastcoast [time]
  (t/to-time-zone time (t/time-zone-for-offset -5)))


(def custom-formatter (f/formatter "h:mma MM-dd-yy"))

(defn custom-parse-date [date] ;; add changeable time zones?
  (to-eastcoast (f/parse custom-formatter date)))


(defn unparse-date
  "bring the time object back to the string" [date]
  (f/unparse custom-formatter date))


(defn parse-message 
  "format is gonna be Dinner With Andre ; 3:00PM 2-8-18"
  [message]
  (let [[title date] (string/split message #";")]
    {:event-title (string/trim title)
     :event-date (str (custom-parse-date (string/trim date))) ;; THESE ARE SAME FOR NOW
     :notification-date  (custom-parse-date (string/trim date))}))



(defn incoming-text [request]
  (let [incoming-number (get-in request [:params "From"])
        incoming-message (get-in request [:params "Body"])]
    (if (= incoming-number my-number)
      (do
;;        (send-to-ifttt incoming-message)
        ;; parse the message somehow so that it can be processed
;;        (println (str request))
        (schedule-notification (parse-message incoming-message))
        (return-twilio-message "Got it, keep on truckin'!"))
      (do
        (binding [*out* *err*]
          (println (str "Bad number! Got " incoming-number ", wanted " my-number)))
        return-unauthorized))))


 
(defn send-notification
  "takes the request from temporize and sends out the notification text!
  expects a map like {:event-title str :event-date string}"

  [notification]
  (println (str "THIS IS WHAT IS RECEIVED FROM TEMPORIZE: \n" notification))
  ;;  (println (str "THIS IS THE BODY RECEIVED: \n \n" (slurp (:body notification))))

  (let [params (:params notification)
        event-title (get params "event-title")
        event-date (get params "event-date")]
    (tw/send-message (System/getenv "MY_NUMBER") (System/getenv "TWILIO_NUMBER")
                     {:Body (str "Reminder: " event-title " at "  event-date)})))
  ;; parse the request coming from temporize
  ;; then take the message and send it through return-twilio message
;; do stuff


;; (send-notification



;; SCHEDULE EVENT WHEN YOU GET A MESSAGE, AFTER IT'S PARSED


(defroutes app
  (POST "/incoming" [request] incoming-text)
  (POST "/notify" [request] send-notification))

(defn -main []
  (let [port (Integer/parseInt (or (System/getenv "PORT") "5000"))]
    (run-server (wrap-params app :params) {:port port})
    (println (str "listening on port " port))))


;;(require '[pretrttime-nlp :as ]

;; (find-ns 'dropbox-repl.core)
;; (require '[dropbox-repl.core :as dr])
;; (dr/get-current-account)


;;(get-current-account)

;;;;;;;;;;; ORG EDITOR ;;;;;;;;;;;

;; TIME STUFF, FORMATTING STUFF












