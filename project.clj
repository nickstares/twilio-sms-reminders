(defproject twilio-sms-reminders "0.1.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :user {:plugins [[cider/cider-nrepl "0.12.0-SNAPSHOT"]
                    [refactor-nrepl "0.3.0-SNAPSHOT"]
                   [lein-environ "1.1.0"]
                   [lein-ring "0.12.3"]
                   ;;[lein-localrepo "0.5.4"]
                   ]
         :ring {:handler twilio-sms-reminders/-main}          
         :dependencies [[alembic "0.3.2"]
                        [org.clojure/tools.nrepl "0.2.7"]]}

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [http-kit "2.1.18"]
                 [compojure "1.6.0"]
                 [ring/ring-defaults "0.1.5"]
                 [ring/ring-codec "1.1.0"]
                 [twijlio "0.1.2-SNAPSHOT"]
                 ]
  
                 ;; [org.clojure/java.jdbc "LATEST"]
                 ;; [org.postgresql/postgresql "LATEST"]

;;                              [org.opcsoft.prettytime/prettytime-nlp "4.0.1.Final"]



;;                 [com.gfredericks/org-editor "0.1.0"]
                 ;; [com.jaydeesimon/dropbox-repl "0.1.1"]]
  

  ;;:repositories {"local" ~(str (.toURI (java.io.File. "/Users/nicholasstares/.m2/repository/org/opcsoft/prettytime/prettytime-nlp/4.0.1.Final")))}


  
  :jvm-opts ["-Xmx1G"]
:min-lein-version "2.0.0"
  :main twilio-sms-reminder.core)

