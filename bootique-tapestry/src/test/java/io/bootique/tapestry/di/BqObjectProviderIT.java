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

package io.bootique.tapestry.di;

import io.bootique.BQRuntime;
import io.bootique.junit5.BQTest;
import io.bootique.junit5.BQTestFactory;
import io.bootique.junit5.BQTestTool;
import io.bootique.meta.application.ApplicationMetadata;
import io.bootique.tapestry.TapestryModule;
import io.bootique.tapestry.TapestryModuleProvider;
import io.bootique.tapestry.env.TapestryEnvironment;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@BQTest
public class BqObjectProviderIT {

    @BQTestTool
    final BQTestFactory app = new BQTestFactory();

    @Test
    public void testInjectInT5_BqSingleton() {
        BQRuntime runtime = app.app("-s")
                .moduleProvider(new TapestryModuleProvider())
                .module(b -> TapestryModule.extend(b).addTapestryModule(T1Module.class))
                .property("bq.tapestry.appPackage", "no.such.package")
                .createRuntime();

        // Tapestry is initialized lazily, so get a hold of T5-managed services, we need to run jetty.
        runtime.run();

        T1 t1 = runtime.getInstance(TapestryEnvironment.class).getRegistry().get().getService(T1.class);
        assertNotNull(t1.getInjectable());
        assertNotNull(t1.getInjectable().getName());
    }

    public static class T1Module {
        public static void bind(ServiceBinder binder) {
            binder.bind(T1.class, T1.class);
        }
    }

    public static class T1 {

        @Inject
        private ApplicationMetadata injectable;

        public ApplicationMetadata getInjectable() {
            return injectable;
        }
    }
}
