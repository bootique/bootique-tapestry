package io.bootique.tapestry.env;

import org.apache.tapestry5.ioc.Registry;

import java.util.Optional;

/**
 * Provides access to Tapestry {@link Registry} in a context of a given web application. Allows Guice DI services to
 * lookup Tapestry-specific services that otherwise can't be injected. Will throw an exception if the Tapestry subsystem
 * is not yet started, and its servlet filters are not yet initialized.
 *
 * @since 0.7
 */
public interface TapestryEnvironment {

    Optional<Registry> getRegistry();
}
