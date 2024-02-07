#!/bin/bash

mvn install

cp target/ws-app-service.war ../../../software/apache-tomcat-10.1.13/webapps/

../../../software/apache-tomcat-10.1.13/bin/startup.sh
