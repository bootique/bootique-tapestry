package io.bootique.tapestry.di;

import io.bootique.BQRuntime;
import io.bootique.meta.application.ApplicationMetadata;
import io.bootique.tapestry.TapestryModule;
import io.bootique.tapestry.TapestryModuleProvider;
import io.bootique.tapestry.env.TapestryEnvironment;
import io.bootique.test.junit.BQTestFactory;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class GuiceObjectProviderIT {

    @Rule
    public BQTestFactory app = new BQTestFactory();

    @Test
    public void testInjectInT5_GuiceSingleton() {
        BQRuntime runtime = app.app("-s")
                .module(new TapestryModuleProvider())
                .module(b -> TapestryModule.extend(b).addTapestryModule(T1Module.class))
                .property("bq.tapestry.appPackage", "no.such.package")
                .createRuntime();

        // Tapestry is initialized lazily, so get a hold of T5-managed services, we need to run jetty.
        runtime.run();

        T1 t1 = runtime.getInstance(TapestryEnvironment.class).getRegistry().get().getService(T1.class);
        assertNotNull(t1.getInjectable());
        assertNotNull(t1.getInjectable().getName());
    }

    public static class T1Module {
        public static void bind(ServiceBinder binder) {
            binder.bind(T1.class, T1.class);
        }
    }

    public static class T1 {

        @Inject
        private ApplicationMetadata injectable;

        public ApplicationMetadata getInjectable() {
            return injectable;
        }
    }
}
