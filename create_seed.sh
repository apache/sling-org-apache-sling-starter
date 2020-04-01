#!/bin/sh -e

echo "-----------------------------------"
echo "| PLEASE SHUTDOWN SLING MANUALLY  |"
echo "| AFTER STARTUP IS COMPLETE       |"
echo "-----------------------------------"

set +x
sleep 3

rm -rf sling/sling-seed
java -jar target/org.apache.sling.starter-12-SNAPSHOT.jar -Dsling.run.modes=oak_composite_seed -c sling/sling-seed
