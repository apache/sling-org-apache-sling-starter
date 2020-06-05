[<img src="https://sling.apache.org/res/logos/sling.png"/>](https://sling.apache.org)

 [![Build Status](https://builds.apache.org/buildStatus/icon?job=Sling/sling-org-apache-sling-starter/master)](https://builds.apache.org/job/Sling/job/sling-org-apache-sling-starter/job/master) [![Test Status](https://img.shields.io/jenkins/t/https/builds.apache.org/job/Sling/job/sling-org-apache-sling-starter/job/master.svg)](https://builds.apache.org/job/Sling/job/sling-org-apache-sling-starter/job/master/test_results_analyzer/) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.apache.sling/org.apache.sling.starter/badge.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.apache.sling%22%20a%3A%22org.apache.sling.starter%22) [![JavaDocs](https://www.javadoc.io/badge/org.apache.sling/org.apache.sling.starter.svg)](https://www.javadoc.io/doc/org.apache.sling/org.apache.sling.starter) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

# Apache Sling Starter

This module is part of the [Apache Sling](https://sling.apache.org) project.

The starter project produces both a Standalone Java Application which
contains everything needed to run the Launchpad in a single JAR file and a Web
Application.

It is **not meant to be a production-ready setup**, more as a way to facilitate experimenting and learning Sling. 

See [Releasing a new version of the Sling starter](https://cwiki.apache.org/confluence/display/SLING/Releasing+a+new+version+of+the+Sling+Starter) for how to create a release of this module.

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

Experimental Feature Model support
----------------------------------------

During the build the provisioning model files will be converted on the fly to feature model files
on-the-fly. The conversion taking into account the `oak_tar` runmode places its results in
`target/fm/oak_tar`. In a similar way, the MongoDB feature files are placed under
`target/fm/oak_mongo`.

For convenience, the results are aggregates in a single, standalone feature file. Due to technical
limitations only a single aggregate feature file is created, by default the `oak_tar` one, found
under  `target/slingfeature-tmp/feature-oak_tar.json`.

If you don't have a copy of the feature launcher jar, download it, for instance using

    $ mvn dependency:get dependency:copy \
        -Dartifact=org.apache.sling:org.apache.sling.feature.launcher:LATEST \
        -DoutputDirectory=.

To launch Sling using the feature launcher, simply execute

    $ java -jar org.apache.sling.feature.launcher-*.jar -f target/slingfeature-tmp/feature-oak_tar.json
    
To clean up the repository state just delete the `launcher` directory.

To generate the oak_mongo aggregate run the build and define the `fm.oak_mongo` property, e.g.

    $ mvn clean package -Dfm.oak_mongo
    
The instruction to launch Sling then becomes

     $ java -jar org.apache.sling.feature.launcher-*.jar -f target/slingfeature-tmp/feature-oak_mongo.json
     
