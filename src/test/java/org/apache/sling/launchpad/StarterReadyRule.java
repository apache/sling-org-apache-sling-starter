/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sling.launchpad;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.rules.ExternalResource;

public class StarterReadyRule extends ExternalResource {

    private final List<UrlCheck> checks = new ArrayList<>();
    private final int launchpadPort;
    private static final Map<Integer, Throwable> previousFailures = new HashMap<>();

    public StarterReadyRule(int launchpadPort) {
        this.launchpadPort = launchpadPort;
        final String baseURL = String.format("http://localhost:%d", launchpadPort);
        checks.add(new UrlCheck(baseURL, "/server/default/jcr:root/content"));
        checks.add(new UrlCheck(baseURL, "/content/starter.html") {
            final String READY_MARKER = "Do not remove this comment, used for Starter integration tests";
            @Override
            public String run(HttpResponse response) throws Exception {
                try (InputStreamReader isr = new InputStreamReader(response.getEntity().getContent());
                        BufferedReader reader = new BufferedReader(isr)) {

                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains(READY_MARKER)) {
                            return null;
                        }
                    }
                }

                return String.format("Did not find 'ready' marker [%s] in the response body", READY_MARKER);
            }
        });
    }

    @Override
    protected void before() throws Throwable {
        final Throwable previous = previousFailures.get(launchpadPort);
        if(previous != null) {
            throw new RuntimeException(
                String.format("%s failed previously on port %d, refusing to run more tests", getClass().getSimpleName(), launchpadPort),
                previous
            );
        }
        final int TRIES = 60;
        final int WAIT_BETWEEN_TRIES_MILLIS = 1000;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            try {
                UrlCheck.runAll(client, TRIES, WAIT_BETWEEN_TRIES_MILLIS, checks);
            } catch(Throwable t) {
                previousFailures.put(launchpadPort, t);
                throw t;
            }
        }
    }
}
