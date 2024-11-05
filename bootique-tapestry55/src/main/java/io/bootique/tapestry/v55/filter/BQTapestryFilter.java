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

package io.bootique.tapestry.v55.filter;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.TapestryFilter;
import org.apache.tapestry5.internal.SingleKeySymbolProvider;
import org.apache.tapestry5.internal.TapestryAppInitializer;
import org.apache.tapestry5.internal.util.DelegatingSymbolProvider;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.def.ModuleDef;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.services.HttpServletRequestHandler;
import org.apache.tapestry5.services.ServletApplicationInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A servlet filter that starts Tapestry environment. Based on Tapestry {@link org.apache.tapestry5.TapestryFilter}.
 *
 * @deprecated in favor of 5.9 Jakarta (or later) modules
 */
@Deprecated(since = "3.0", forRemoval = true)
// we couldn't subclass TapestryFilter as the interesting parts (init() method) are final and too coarse grained.
// also our own filter allows us to switch all the configuration to Bootique style.
public class BQTapestryFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BQTapestryFilter.class);

    private SymbolProvider baseSymbolProvider;
    private String name;
    private Class[] extraModules;
    private ModuleDef[] extraModuleDefs;

    private ServletContext context;
    private Registry registry;
    private HttpServletRequestHandler handler;

    public BQTapestryFilter(String name, SymbolProvider baseSymbolProvider, Class[] extraModules, ModuleDef[] extraModuleDefs) {
        this.name = name;
        this.baseSymbolProvider = baseSymbolProvider;
        this.extraModules = extraModules;
        this.extraModuleDefs = extraModuleDefs;
    }

    protected SymbolProvider createSymbolProvider(ServletContext context) {
        // merge upstream provider with defaults from ServletContext
        return new DelegatingSymbolProvider(
                baseSymbolProvider,
                new SingleKeySymbolProvider(SymbolConstants.CONTEXT_PATH, context.getContextPath())
        );
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        this.context = filterConfig.getServletContext();

        SymbolProvider contextualSymbolProvider = createSymbolProvider(context);

        String executionMode = contextualSymbolProvider.valueForSymbol(SymbolConstants.EXECUTION_MODE);
        TapestryAppInitializer appInitializer = new TapestryAppInitializer(LOGGER, contextualSymbolProvider, name, executionMode);

        appInitializer.addModules(extraModules);
        appInitializer.addModules(extraModuleDefs);

        this.registry = appInitializer.createRegistry();
        this.context.setAttribute(TapestryFilter.REGISTRY_CONTEXT_NAME, registry);

        ServletApplicationInitializer ai = registry.getService("ServletApplicationInitializer",
                ServletApplicationInitializer.class);

        ai.initializeApplication(context);

        this.registry.performRegistryStartup();

        this.handler = registry.getService("HttpServletRequestHandler", HttpServletRequestHandler.class);

        appInitializer.announceStartup();
        registry.cleanupThread();
    }

    @Override
    public final void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            boolean handled = handler.service((HttpServletRequest) request,
                    (HttpServletResponse) response);

            if (!handled) {
                chain.doFilter(request, response);
            }
        } finally {
            registry.cleanupThread();
        }
    }

    @Override
    public final void destroy() {

        // if startup has failed, registry will be null
        if (registry != null) {
            registry.shutdown();
        }

        context.removeAttribute(TapestryFilter.REGISTRY_CONTEXT_NAME);

        registry = null;
        context = null;
        handler = null;
    }
}
