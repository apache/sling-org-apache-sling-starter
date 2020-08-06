[<img src="https://sling.apache.org/res/logos/sling.png"/>](https://sling.apache.org)

 [![Build Status](https://builds.apache.org/buildStatus/icon?job=Sling/sling-org-apache-sling-starter/master)](https://builds.apache.org/job/Sling/job/sling-org-apache-sling-starter/job/master) [![Test Status](https://img.shields.io/jenkins/t/https/builds.apache.org/job/Sling/job/sling-org-apache-sling-starter/job/master.svg)](https://builds.apache.org/job/Sling/job/sling-org-apache-sling-starter/job/master/test_results_analyzer/) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.apache.sling/org.apache.sling.starter/badge.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.apache.sling%22%20a%3A%22org.apache.sling.starter%22) [![JavaDocs](https://www.javadoc.io/badge/org.apache.sling/org.apache.sling.starter.svg)](https://www.javadoc.io/doc/org.apache.sling/org.apache.sling.starter) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

# Apache Sling Starter

This module is part of the [Apache Sling](https://sling.apache.org) project.

The starter project produces feature artifacts that can be launched using the
[Feature Launcher](https://github.com/apache/sling-org-apache-sling-feature-launcher).

It is **not meant to be a production-ready setup**, more as a way to facilitate experimenting and learning Sling. 

See [Releasing a new version of the Sling starter](https://cwiki.apache.org/confluence/display/SLING/Releasing+a+new+version+of+the+Sling+Starter) for how to create a release of this module.

How to run the Sling Starter module in Standalone mode
----------------------------------------

  NOTE: "mvn clean" deletes the "launcher" work directory in the project base
        directory. It is advisable to use a work directory outside of the
        project directory.

1) Build the Sling Starter using 

	mvn clean install
	
in the current directory.

2) Download the feature launcher using

    mvn dependency:get dependency:copy -Dartifact=org.apache.sling:org.apache.sling.feature.launcher:1.1.4
    
    mvn dependency:copy -Dartifact=org.apache.sling:org.apache.sling.feature.launcher:1.1.4

3) Start Sling backed by an Oak SegmentStore with

    java -jar target/dependency/org.apache.sling.feature.launcher-1.1.4.jar -f target/slingfeature-tmp/feature-oak_tar.json
	
4) Browse Sling in:

        http://localhost:8080

For MongoDB support replace the launch command with

    java -jar target/dependency/org.apache.sling.feature.launcher-1.1.4.jar -f target/slingfeature-tmp/feature-oak_mongo.json

This expects a MongoDB server to be running, search for `mongodb://` in the feature files for the expected URL
(currently `mongodb://localhost:27017`).
