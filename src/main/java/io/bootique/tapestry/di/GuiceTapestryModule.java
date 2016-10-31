package io.bootique.tapestry.di;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import io.bootique.tapestry.annotation.LibraryMappings;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.services.LibraryMapping;

import java.util.Map;

/**
 * A Tapestry DI Module that provides integration with Guice DI.
 */
public class GuiceTapestryModule {

    public void contributeMasterObjectProvider(
            @InjectService(InjectorModuleDef.INJECTOR_SERVICE_ID) Injector injector,
            OrderedConfiguration configuration) {

        configuration.add("guiceProvider", new GuiceObjectProvider(injector), "after:Service,Alias,Autobuild");
    }

    public void contributeComponentClassResolver(
            @InjectService(InjectorModuleDef.INJECTOR_SERVICE_ID) Injector injector,
            Configuration<LibraryMapping> configuration) {

        // register Tapestry component libraries.


        TypeLiteral<Map<String, String>> libsType = new TypeLiteral<Map<String, String>>() {};
        Map<String, String> libsMap = injector.getInstance(Key.get(libsType, LibraryMappings.class));
        libsMap.forEach((prefix, pack) -> configuration.add(new LibraryMapping(prefix, pack)));
    }
}
