(defproject thread-exec "0.3.0"
  :description "Intelligent thread execution and pooling that automatically create different pools to isolate slow running tasks from faster running tasks and avoid the system slowing down."
  :url "https://github.com/gerritjvv/thread-exec"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :global-vars {*warn-on-reflection* true
                *assert* false}
  :javac-options ["-target" "1.6" "-source" "1.6" "-Xlint:-options"]
  :java-source-paths ["java"]
  
  :plugins [
         [lein-midje "3.0.1"] [lein-marginalia "0.7.1"]
         [lein-kibit "0.0.8"] [no-man-is-an-island/lein-eclipse "2.0.0"]
           ]
  :dependencies [
		[org.clojure/tools.logging "0.2.6"]
    		[midje "1.6-alpha2" :scope "test"]
    		[thread-load "0.3.2"]
		[org.clojure/clojure "1.8.0"]])
