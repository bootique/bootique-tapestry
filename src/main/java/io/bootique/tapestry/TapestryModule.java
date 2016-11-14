package io.bootique.tapestry;

import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import io.bootique.ConfigModule;
import io.bootique.config.ConfigurationFactory;
import io.bootique.jetty.JettyModule;
import io.bootique.jetty.MappedFilter;
import io.bootique.tapestry.annotation.IgnoredPaths;
import io.bootique.tapestry.annotation.LibraryMappings;
import io.bootique.tapestry.annotation.TapestryFilter;
import io.bootique.tapestry.filter.BQTapestryFilterFactory;

public class TapestryModule extends ConfigModule {

    public static MapBinder<String, String> contributeLibraries(Binder binder) {
        return MapBinder.newMapBinder(binder, String.class, String.class, LibraryMappings.class);
    }

    /**
     * @since 0.4
     * @param binder DI binder to use for contributions.
     * @return Multibinder for URL paths that should not be processed by Tapestry.
     */
    public static Multibinder<String> contributeIgnoredPaths(Binder binder) {
        return Multibinder.newSetBinder(binder, String.class, IgnoredPaths.class);
    }

    @Override
    public void configure(Binder binder) {
        JettyModule.contributeMappedFilters(binder).addBinding().to(Key.get(MappedFilter.class, TapestryFilter.class));

        contributeLibraries(binder);
        contributeIgnoredPaths(binder);
    }

    @Singleton
    @Provides
    @TapestryFilter
    MappedFilter createTapestryFilter(ConfigurationFactory configurationFactory, Injector injector) {
        return configurationFactory.config(BQTapestryFilterFactory.class, configPrefix).createTapestryFilter(injector);
    }
}
