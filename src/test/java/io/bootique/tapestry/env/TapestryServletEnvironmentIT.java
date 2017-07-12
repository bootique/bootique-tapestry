package io.bootique.tapestry.env;

import io.bootique.BQRuntime;
import io.bootique.jetty.JettyModule;
import io.bootique.jetty.test.junit.JettyTestFactory;
import io.bootique.tapestry.TapestryModule;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.services.ComponentClassResolver;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertNotNull;

public class TapestryServletEnvironmentIT {

    @Rule
    public JettyTestFactory app = new JettyTestFactory();

    @Test
    public void testGetRegistry_BeforeStart() {
        BQRuntime runtime = app.app()
                .module(JettyModule.class)
                .module(TapestryModule.class)
                .property("bq.tapestry.appPackage", "no.such.package")
                // create runtime, but do not start .. no registry yet
                .createRuntime();

        Optional<Registry> registry = runtime.getInstance(TapestryEnvironment.class).getRegistry();
        Assert.assertFalse(registry.isPresent());
    }

    @Test
    public void testGetRegistry() {
        BQRuntime runtime = app.app()
                .module(JettyModule.class)
                .module(TapestryModule.class)
                .property("bq.tapestry.appPackage", "no.such.package")
                .start();

        Optional<Registry> registry = runtime.getInstance(TapestryEnvironment.class).getRegistry();
        Assert.assertTrue(registry.isPresent());
        assertNotNull(registry.get().getService(ComponentClassResolver.class));
    }
}
