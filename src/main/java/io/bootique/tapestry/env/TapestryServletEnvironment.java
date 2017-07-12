package io.bootique.tapestry.env;

import io.bootique.jetty.servlet.ServletEnvironment;
import org.apache.tapestry5.TapestryFilter;
import org.apache.tapestry5.ioc.Registry;

import java.util.Optional;

/**
 * @since 0.7
 */
public class TapestryServletEnvironment implements TapestryEnvironment {

    private ServletEnvironment servletEnvironment;

    public TapestryServletEnvironment(ServletEnvironment servletEnvironment) {
        this.servletEnvironment = servletEnvironment;
    }

    @Override
    public Optional<Registry> getRegistry() {
        return servletEnvironment.context()
                .map(c -> (Registry) c.getAttribute(TapestryFilter.REGISTRY_CONTEXT_NAME));
    }
}
