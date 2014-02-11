(defproject thread-exec "0.1.0-SNAPSHOT"
  :description "Intelligent thread execution and pooling that automatically create different pools to isolate slow running tasks from faster running tasks and avoid the system slowing down."
  :url "https://github.com/gerritjvv/thread-exec"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :global-vars {*warn-on-reflection* true
                *assert* false}
  :plugins [
         [lein-midje "3.0.1"] [lein-marginalia "0.7.1"]
         [lein-kibit "0.0.8"] [no-man-is-an-island/lein-eclipse "2.0.0"]
           ]
  :dependencies [
                [midje "1.6-alpha2" :scope "test"]
		[org.clojure/clojure "1.5.1"]])
