/**
 *    Licensed to the ObjectStyle LLC under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ObjectStyle LLC licenses
 *  this file to you under the Apache License, Version 2.0 (the
 *  “License”); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  “AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.bootique.tapestry.filter;

import com.google.inject.Injector;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.jetty.MappedFilter;
import io.bootique.tapestry.di.InjectorModuleDef;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.ioc.def.ModuleDef;
import org.apache.tapestry5.ioc.internal.services.MapSymbolProvider;
import org.apache.tapestry5.ioc.services.SymbolProvider;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@BQConfig
public class BQTapestryFilterFactory {

    protected String urlPattern;
    protected int filterOrder;

    protected String name;

    // properties from http://tapestry.apache.org/configuration.html

    protected Boolean productionMode;
    protected String executionModes;
    protected String supportedLocales;
    protected String charset;
    protected String appPackage;

    public BQTapestryFilterFactory() {
        this.urlPattern = "/*";
        this.filterOrder = 1;
        this.name = "tapestry";

        this.productionMode = true;
        this.executionModes = "production";
        this.charset = "UTF-8";
    }

    @BQConfigProperty
    public void setProductionMode(Boolean productionMode) {
        this.productionMode = productionMode;
    }

    @BQConfigProperty
    public void setExecutionModes(String executionModes) {
        this.executionModes = executionModes;
    }

    @BQConfigProperty
    public void setSupportedLocales(String supportedLocales) {
        this.supportedLocales = supportedLocales;
    }

    @BQConfigProperty
    public void setCharset(String charset) {
        this.charset = charset;
    }

    @BQConfigProperty
    public void setName(String name) {
        this.name = name;
    }

    @BQConfigProperty
    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    @BQConfigProperty
    public void setFilterOrder(int filterOrder) {
        this.filterOrder = filterOrder;
    }

    @BQConfigProperty
    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public MappedFilter<BQTapestryFilter> createTapestryFilter(Injector injector, Map<String, String> diSymbols, Set<Class<?>> customModules) {
        SymbolProvider symbolProvider = createSymbolProvider(diSymbols);
        BQTapestryFilter filter = new BQTapestryFilter(name, symbolProvider, toModuleArray(customModules), extraModuleDefs(injector));
        return new MappedFilter(filter, Collections.singleton(urlPattern), name, filterOrder);
    }

    protected Class[] toModuleArray(Set<Class<?>> modules) {
        return modules.toArray(new Class[modules.size()]);
    }

    protected ModuleDef[] extraModuleDefs(Injector injector) {
        ModuleDef guiceBridge = new InjectorModuleDef(injector);

        return new ModuleDef[]{guiceBridge};
    }

    protected SymbolProvider createSymbolProvider(Map<String, String> diSymbols) {

        Map<String, String> params = new HashMap<>(diSymbols);

        // override DI symbols if set explicitly in the factory
        if (productionMode != null) {
            params.put(SymbolConstants.PRODUCTION_MODE, Boolean.toString(productionMode));
        }

        if (executionModes != null) {
            params.put(SymbolConstants.EXECUTION_MODE, executionModes);
        }

        if (charset != null) {
            params.put(SymbolConstants.CHARSET, charset);
        }

        if (appPackage != null) {
            params.put(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM, appPackage);
        } else {
            // sanity check
            if (!params.containsKey(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM)) {
                throw new IllegalStateException("Tapestry app package is not defined. Use 'tapestry.appPackage' config " +
                        "or inject 'tapestry.app-package' symbol with this value.");
            }
        }

        if (supportedLocales != null) {
            params.put(SymbolConstants.SUPPORTED_LOCALES, supportedLocales);
        }

        // provide default values for symbols if not defined in DI
        if (!params.containsKey(SymbolConstants.GZIP_COMPRESSION_ENABLED)) {
            // compression should be configured at the Jetty level
            params.put(SymbolConstants.GZIP_COMPRESSION_ENABLED, "false");
        }

        return new MapSymbolProvider(params);
    }
}
