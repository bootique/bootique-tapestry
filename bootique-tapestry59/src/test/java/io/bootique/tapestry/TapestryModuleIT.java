/*
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.bootique.tapestry;

import io.bootique.jetty.JettyModule;
import io.bootique.jetty.MappedServlet;
import io.bootique.jetty.junit5.JettyTester;
import io.bootique.junit5.BQTest;
import io.bootique.junit5.BQTestFactory;
import io.bootique.junit5.BQTestTool;
import io.bootique.tapestry.v59.TapestryModule;
import io.bootique.tapestry.v59.testapp2.bq.TestApp2BootiqueModule;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.tapestry5.services.LibraryMapping;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@BQTest
public class TapestryModuleIT {

    @BQTestTool
    final BQTestFactory app = new BQTestFactory().autoLoadModules();

    @BQTestTool
    final JettyTester jetty = JettyTester.create();

    @Test
    public void pageRender_Index() {
        app.app("-s")
                .module(jetty.moduleReplacingConnectors())
                .property("bq.tapestry.appPackage", "io.bootique.tapestry.v59.testapp1")
                .run();

        assertHtml("/", "Index", "[xyz]");
    }

    @Test
    public void pageRender_Page2() {
        app.app("-s")
                .module(jetty.moduleReplacingConnectors())
                .property("bq.tapestry.appPackage", "io.bootique.tapestry.v59.testapp1")
                .run();

        assertHtml("/page2", "I am wrapped", "[I am page2 body]");
    }

    @Test
    public void pageRender_T5_Injection() {
        app.app("-s")
                .module(jetty.moduleReplacingConnectors())
                .property("bq.tapestry.appPackage", "io.bootique.tapestry.v59.testapp2")
                .property("bq.tapestry.name", "testapp2")
                .run();

        assertHtml("/", "Index", "[III]");
    }

    @Test
    public void pageRender_T5_BQInjection() {
        app.app("-s")
                .module(jetty.moduleReplacingConnectors())
                .modules(TestApp2BootiqueModule.class)
                .property("bq.tapestry.appPackage", "io.bootique.tapestry.v59.testapp2")
                .property("bq.tapestry.name", "testapp2")
                .run();

        assertHtml("/bqservices", "BQServices", "{III}");
    }

    @Test
    public void pageRender_T5_BQInjection_Annotations() {
        app.app("-s", "testarg", "testarg2")
                .module(jetty.moduleReplacingConnectors())
                .modules(TestApp2BootiqueModule.class)
                .property("bq.tapestry.appPackage", "io.bootique.tapestry.v59.testapp2")
                .property("bq.tapestry.name", "testapp2")
                .run();

        assertHtml("/bqannotatedservices", "BQAnnotatedServices", "-s_testarg_testarg2");
    }

    @Test
    public void pageRender_LibComponent() {
        app.app("-s")
                .module(jetty.moduleReplacingConnectors())
                .module(b -> TapestryModule.extend(b)
                        .addLibraryMapping(new LibraryMapping("lib", "io.bootique.tapestry.v59.testlib1")))
                .property("bq.tapestry.appPackage", "io.bootique.tapestry.v59.testapp2")
                .run();

        assertHtml("/bqpagewithlibcomponent", "Index with Lib", "<b>__val__</b>");
    }

    @Test
    public void ignorePaths() {
        app.app("-s")
                .module(jetty.moduleReplacingConnectors())
                .module(b -> {
                    TapestryModule.extend(b).addIgnoredPath("/ignored_by_tapestry/*");
                    JettyModule.extend(b).addMappedServlet(MappedServlet.ofStatic("/").name("default").build());
                })
                .property("bq.tapestry.appPackage", "io.bootique.tapestry.v59.testapp1")
                .property("bq.jetty.staticResourceBase", "classpath:docroot")
                .run();

        assertHtml("/", "Index", "[xyz]");
        assertHtml("/ignored_by_tapestry/static.html", "Static", "I am a static file");
    }

    @Test
    public void pageRender_T5Modules() {
        app.app("-s")
                .module(jetty.moduleReplacingConnectors())
                .module(b -> TapestryModule.extend(b).addTapestryModule(TestApp3Module.class))
                .property("bq.tapestry.appPackage", "io.bootique.tapestry.v59.testapp3")
                .run();

        assertHtml("/page1", "Testapp3 Page1", ":DeferredServiceImpl:");
    }

    private void assertHtml(String uri, String expectedTitle, String expectedBody) {
        Response r = jetty.getTarget().path(uri).request(MediaType.TEXT_HTML).get();
        assertEquals(200, r.getStatus());

        String html = r.readEntity(String.class);

        // adding a small delay after reading the response. Otherwise, container may start shutdown
        // when T5 request is still in progress, resulting in stack traces in the logs.
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
        }

        assertTrue(html.startsWith("<!DOCTYPE html><html"));
        assertTrue(html.endsWith("</html>"));

        assertTrue(html.contains("<title>" + expectedTitle + "</title>"), () -> "Expected: " + expectedTitle);
        assertTrue(html.contains(expectedBody), () -> "Unexpected html: " + html);
    }
}
