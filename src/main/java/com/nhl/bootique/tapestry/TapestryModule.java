package com.nhl.bootique.tapestry;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.nhl.bootique.ConfigModule;
import com.nhl.bootique.config.ConfigurationFactory;
import com.nhl.bootique.env.Environment;
import com.nhl.bootique.jetty.JettyModule;
import com.nhl.bootique.jetty.MappedFilter;
import com.nhl.bootique.jetty.MappedServlet;
import com.nhl.bootique.tapestry.boot.BQTapestryFilterFactory;

public class TapestryModule extends ConfigModule {

    @Override
    public void configure(Binder binder) {
        JettyModule.contributeMappedServlets(binder).addBinding().to(Key.get(MappedServlet.class, TapestryFilter.class));
    }

    @Singleton
    @Provides
    @TapestryFilter
    public MappedFilter createTapestryFilter(ConfigurationFactory configurationFactory, Environment environment) {
        return configurationFactory.config(BQTapestryFilterFactory.class, configPrefix).createTapestryFilter(environment);
    }
}
