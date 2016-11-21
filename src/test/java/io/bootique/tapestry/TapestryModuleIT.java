package io.bootique.tapestry;

import io.bootique.jetty.JettyModule;
import io.bootique.jetty.test.junit.JettyTestFactory;
import io.bootique.tapestry.testapp2.bq.TestApp2BootiqueModule;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TapestryModuleIT {

    private static WebTarget BASE_TARGET = ClientBuilder.newClient().target("http://127.0.0.1:8080/");

    @Rule
    public JettyTestFactory app = new JettyTestFactory();

    @Test
    public void testPageRender_Index() {
        app.app()
                .module(JettyModule.class)
                .module(TapestryModule.class)
                .property("bq.tapestry.appPackage", "io.bootique.tapestry.testapp1")
                .start();

        assertHtml("/", "Index", "[xyz]");
    }

    @Test
    public void testPageRender_Page2() {
        app.app()
                .modules(JettyModule.class, TapestryModule.class)
                .property("bq.tapestry.appPackage", "io.bootique.tapestry.testapp1")
                .start();

        assertHtml("/page2", "I am wrapped", "[I am page2 body]");
    }

    @Test
    public void testPageRender_T5_Injection() {
        app.app()
                .modules(JettyModule.class, TapestryModule.class)
                .property("bq.tapestry.appPackage", "io.bootique.tapestry.testapp2")
                .property("bq.tapestry.name", "testapp2")
                .start();

        assertHtml("/", "Index", "[III]");
    }

    @Test
    public void testPageRender_T5_BQInjection() {
        app.app()
                .modules(JettyModule.class, TapestryModule.class, TestApp2BootiqueModule.class)
                .property("bq.tapestry.appPackage", "io.bootique.tapestry.testapp2")
                .property("bq.tapestry.name", "testapp2")
                .start();

        assertHtml("/bqservices", "BQServices", "{III}");
    }

    @Test
    public void testPageRender_T5_BQInjection_Annotations() {
        app.app("testarg", "testarg2")
                .modules(JettyModule.class, TapestryModule.class, TestApp2BootiqueModule.class)
                .property("bq.tapestry.appPackage", "io.bootique.tapestry.testapp2")
                .property("bq.tapestry.name", "testapp2")
                .start();

        assertHtml("/bqannotatedservices", "BQAnnotatedServices", "testarg_testarg2");
    }

    @Test
    public void testPageRender_LibComponent() {
        app.app()
                .module(JettyModule.class)
                .module(TapestryModule.class)
                .module(b ->
                        TapestryModule.contributeLibraries(b).addBinding("lib").toInstance("io.bootique.tapestry.testlib1"))
                .property("bq.tapestry.appPackage", "io.bootique.tapestry.testapp2")
                .start();

        assertHtml("/bqpagewithlibcomponent", "Index with Lib", "<b>__val__</b>");
    }

    @Test
    public void testIgnorePaths() {
        app.app()
                .module(JettyModule.class)
                .module(TapestryModule.class)
                .module(b -> {
                    TapestryModule.contributeIgnoredPaths(b).addBinding().toInstance("/ignored_by_tapestry/*");
                    JettyModule.contributeDefaultServlet(b);
                })
                .property("bq.tapestry.appPackage", "io.bootique.tapestry.testapp1")
                .property("bq.jetty.staticResourceBase", "classpath:docroot")
                .start();

        assertHtml("/", "Index", "[xyz]");
        assertHtml("/ignored_by_tapestry/static.html", "Static", "I am a static file");
    }

    @Test
    public void testPageRender_T5Modules() {
        app.app()
                .module(JettyModule.class)
                .module(TapestryModule.class)
                .module(b ->
                        TapestryModule.contributeModules(b).addBinding().toInstance(TestApp3Module.class))
                .property("bq.tapestry.appPackage", "io.bootique.tapestry.testapp3")
                .start();

        assertHtml("/page1", "Testapp3 Page1", ":DeferredServiceImpl:");
    }

    private void assertHtml(String uri, String expectedTitle, String expectedBody) {
        Response r = BASE_TARGET.path(uri).request(MediaType.TEXT_HTML).get();
        assertEquals(200, r.getStatus());

        String html = r.readEntity(String.class);

        // adding a small delay after reading the response. Otherwise container may start shutdown
        // when T5 request is still in progress, resulting in stack traces in the logs.
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
        }

        assertTrue(html.startsWith("<!DOCTYPE html><html"));
        assertTrue(html.endsWith("</html>"));

        assertTrue("Expected: " + expectedTitle, html.contains("<title>" + expectedTitle + "</title>"));
        assertTrue("Unexpected html: " + html, html.contains(expectedBody));
    }
}
