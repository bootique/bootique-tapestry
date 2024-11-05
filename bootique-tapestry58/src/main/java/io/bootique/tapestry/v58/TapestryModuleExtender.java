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

package io.bootique.tapestry.v58;

import io.bootique.ModuleExtender;
import io.bootique.di.Binder;
import io.bootique.di.Key;
import io.bootique.di.MapBuilder;
import io.bootique.di.SetBuilder;
import io.bootique.di.TypeLiteral;
import io.bootique.tapestry.v58.annotation.IgnoredPaths;
import io.bootique.tapestry.v58.annotation.Symbols;
import io.bootique.tapestry.v58.annotation.TapestryModuleBinding;
import org.apache.tapestry5.services.LibraryMapping;

/**
 * @deprecated in favor of 5.9 Jakarta (or later) modules
 */
@Deprecated(since = "3.0", forRemoval = true)
public class TapestryModuleExtender extends ModuleExtender<TapestryModuleExtender> {

    private MapBuilder<String, String> symbols;
    private SetBuilder<LibraryMapping> libraries;
    private SetBuilder<Class<?>> modules;
    private SetBuilder<String> ignoredPaths;

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
        contributeSymbols().putInstance(name, value);
        return this;
    }

    public TapestryModuleExtender addLibraryMapping(LibraryMapping libraryMapping) {
        contributeLibraries().addInstance(libraryMapping);
        return this;
    }

    public TapestryModuleExtender addTapestryModule(Class<?> moduleType) {
        contributeModules().addInstance(moduleType);
        return this;
    }

    public TapestryModuleExtender addIgnoredPath(String ignoredPath) {
        contributeIgnoredPaths().addInstance(ignoredPath);
        return this;
    }

    protected MapBuilder<String, String> contributeSymbols() {
        return symbols != null ? symbols : (symbols = newMap(String.class, String.class, Symbols.class));
    }

    protected SetBuilder<LibraryMapping> contributeLibraries() {
        return libraries != null ? libraries : (libraries = newSet(LibraryMapping.class));
    }

    protected SetBuilder<Class<?>> contributeModules() {

        if (modules == null) {

            TypeLiteral<Class<?>> type = new TypeLiteral<Class<?>>() {
            };
            modules = newSet(Key.get(type, TapestryModuleBinding.class));
        }

        return modules;
    }

    protected SetBuilder<String> contributeIgnoredPaths() {
        return ignoredPaths != null ? ignoredPaths : (ignoredPaths = newSet(String.class, IgnoredPaths.class));
    }
}
