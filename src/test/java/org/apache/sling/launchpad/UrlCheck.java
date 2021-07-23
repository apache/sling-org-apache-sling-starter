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

import java.net.ConnectException;
import java.util.Collection;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

class UrlCheck {
    private final String url;

    UrlCheck(String baseURL, String path) {
        final String separator = baseURL.endsWith("/") ? "" : "/";
        this.url = baseURL + separator + path;
    }

    String getUrl() {
        return url;
    }

    /**
     * @param response the HttpResponse
     * @return null if check check was successful, an error description otherwise
     * @throws Exception
     */
    String run(HttpResponse response) throws Exception {
        return null;
    }

    /** Run all supplied checks */
    static void runAll(CloseableHttpClient client, int retryCount, int msecBetweenRetries, Collection<UrlCheck> checks) throws Exception {
        for(UrlCheck check : checks) {
            String lastFailure = null;
            HttpGet get = new HttpGet(check.getUrl());
            int requestCount = 0;
            final long start = System.currentTimeMillis();
            for (int i = 0; i < retryCount; i++) {
                try (CloseableHttpResponse response = client.execute(get)) {
                    requestCount++;
                    if (response.getStatusLine().getStatusCode() != 200) {
                        lastFailure = "Status code is " + response.getStatusLine();
                        Thread.sleep(msecBetweenRetries);
                        continue;
                    }
    
                    lastFailure = check.run(response);
                    if (lastFailure == null) {
                        break;
                    }
                } catch ( ConnectException e ) {
                    lastFailure = e.getClass().getName() + " : " + e.getMessage();
                }
    
                Thread.sleep(msecBetweenRetries);
            }
            
            if(lastFailure != null) {
                throw new RuntimeException(
                    String.format("Starter not ready or path not found after %d tries (%d msec). Request to URL %s failed with message '%s'",
                    requestCount, 
                    System.currentTimeMillis() - start,
                    check.getUrl(), 
                    lastFailure));
            }
        }
    }

}