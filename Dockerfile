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

FROM docker.io/openjdk:17-slim

LABEL org.opencontainers.image.authors="dev@sling.apache.org"

EXPOSE 8080

RUN mkdir -p /opt/sling/artifacts
COPY src/main/container /opt/sling
COPY target/dependency/org.apache.sling.feature.launcher.jar /opt/sling
COPY target/artifacts/ /opt/sling/artifacts/

RUN groupadd -r sling && \
    useradd --no-log-init -r -g sling sling && \
    mkdir /opt/sling/launcher && \
    chown sling:sling /opt/sling/launcher

# ensure all files are readable by the sling user
# for some reason some jar files are 0600 and others are 0644
RUN find /opt/sling/artifacts -type f | xargs chmod 0644

VOLUME /opt/sling/launcher

USER sling:sling

WORKDIR /opt/sling
ENTRYPOINT [ "/opt/sling/bin/launch.sh" ]
CMD ["oak_tar"]
