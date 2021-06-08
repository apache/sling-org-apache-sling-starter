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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.felix.utils.json.JSONParser;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

@RunWith(Parameterized.class)
public class SmokeIT {
    private static final String CHECK_PATHS_PROPERTY = "starter.check.paths";
    private static final int MAX_READABLE_INDEX = 50;

    private final int slingHttpPort;
    private static final int STARTER_MIN_BUNDLES_COUNT = Integer.getInteger("starter.min.bundles.count", Integer.MAX_VALUE);

    @Rule
    public final StarterReadyRule launchpadRule;
    private HttpClientContext httpClientContext;

    @Parameterized.Parameters(name = "Port: {0,number,#}")
    public static Collection<Object[]> data() {
        // This is a string of Sling instance port numbers to test, like
        //     false:80,true:1234
        // meaning: do not skip testing on port 80 but skip testing port 1234
        final Stream<String> portDefs = Stream.of(System.getProperty("starter.http.test.ports").split(","));
        final List<Object[]> result = new ArrayList<>();
        portDefs.forEach(def -> {
            final String [] parts = def.split(":");
            if("false".equals(parts[0])) {
                result.add(new Object[]{Integer.valueOf(parts[1].trim())});
            }
        });
        return result;
    }

    public SmokeIT(int slingHttpPort) {
        this.slingHttpPort = slingHttpPort;
        launchpadRule = new StarterReadyRule(slingHttpPort);
    }

    @Before
    public void prepareHttpContext() {

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials("admin", "admin");
        credsProvider.setCredentials(new AuthScope("localhost", slingHttpPort), creds);

        BasicAuthCache authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(new HttpHost("localhost", slingHttpPort, "http"), basicAuth);

        httpClientContext = HttpClientContext.create();
        httpClientContext.setCredentialsProvider(credsProvider);
        httpClientContext.setAuthCache(authCache);
    }

    private CloseableHttpClient newClient() {

        return HttpClientBuilder.create()
                .setDefaultCredentialsProvider(httpClientContext.getCredentialsProvider())
                .build();
    }

    @Test
    public void verifyAllBundlesStarted() throws Exception {

        try ( CloseableHttpClient client = newClient() ) {

            HttpGet get = new HttpGet("http://localhost:" + slingHttpPort + "/system/console/bundles.json");

            // pass the context to ensure preemptive basic auth is used
            // https://hc.apache.org/httpcomponents-client-ga/tutorial/html/authentication.html
            try ( CloseableHttpResponse response = client.execute(get, httpClientContext) ) {

                if ( response.getStatusLine().getStatusCode() != 200 ) {
                    fail("Unexpected status line " + response.getStatusLine());
                }

                Header contentType = response.getFirstHeader("Content-Type");
                assertThat("Content-Type header", contentType.getValue(), CoreMatchers.startsWith("application/json"));

                Map<String, Object> obj = new JSONParser(response.getEntity().getContent()).getParsed();

                @SuppressWarnings("unchecked")
                List<Object> status = (List<Object>) obj.get("s");

                @SuppressWarnings("unchecked")
                List<Object> bundles = (List<Object>) obj.get("data");
                if(bundles.size() < STARTER_MIN_BUNDLES_COUNT) {
                    fail("Expected at least " + STARTER_MIN_BUNDLES_COUNT + " bundles, got " + bundles.size());
                }

                BundleStatus bs = new BundleStatus(status);

                if ( bs.resolvedBundles != 0 || bs.installedBundles != 0 ) {

                    StringBuilder out = new StringBuilder();
                    out.append("Expected all bundles to be active, but instead got ")
                        .append(bs.resolvedBundles).append(" resolved bundles, ")
                        .append(bs.installedBundles).append(" installed bundlles: ");

                    for ( int i = 0 ; i < bundles.size(); i++ ) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> bundle = (Map<String, Object>) bundles.get(i);

                        String bundleState = (String) bundle.get("state");
                        String bundleSymbolicName = (String) bundle.get("symbolicName");
                        String bundleVersion = (String) bundle.get("version");

                        switch ( bundleState ) {
                            case "Active":
                            case "Fragment":
                                continue;

                            default:
                                out.append("\n- ").append(bundleSymbolicName).append(" ").append(bundleVersion).append(" is in state " ).append(bundleState);
                        }
                    }

                    fail(out.toString());
                }
            }
        }
    }

    @Test
    public void ensureRepositoryIsStarted() throws Exception {
        try ( CloseableHttpClient client = newClient() ) {

            HttpGet get = new HttpGet("http://localhost:" + slingHttpPort + "/server/default/jcr:root/content");

            try ( CloseableHttpResponse response = client.execute(get) ) {

                if ( response.getStatusLine().getStatusCode() != 200 ) {
                    fail("Unexpected status line " + response.getStatusLine());
                }

                Header contentType = response.getFirstHeader("Content-Type");
                assertThat("Content-Type header", contentType.getValue(), equalTo("text/xml"));

                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setNamespaceAware(true);
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document document = db.parse(response.getEntity().getContent());

                Element docElement = document.getDocumentElement();
                NamedNodeMap attrs = docElement.getAttributes();

                Node nameAttr = attrs.getNamedItemNS("http://www.jcp.org/jcr/sv/1.0", "name");
                assertThat("no 'name' attribute found", nameAttr, notNullValue());
                assertThat("Invalid name attribute value", nameAttr.getNodeValue(), equalTo("content"));
            }
        }
    }

    /**
     * For testing the SLING-10402 scenario
     */
    @Test
    public void verifyReadableUrls() throws Exception {
        final AtomicInteger checkedPaths = new AtomicInteger();
        try ( CloseableHttpClient client = newClient() ) {
            Stream.of(System.getProperty(CHECK_PATHS_PROPERTY).split(","))
                .map(path -> path.trim())
                .filter(path -> !path.isEmpty())
                .forEach(path -> {
                    HttpGet get = new HttpGet(String.format("http://localhost:%d%s", slingHttpPort, path));
                    try (CloseableHttpResponse response = client.execute(get, httpClientContext)) {
                        checkedPaths.incrementAndGet();
                        if ( response.getStatusLine().getStatusCode() != 200 ) {
                            fail(String.format("Unexpected status line \"%s\" for %s", response.getStatusLine(), path));
                        }
                    } catch(Exception ex) {
                        throw new RuntimeException(ex);
                    }
            });
        }

        final int minTests = 6;
        assertTrue(String.format("Expecting at least %d tests, got %d", minTests, checkedPaths.get()), checkedPaths.get() >= minTests);
    }

    static class BundleStatus {

        long totalBundles;
        long activeBundles;
        long activeFragments;
        long resolvedBundles;
        long installedBundles;

        public BundleStatus(List<Object> array) {

            totalBundles = (Long)array.get(0);
            activeBundles = (Long)array.get(1);
            activeFragments = (Long)array.get(2);
            resolvedBundles = (Long)array.get(3);
            installedBundles = (Long)array.get(4);

        }
    }
}
