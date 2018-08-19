- server-local => This is the spring-cloud-dataflow-server-local

- cloud-task-demo => simply mvn clean install and use that jar for application registration in http://localhost:9393/dashboard/#/apps

  register application w/ URI => maven://com.nayak:cloud‑task‑demo:jar:0.0.1‑SNAPSHOT

- managing-scdf =>  curl --data "task=<registered-app>" localhost:8080/task

NOTE:

Though Scheduling feature is available in 1.6 and is discussed in Spring Youtube videos, but Scheduling feature is not available in spring-cloud-dataflow-server-local . 

Scheduling is available in 1.6 on cloudfoundery module and not in the local one :(  
