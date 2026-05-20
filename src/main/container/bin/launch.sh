#!/bin/bash -e

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


feature_name="${1}"

if [[ -z "${feature_name}" ]]; then
    echo "[ERROR] Missing feature name. Aborting"
    exit 1
fi

shift

extra_features=()
repository_urls=()

while [[ $# -gt 0 ]]; do
    case "$1" in
        --extra-features)
            shift
            start_count=${#extra_features[@]}
            while [[ $# -gt 0 && "$1" != -* ]]; do
                extra_features+=("$1")
                shift
            done
            if [[ ${#extra_features[@]} -eq ${start_count} ]]; then
                echo "[ERROR] --extra-features requires at least one value. Aborting"
                exit 1
            fi
            ;;
        --repository-urls)
            shift
            start_count=${#repository_urls[@]}
            while [[ $# -gt 0 && "$1" != -* ]]; do
                repository_urls+=("$1")
                shift
            done
            if [[ ${#repository_urls[@]} -eq ${start_count} ]]; then
                echo "[ERROR] --repository-urls requires at least one value. Aborting"
                exit 1
            fi
            ;;
        -*)
            echo "[ERROR] Unknown option $1. Aborting"
            exit 1
            ;;
        *)
            echo "[ERROR] Unexpected argument $1. Aborting"
            exit 1
            ;;
    esac
done

feature=$(find artifacts -name "*${feature_name}*.slingosgifeature")

if [[ ! -f "${feature}" ]]; then
    echo "[ERROR] Did not find any feature file matching name ${feature_name}. Aborting"
    exit 1
fi

docker_feature=$(find artifacts -name "*docker.slingosgifeature")

echo "[INFO] Selected ${feature} for launching"
echo "[INFO] Automatically appended ${docker_feature}"

feature_list=("${feature}")
for extra_feature in "${extra_features[@]}"; do
    echo "[INFO] Appended extra feature ${extra_feature}"
    feature_list+=("${extra_feature}")
done
feature_list+=("${docker_feature}")

feature=$(IFS=, ; printf '%s' "${feature_list[*]}")

effective_repository_urls=(
    "file:///opt/sling/artifacts"
)

if [[ ${#repository_urls[@]} -eq 0 ]]; then
    effective_repository_urls+=(
        "https://repo.maven.apache.org/maven2"
        "https://repository.apache.org/content/groups/snapshots"
    )
else
    effective_repository_urls+=("${repository_urls[@]}")
fi

launcher_args=(
    -u
    "${effective_repository_urls[@]}"
    -CC "org.apache.sling.commons.log.LogManager=MERGE_LATEST"
)

echo "[INFO] Using repository URLs ${effective_repository_urls[*]}"

launcher_args+=(-f "${feature}")

if [ ! -z "${JAVA_DEBUG_PORT}" ]; then
    JAVA_DEBUG_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:${JAVA_DEBUG_PORT}"
fi
# remove add-opens after SLING-10831 is fixed
JAVA_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED ${JAVA_DEBUG_OPTS} ${EXTRA_JAVA_OPTS}"

agents=$(find agents -name "*.jar")
for agent in ${agents}; do
    echo "[INFO] Discovered agent ${agent}"
    JAVA_OPTS="-javaagent:${agent} ${JAVA_OPTS}"
done

export JAVA_OPTS
echo "[INFO] JAVA_OPTS=${JAVA_OPTS}"

echo "[INFO] Launch feature list: ${feature}"

exec org.apache.sling.feature.launcher/bin/launcher "${launcher_args[@]}"
