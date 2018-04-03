package io.bootique.tapestry;

import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import io.bootique.BQCoreModule;
import io.bootique.ConfigModule;
import io.bootique.config.ConfigurationFactory;
import io.bootique.jetty.JettyModule;
import io.bootique.jetty.MappedFilter;
import io.bootique.jetty.servlet.ServletEnvironment;
import io.bootique.tapestry.annotation.Symbols;
import io.bootique.tapestry.annotation.TapestryModuleBinding;
import io.bootique.tapestry.di.GuiceTapestryModule;
import io.bootique.tapestry.env.TapestryEnvironment;
import io.bootique.tapestry.env.TapestryServletEnvironment;
import io.bootique.tapestry.filter.BQTapestryFilter;
import io.bootique.tapestry.filter.BQTapestryFilterFactory;

import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class TapestryModule extends ConfigModule {

    /**
     * @param binder DI binder passed to the Module that invokes this method.
     * @return an instance of {@link TapestryModuleExtender} that can be used to load Tapestry custom extensions.
     * @since 0.5
     */
    public static TapestryModuleExtender extend(Binder binder) {
        return new TapestryModuleExtender(binder);
    }

    @Override
    public void configure(Binder binder) {
        TapestryModule.extend(binder).initAllExtensions().addTapestryModule(GuiceTapestryModule.class);
        TypeLiteral<MappedFilter<BQTapestryFilter>> tf = new TypeLiteral<MappedFilter<BQTapestryFilter>>() {
        };
        JettyModule.extend(binder).addMappedFilter(tf);

        // decrease default verbosity...
        BQCoreModule.extend(binder)
                .setLogLevel("org.apache.tapestry5.modules.TapestryModule.ComponentClassResolver", Level.WARNING)
                .setLogLevel("io.bootique.tapestry.filter.BQTapestryFilter", Level.WARNING);
    }

    @Singleton
    @Provides
    TapestryEnvironment provideTapestryEnvironment(ServletEnvironment servletEnvironment) {
        return new TapestryServletEnvironment(servletEnvironment);
    }

    @Singleton
    @Provides
    MappedFilter<BQTapestryFilter> createTapestryFilter(
            ConfigurationFactory configurationFactory,
            Injector injector,
            @Symbols Map<String, String> diSymbols,
            @TapestryModuleBinding Set<Class<?>> moduleTypes) {

        return configurationFactory
                .config(BQTapestryFilterFactory.class, configPrefix)
                .createTapestryFilter(injector, diSymbols, moduleTypes);
    }
}
