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

package io.bootique.tapestry.v55.di;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import io.bootique.tapestry.v55.annotation.IgnoredPaths;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.services.LibraryMapping;

import java.util.Set;

/**
 * A Tapestry DI Module that provides integration with Guice DI.
 */
public class GuiceTapestryModule {

    public static void contributeIgnoredPathsFilter(
            @InjectService(InjectorModuleDef.INJECTOR_SERVICE_ID) Injector injector,
            Configuration<String> configuration) {

        TypeLiteral<Set<String>> ignoresType = new TypeLiteral<Set<String>>() {
        };
        Set<String> ignores = injector.getInstance(Key.get(ignoresType, IgnoredPaths.class));
        ignores.forEach(configuration::add);
    }

    public void contributeMasterObjectProvider(
            @InjectService(InjectorModuleDef.INJECTOR_SERVICE_ID) Injector injector,
            OrderedConfiguration configuration) {

        configuration.add("guiceProvider", new GuiceObjectProvider(injector), "after:Service,Alias,Autobuild");
    }

    public void contributeComponentClassResolver(
            @InjectService(InjectorModuleDef.INJECTOR_SERVICE_ID) Injector injector,
            Configuration<LibraryMapping> configuration) {

        // register Tapestry component libraries.


        TypeLiteral<Set<LibraryMapping>> libsType = new TypeLiteral<Set<LibraryMapping>>() {
        };
        injector.getInstance(Key.get(libsType)).forEach(configuration::add);
    }
}
