/*
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
