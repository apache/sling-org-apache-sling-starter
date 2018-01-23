# Apache Sling Starter

This module is part of the [Apache Sling](https://sling.apache.org) project.

The starter project produces both a Standalone Java Application which
contains everything needed to run the Launchpad in a single JAR file and a Web
Application.

How to run the Sling Starer module in Standalone mode
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
