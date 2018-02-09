package io.bootique.tapestry;

import com.google.inject.Module;
import io.bootique.BQModule;
import io.bootique.BQModuleProvider;
import io.bootique.jetty.JettyModuleProvider;
import io.bootique.tapestry.filter.BQTapestryFilterFactory;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static java.util.Collections.singletonList;

public class TapestryModuleProvider implements BQModuleProvider {

    @Override
    public Module module() {
        return new TapestryModule();
    }

    @Override
    public Map<String, Type> configs() {
        // TODO: config prefix is hardcoded. Refactor away from ConfigModule, and make provider
        // generate config prefix, reusing it in metadata...
        return Collections.singletonMap("tapestry", BQTapestryFilterFactory.class);
    }

    @Override
    public BQModule.Builder moduleBuilder() {
        return BQModuleProvider.super
                .moduleBuilder()
                .description("Provides integration with Apache Tapestry.");
    }

    @Override
    public Collection<BQModuleProvider> dependencies() {
        return singletonList(
               new JettyModuleProvider()
        );
    }
}
