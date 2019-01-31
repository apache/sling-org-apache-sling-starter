[<img src="https://sling.apache.org/res/logos/sling.png"/>](https://sling.apache.org)

 [![Build Status](https://builds.apache.org/buildStatus/icon?job=Sling/sling-org-apache-sling-starter/master)](https://builds.apache.org/job/Sling/job/sling-org-apache-sling-starter/job/master) [![Test Status](https://img.shields.io/jenkins/t/https/builds.apache.org/job/Sling/job/sling-org-apache-sling-starter/job/master.svg)](https://builds.apache.org/job/Sling/job/sling-org-apache-sling-starter/job/master/test_results_analyzer/) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.apache.sling/org.apache.sling.starter/badge.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.apache.sling%22%20a%3A%22org.apache.sling.starter%22) [![JavaDocs](https://www.javadoc.io/badge/org.apache.sling/org.apache.sling.starter.svg)](https://www.javadoc.io/doc/org.apache.sling/org.apache.sling.starter) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

# Apache Sling Starter

This module is part of the [Apache Sling](https://sling.apache.org) project.

The starter project produces both a Standalone Java Application which
contains everything needed to run the Launchpad in a single JAR file and a Web
Application.

How to run the Sling Starter module in Standalone mode
----------------------------------------

  NOTE: "mvn clean" deletes the "sling" work directory in the project base
        directory. It is advisable to use a work directory outside of the
        project directory.

1) Build the Sling Starter using 

	mvn clean install
	
in the current directory.

2) Start the generated jar with

	 java -jar target/org.apache.sling.starter-10-SNAPSHOT.jar 
	 
Use the correct version number instead of 10-SNAPSHOT, if needed.

3) Browse Sling in:

        http://localhost:8080

How to run the Sling Starter module in webapp mode
----------------------------------------

1) Build the Sling Starter using 

	mvn clean install
	
in the current directory.

2) Deploy target/org.apache.sling.starter-10-SNAPSHOT.war to your favorite application
server or servlet container. Servlet 3.1 is a minimum requirement for the web app.
