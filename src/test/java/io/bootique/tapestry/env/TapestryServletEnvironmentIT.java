package io.bootique.tapestry.env;

import io.bootique.BQRuntime;
import io.bootique.jetty.JettyModule;
import io.bootique.tapestry.TapestryModule;
import io.bootique.test.junit.BQTestFactory;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.services.ComponentClassResolver;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertNotNull;

public class TapestryServletEnvironmentIT {

    @Rule
    public BQTestFactory app = new BQTestFactory();

    @Test
    public void testGetRegistry_BeforeStart() {
        BQRuntime runtime = app.app()
                .module(JettyModule.class)
                .module(TapestryModule.class)
                .property("bq.tapestry.appPackage", "no.such.package")
                .createRuntime();

        // create runtime, but do not run .. as a result there should be no T5 registry

        Optional<Registry> registry = runtime.getInstance(TapestryEnvironment.class).getRegistry();
        Assert.assertFalse(registry.isPresent());
    }

    @Test
    public void testGetRegistry() {
        BQRuntime runtime = app.app("-s")
                .module(JettyModule.class)
                .module(TapestryModule.class)
                .property("bq.tapestry.appPackage", "no.such.package")
                .createRuntime();

        runtime.run();

        Optional<Registry> registry = runtime.getInstance(TapestryEnvironment.class).getRegistry();
        Assert.assertTrue(registry.isPresent());
        assertNotNull(registry.get().getService(ComponentClassResolver.class));
    }
}
