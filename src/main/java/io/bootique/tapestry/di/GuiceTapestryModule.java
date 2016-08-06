package io.bootique.tapestry.di;

import com.google.inject.Injector;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.InjectService;

/**
 * A Tapestry DI Module that provides integration with Guice DI.
 */
public class GuiceTapestryModule {

    public void contributeMasterObjectProvider(
            @InjectService(InjectorModuleDef.INJECTOR_SERVICE_ID) Injector injector,
            OrderedConfiguration configuration) {

        configuration.add("guiceProvider", new GuiceObjectProvider(injector), "after:Service,Alias,Autobuild");
    }
}
