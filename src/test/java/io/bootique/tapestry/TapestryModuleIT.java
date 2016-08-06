package io.bootique.tapestry;

import com.nhl.bootique.jetty.test.junit.JettyTestFactory;
import io.bootique.jetty.JettyModule;
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
        app.newRuntime().configurator(bootique -> bootique.module(JettyModule.class).module(TapestryModule.class)
        ).property("bq.tapestry.appPackage", "io.bootique.tapestry.testapp1").startServer();

        assertHtml("/", "Index", "[xyz]");
    }

    @Test
    public void testPageRender_Page2() {
        app.newRuntime().configurator(bootique -> bootique.module(JettyModule.class).module(TapestryModule.class)
        ).property("bq.tapestry.appPackage", "io.bootique.tapestry.testapp1").startServer();

        assertHtml("/page2", "I am wrapped", "[I am page2 body]");
    }

    @Test
    public void testPageRender_T5_Injection() {
        app.newRuntime().configurator(bootique -> bootique.module(JettyModule.class).module(TapestryModule.class))
                .property("bq.tapestry.appPackage", "io.bootique.tapestry.testapp2")
                .property("bq.tapestry.name", "testapp2")
                .startServer();

        assertHtml("/", "Index", "[III]");
    }

    @Test
    public void testPageRender_T5_BQInjection() {
        app.newRuntime().configurator(bootique -> bootique
                .module(JettyModule.class)
                .module(TapestryModule.class)
                .module(TestApp2BootiqueModule.class))
                .property("bq.tapestry.appPackage", "io.bootique.tapestry.testapp2")
                .property("bq.tapestry.name", "testapp2")
                .startServer();

        assertHtml("/bqservices", "BQServices", "{III}");
    }

    @Test
    public void testPageRender_T5_BQInjection_Annotations() {
        app.newRuntime().configurator(bootique -> bootique
                .module(JettyModule.class)
                .module(TapestryModule.class)
                .module(TestApp2BootiqueModule.class))
                .property("bq.tapestry.appPackage", "io.bootique.tapestry.testapp2")
                .property("bq.tapestry.name", "testapp2")
                .startServer("testarg", "testarg2");

        assertHtml("/bqannotatedservices", "BQAnnotatedServices", "--server_testarg_testarg2");
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

        assertTrue(html.contains("<title>" + expectedTitle + "</title>"));
        assertTrue(html.contains(expectedBody));
    }
}
