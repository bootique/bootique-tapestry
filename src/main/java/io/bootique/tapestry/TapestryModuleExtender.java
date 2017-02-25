package io.bootique.tapestry;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import io.bootique.ModuleExtender;
import io.bootique.tapestry.annotation.IgnoredPaths;
import io.bootique.tapestry.annotation.Symbols;
import io.bootique.tapestry.annotation.TapestryModuleBinding;
import org.apache.tapestry5.services.LibraryMapping;

/**
 * @since 0.5
 */
public class TapestryModuleExtender extends ModuleExtender<TapestryModuleExtender> {

    private MapBinder<String, String> symbols;
    private Multibinder<LibraryMapping> libraries;
    private Multibinder<Class<?>> modules;
    private Multibinder<String> ignoredPaths;

    public TapestryModuleExtender(Binder binder) {
        super(binder);
    }

    @Override
    public TapestryModuleExtender initAllExtensions() {
        contributeModules();
        contributeIgnoredPaths();
        contributeLibraries();
        contributeSymbols();
        return this;
    }

    /**
     * @param name  symbol name
     * @param value symbol value
     * @return this extender instance
     * @see org.apache.tapestry5.SymbolConstants
     * @see org.apache.tapestry5.internal.InternalConstants
     */
    public TapestryModuleExtender setSymbol(String name, String value) {
        contributeSymbols().addBinding(name).toInstance(value);
        return this;
    }

    public TapestryModuleExtender addLibraryMapping(LibraryMapping libraryMapping) {
        contributeLibraries().addBinding().toInstance(libraryMapping);
        return this;
    }

    public TapestryModuleExtender addTapestryModule(Class<?> moduleType) {
        contributeModules().addBinding().toInstance(moduleType);
        return this;
    }

    public TapestryModuleExtender addIgnoredPath(String ignoredPath) {
        contributeIgnoredPaths().addBinding().toInstance(ignoredPath);
        return this;
    }

    protected MapBinder<String, String> contributeSymbols() {
        return symbols != null ? symbols : (symbols = newMap(String.class, String.class, Symbols.class));
    }

    protected Multibinder<LibraryMapping> contributeLibraries() {
        return libraries != null ? libraries : (libraries = newSet(LibraryMapping.class));
    }

    protected Multibinder<Class<?>> contributeModules() {

        if (modules == null) {

            TypeLiteral<Class<?>> type = new TypeLiteral<Class<?>>() {
            };
            modules = newSet(Key.get(type, TapestryModuleBinding.class));
        }

        return modules;
    }

    protected Multibinder<String> contributeIgnoredPaths() {
        return ignoredPaths != null ? ignoredPaths : (ignoredPaths = newSet(String.class, IgnoredPaths.class));
    }
}
