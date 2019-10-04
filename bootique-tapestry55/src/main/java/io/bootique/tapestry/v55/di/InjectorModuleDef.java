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
import org.apache.tapestry5.ioc.def.ContributionDef;
import org.apache.tapestry5.ioc.def.DecoratorDef;
import org.apache.tapestry5.ioc.def.ModuleDef;
import org.apache.tapestry5.ioc.def.ServiceDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

public class InjectorModuleDef implements ModuleDef {

    private static final Logger LOGGER = LoggerFactory.getLogger(InjectorModuleDef.class);

    static final String INJECTOR_SERVICE_ID = "BootiqueGuiceInjector";


    private ServiceDef injectorServiceDef;

    public InjectorModuleDef(Injector injector) {
        this.injectorServiceDef = new InjectorServiceDef(injector, INJECTOR_SERVICE_ID);
    }

    @Override
    public Set<String> getServiceIds() {
        return Collections.singleton(INJECTOR_SERVICE_ID);
    }

    @Override
    public ServiceDef getServiceDef(String serviceId) {
        return INJECTOR_SERVICE_ID.equals(serviceId) ? injectorServiceDef : null;
    }

    @Override
    public Set<DecoratorDef> getDecoratorDefs() {
        return Collections.emptySet();
    }

    @Override
    public Set<ContributionDef> getContributionDefs() {
        return Collections.emptySet();
    }

    @Override
    public Class<?> getBuilderClass() {
        return null;
    }

    @Override
    public String getLoggerName() {
        return InjectorServiceDef.class.getName();
    }
}
