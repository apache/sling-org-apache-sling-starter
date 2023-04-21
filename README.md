[![Apache Sling](https://sling.apache.org/res/logos/sling.png)](https://sling.apache.org)

&#32;[![Build Status](https://ci-builds.apache.org/job/Sling/job/modules/job/sling-org-apache-sling-starter/job/master/badge/icon)](https://ci-builds.apache.org/job/Sling/job/modules/job/sling-org-apache-sling-starter/job/master/)&#32;[![Test Status](https://img.shields.io/jenkins/tests.svg?jobUrl=https://ci-builds.apache.org/job/Sling/job/modules/job/sling-org-apache-sling-starter/job/master/)](https://ci-builds.apache.org/job/Sling/job/modules/job/sling-org-apache-sling-starter/job/master/test/?width=800&height=600)&#32;[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=apache_sling-org-apache-sling-starter&metric=alert_status)](https://sonarcloud.io/dashboard?id=apache_sling-org-apache-sling-starter)&#32;[![JavaDoc](https://www.javadoc.io/badge/org.apache.sling/org.apache.sling.starter.svg)](https://www.javadoc.io/doc/org.apache.sling/org.apache.sling.starter)&#32;[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.apache.sling/org.apache.sling.starter/badge.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.apache.sling%22%20a%3A%22org.apache.sling.starter%22) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

# Apache Sling Starter

This module is part of the [Apache Sling](https://sling.apache.org) project.

The starter project produces feature artifacts that can be launched using the
[Feature Launcher](https://github.com/apache/sling-org-apache-sling-feature-launcher).

It is **not meant to be a production-ready setup**, more as a way to facilitate experimenting and learning Sling. 

See [Releasing a new version of the Sling starter](https://cwiki.apache.org/confluence/display/SLING/Releasing+a+new+version+of+the+Sling+Starter) for how to create a release of this module.

## How to run the Sling Starter module in Standalone mode

> **Note**
> This is a note "mvn clean" deletes the "launcher" work directory in the project base
> directory. It is advisable to use a work directory outside of the project directory.

1. Build the Sling Starter using
   ```bash
   mvn clean install
   ```
   in the current directory.

   > **Hint** 
   > You can defer stopping the instance after running the ITs with argument `-Dfeature-launcher.waitForInput=true` to do some manual checks.

2. Start Sling backed by 
   - Oak SegmentStore with
     ```bash
     target/dependency/org.apache.sling.feature.launcher/bin/launcher -f target/slingfeature-tmp/feature-oak_tar.json
     ```
   - Oak MongoDB DocumentStore with
     ```bash
     target/dependency/org.apache.sling.feature.launcher/bin/launcher -f target/slingfeature-tmp/feature-oak_mongo.json
     ```
     This expects a MongoDB server to be running, search for `mongodb://` in the feature files for the expected URL (currently `mongodb://localhost:27017`).

3. Browse Sling in [localhost:8080](http://localhost:8080)

## How to run the Sling Starter Docker image

The following tags are supported

| Tag name       | Supported platforms | Links                                                                                                                                                                                     |
|----------------|---------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `12`, `latest` | `amd64`             | [Dockerfile](https://github.com/apache/sling-org-apache-sling-starter/blob/org.apache.sling.starter-12/Dockerfile), [Release notes](https://sling.apache.org/news/sling-12-released.html) |
| `11`           | `amd64`             | [Dockerfile](https://github.com/apache/sling-org-apache-sling-starter-docker/blob/11/Dockerfile), [Release notes](https://sling.apache.org/news/sling-11-released.html)                   |
| `10`           | `amd64`             | [Dockerfile](https://github.com/apache/sling-org-apache-sling-starter-docker/blob/10/Dockerfile), [Release notes](https://sling.apache.org/news/sling-10-released.html)                   |
| `9`            | `amd64`             | [Dockerfile](https://github.com/apache/sling-org-apache-sling-starter-docker/blob/9/Dockerfile), [Release notes](https://sling.apache.org/news/sling-launchpad-9-released.html)           |
| `snapshot`     | `amd64`, `arm64`    | [Dockerfile](https://github.com/apache/sling-org-apache-sling-starter/blob/master/Dockerfile)                                                                                             |

1. Start Sling Docker image backed by 
   - Oak SegmentStore with
     ```bash
     docker volume create sling-launcher
     docker run --rm -p 8080:8080 -v sling-launcher:/opt/sling/launcher apache/sling:snapshot
     ```
   - Oak MongoDB DocumentStore with
     ```bash
     docker volume create sling-launcher
     docker run --rm -p 27017:27017 mongo:4.4.6
     docker run --rm -p 8081:8080 -v sling-launcher:/opt/sling/launcher apache/sling:snapshot oak_mongo
     ```

The [docker/](docker/) directory contains sample files related to container-based development.

## Testing

The Sling Starter will execute two suites of tests using the `maven-surefire-plugin`:

1. A small set of smoke tests, embedded in the project, that verify the basic functionality of the Starter
2. An extensive set of end-to-end tests that verify the overall functionality of the Starter and the bundles that are embedded into it

By default, these are both executed when building the project against an Oak SegmentNodeStore backend.

Additionally, when the `ci` profile is enabled the smoke tests are also executed in against an Oak DocumentNodeStore backend. For technical resons, the full end-to-end tests are not executed.


## Docker

> The Sling Starter Docker image is only for testing/learning purposes. For your custom applications, 
> please configure your [Feature model](https://github.com/apache/sling-org-apache-sling-feature) and 
> build your image with all dependencies and configurations built in.

Our example Docker image uses `/opt/sling/bin/launch.sh` script as the `entrypoint`. It supports the 
following environment variables:

| Environment variable | Description                            | Example              |
|----------------------|----------------------------------------|----------------------|
| `JAVA_DEBUG_PORT`    | Run Sling Starter in Java debug mode   | `5005`               |
| `EXTRA_JAVA_OPTS`    | Extra java options e.g `Xmx` or `Xms`. | `-Xms256m -Xmx2048m` |

> **Example**
> running Sling Starter in debug mode with custom memory settings

```bash
docker run -p 8080:8080 -p 5005:5005 -e JAVA_DEBUG_PORT=5005 -e EXTRA_JAVA_OPTS='-Xms256m -Xmx2048m' apache/sling:snapshot
```

## Building the Docker image

This module can optionally build a Docker image. This is achieved by running a build with the `-Ddocker.skip=false` argument. By default, the image is built as `apache/sling:snapshot`. The tag can be overrriden using the `docker.image.tag` Maven property.

```
$ mvn clean package -Ddocker.skip=false -Ddocker.image.tag=local
$ docker run --rm -p 8080:8080 apache/sling:local
```

## Extending the Sling Starter

If you wish the extend the Sling Starter but would like to keep various application-level features out, you can
start with the `nosample_base` aggregate, which contains:

- all the base features
- Oak base features, without the NodeStore setup
- No applications ( Composum, Slingshot, etc )

For instance, launching an empty Sling Starter with segment persistence can be achieved by running

    target/dependency/org.apache.sling.feature.launcher/bin/launcher -f target/slingfeature-tmp/feature-nosample_base.json,target/slingfeature-tmp/feature-oak_persistence_sns.json
    
Your own feature files can be added to the feature list.


## Helper scripts

The `scripts` directory contains helper scripts that will aid with local development by simplifying the use of tools external to the Sling Starter.
