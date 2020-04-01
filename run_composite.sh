#!/bin/sh -ex
java -jar target/org.apache.sling.starter-12-SNAPSHOT.jar -Dsling.run.modes=oak_composite_run -c sling/sling-composite

