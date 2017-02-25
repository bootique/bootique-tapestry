package io.bootique.tapestry;

import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import io.bootique.BQCoreModule;
import io.bootique.ConfigModule;
import io.bootique.config.ConfigurationFactory;
import io.bootique.jetty.JettyModule;
import io.bootique.jetty.MappedFilter;
import io.bootique.tapestry.annotation.IgnoredPaths;
import io.bootique.tapestry.annotation.Symbols;
import io.bootique.tapestry.annotation.TapestryModuleBinding;
import io.bootique.tapestry.di.GuiceTapestryModule;
import io.bootique.tapestry.filter.BQTapestryFilter;
import io.bootique.tapestry.filter.BQTapestryFilterFactory;
import org.apache.tapestry5.services.LibraryMapping;

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

    /**
     * Returns a MapBinder for Tapestry "symbols" that allow to reconfigure Tapestry runtime.
     *
     * @param binder DI binder to use for contributions.
     * @return A new MapBinder.
     * @see org.apache.tapestry5.SymbolConstants
     * @see org.apache.tapestry5.internal.InternalConstants
     * @since 0.4
     * @deprecated since 0.5 call {@link #extend(Binder)} and then call
     * {@link TapestryModuleExtender#setSymbol(String, String)}.
     */
    @Deprecated
    public static MapBinder<String, String> contributeSymbols(Binder binder) {
        return MapBinder.newMapBinder(binder, String.class, String.class, Symbols.class);
    }

    /**
     * Returns a MapBinder for Tapestry "symbols" that allow to reconfigure Tapestry runtime.
     *
     * @param binder DI binder to use for contributions.
     * @return A new Multibinder.
     * @deprecated since 0.5 call {@link #extend(Binder)} and then call
     * {@link TapestryModuleExtender#addLibraryMapping(LibraryMapping)}.
     */
    @Deprecated
    public static Multibinder<LibraryMapping> contributeLibraries(Binder binder) {
        return Multibinder.newSetBinder(binder, LibraryMapping.class);
    }

    /**
     * Returns a Multibinder for extra Tapestry modules. Custom DI modules allow to customize anything inside the Tapestry
     * stack. Note that Tapestry DI modules (unlike Bootique or Guice) do not inherit from anything.
     *
     * @param binder DI binder to use for contributions.
     * @return a Multibinder for Tapestry modules.
     * @since 0.4
     * @deprecated since 0.5 call {@link #extend(Binder)} and then call
     * {@link TapestryModuleExtender#addTapestryModule(Class)}.
     */
    @Deprecated
    public static Multibinder<Class<?>> contributeModules(Binder binder) {
        TypeLiteral<Class<?>> type = new TypeLiteral<Class<?>>() {
        };
        return Multibinder.newSetBinder(binder, Key.get(type, TapestryModuleBinding.class));
    }

    /**
     * @param binder DI binder to use for contributions.
     * @return Multibinder for URL paths that should not be processed by Tapestry.
     * @since 0.4
     * @deprecated since 0.5 call {@link #extend(Binder)} and then call
     * {@link TapestryModuleExtender#addIgnoredPath(String)}.
     */
    @Deprecated
    public static Multibinder<String> contributeIgnoredPaths(Binder binder) {
        return Multibinder.newSetBinder(binder, String.class, IgnoredPaths.class);
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
