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
import io.bootique.tapestry.annotation.Symbols;
import io.bootique.tapestry.annotation.TapestryFilter;
import io.bootique.tapestry.filter.BQTapestryFilterFactory;

import java.util.Map;

public class TapestryModule extends ConfigModule {

    /**
     * Returns a MapBinder for Tapestry "symbols" that allow to reconfigure Tapestry runtime.
     *
     * @param binder DI binder to use for contributions.
     * @return A new MapBinder.
     * @see org.apache.tapestry5.SymbolConstants
     * @since 0.4
     */
    public static MapBinder<String, String> contributeSymbols(Binder binder) {
        return MapBinder.newMapBinder(binder, String.class, String.class, Symbols.class);
    }

    public static MapBinder<String, String> contributeLibraries(Binder binder) {
        return MapBinder.newMapBinder(binder, String.class, String.class, LibraryMappings.class);
    }

    /**
     * @param binder DI binder to use for contributions.
     * @return Multibinder for URL paths that should not be processed by Tapestry.
     * @since 0.4
     */
    public static Multibinder<String> contributeIgnoredPaths(Binder binder) {
        return Multibinder.newSetBinder(binder, String.class, IgnoredPaths.class);
    }

    @Override
    public void configure(Binder binder) {
        JettyModule.contributeMappedFilters(binder).addBinding().to(Key.get(MappedFilter.class, TapestryFilter.class));

        contributeLibraries(binder);
        contributeIgnoredPaths(binder);
        contributeSymbols(binder);
    }

    @Singleton
    @Provides
    @TapestryFilter
    MappedFilter createTapestryFilter(
            ConfigurationFactory configurationFactory,
            Injector injector,
            @Symbols Map<String, String> diSymbols) {

        return configurationFactory
                .config(BQTapestryFilterFactory.class, configPrefix)
                .createTapestryFilter(injector, diSymbols);
    }
}
