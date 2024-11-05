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

package io.bootique.tapestry.v58.filter;

import org.apache.tapestry5.TapestryFilter;
import org.apache.tapestry5.http.AsyncRequestHandlerResponse;
import org.apache.tapestry5.http.TapestryHttpSymbolConstants;
import org.apache.tapestry5.http.internal.AsyncRequestService;
import org.apache.tapestry5.http.internal.SingleKeySymbolProvider;
import org.apache.tapestry5.http.internal.TapestryAppInitializer;
import org.apache.tapestry5.http.internal.util.DelegatingSymbolProvider;
import org.apache.tapestry5.http.services.HttpServletRequestHandler;
import org.apache.tapestry5.http.services.ServletApplicationInitializer;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.def.ModuleDef;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
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
    private AsyncRequestService asyncRequestService;

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
                new SingleKeySymbolProvider(TapestryHttpSymbolConstants.CONTEXT_PATH, context.getContextPath())
        );
    }

    @Override
    public void init(FilterConfig filterConfig) {

        this.context = filterConfig.getServletContext();

        SymbolProvider contextualSymbolProvider = createSymbolProvider(context);

        String executionMode = contextualSymbolProvider.valueForSymbol(TapestryHttpSymbolConstants.EXECUTION_MODE);
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
        this.asyncRequestService = registry.getService("AsyncRequestService", AsyncRequestService.class);

        appInitializer.announceStartup();
        registry.cleanupThread();
    }

    protected void runFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            boolean handled = handler.service((HttpServletRequest) request, (HttpServletResponse) response);
            if (!handled) {
                chain.doFilter(request, response);
            }
        } finally {
            registry.cleanupThread();
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        AsyncRequestHandlerResponse handlerResponse = asyncRequestService.handle((HttpServletRequest) request, (HttpServletResponse) response);

        if (handlerResponse.isAsync()) {
            AsyncContext asyncContext;
            if (handlerResponse.isHasRequestAndResponse()) {
                asyncContext = request.startAsync(handlerResponse.getRequest(), handlerResponse.getResponse());
            } else {
                asyncContext = request.startAsync();
            }
            if (handlerResponse.getListener() != null) {
                asyncContext.addListener(handlerResponse.getListener());
            }
            if (handlerResponse.getTimeout() > 0) {
                asyncContext.setTimeout(handlerResponse.getTimeout());
            }

            handlerResponse.getExecutor().execute(
                    new ExceptionCatchingRunnable(() -> {
                        runFilter(request, response, chain);
                        asyncContext.complete();
                    }));
        } else {
            runFilter(request, response, chain);
        }
    }

    private interface ExceptionRunnable {
        void run() throws Exception;
    }

    private final class ExceptionCatchingRunnable implements Runnable {
        private final ExceptionRunnable runnable;

        public ExceptionCatchingRunnable(ExceptionRunnable runnable) {
            this.runnable = runnable;
        }

        public void run() {
            try {
                runnable.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
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
