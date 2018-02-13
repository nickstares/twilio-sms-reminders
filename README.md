# twilio-sms-reminders
#### (work in progress)

This is a personal sms reminder service written in Clojure. It's designed to run on Heroku, using Temporize for scheduling jobs and Twilio for sending and receiving sms messages.

### Features:
- Send text message to your Twilio number in format `{Notification Title} ; {h:mma MM-dd-yy}` e.g. `Dinner with Andre ; 7:00PM 10-11-1981`

- receive text message with reminder info at that time


### TODO:
- Make usable for other people
- Use `prettytime-nlp` to parse incoming messages as natural language/in a less strict format
- Add `org-mode` integration through Dropbox
