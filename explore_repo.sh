#!/bin/sh -e

if [ $# -ne 1 ]; then
    echo "Usage: $0 repo_dir"
    exit 1
fi

repo_dir=$1

if [ ! -d $repo_dir ]; then
    echo "${repo_dir} is not a directory"
    exit 2
fi

oak_version=1.26.0
oak_run_jar=$HOME/.m2/repository/org/apache/jackrabbit/oak-run/${oak_version}/oak-run-${oak_version}.jar
if [ ! -f ${oak_run_jar} ]; then
    mvn dependency:get -Dartifact=org.apache.jackrabbit:oak-run:${oak_version}
fi

java -jar ${oak_run_jar} explore ${repo_dir}
