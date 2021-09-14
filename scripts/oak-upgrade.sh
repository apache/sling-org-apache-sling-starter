#!/bin/sh -e
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#  http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

oak_version=$(cat pom.xml | sed -n 's/.*<oak\.version>\(.*\)<\/oak\.version>.*/\1/p')
oak_run_jar=$HOME/.m2/repository/org/apache/jackrabbit/oak-upgrade/${oak_version}/oak-upgrade-${oak_version}.jar
if [ ! -f ${oak_run_jar} ]; then
    mvn -q dependency:get -Dartifact=org.apache.jackrabbit:oak-upgrade:${oak_version} -Dtransitive=false
fi

java -jar ${oak_run_jar} "$@"
