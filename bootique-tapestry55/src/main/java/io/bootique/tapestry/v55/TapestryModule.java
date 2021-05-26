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

package io.bootique.tapestry.v55;

import io.bootique.BQCoreModule;
import io.bootique.ConfigModule;
import io.bootique.config.ConfigurationFactory;
import io.bootique.di.Binder;
import io.bootique.di.Injector;
import io.bootique.di.Provides;
import io.bootique.di.TypeLiteral;
import io.bootique.jetty.JettyModule;
import io.bootique.jetty.MappedFilter;
import io.bootique.jetty.servlet.ServletEnvironment;
import io.bootique.tapestry.v55.annotation.Symbols;
import io.bootique.tapestry.v55.annotation.TapestryModuleBinding;
import io.bootique.tapestry.v55.di.BqTapestryModule;
import io.bootique.tapestry.v55.env.TapestryEnvironment;
import io.bootique.tapestry.v55.env.TapestryServletEnvironment;
import io.bootique.tapestry.v55.filter.BQTapestryFilter;
import io.bootique.tapestry.v55.filter.BQTapestryFilterFactory;

import javax.inject.Singleton;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class TapestryModule extends ConfigModule {

    /**
     * @param binder DI binder passed to the Module that invokes this method.
     * @return an instance of {@link TapestryModuleExtender} that can be used to load Tapestry custom extensions.
     */
    public static TapestryModuleExtender extend(Binder binder) {
        return new TapestryModuleExtender(binder);
    }

    @Override
    public void configure(Binder binder) {
        TapestryModule.extend(binder).initAllExtensions().addTapestryModule(BqTapestryModule.class);
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
            ConfigurationFactory configFactory,
            Injector injector,
            @Symbols Map<String, String> diSymbols,
            @TapestryModuleBinding Set<Class<?>> moduleTypes) {

        return config(BQTapestryFilterFactory.class, configFactory)
                .createTapestryFilter(injector, diSymbols, moduleTypes);
    }
}
