package com.nhl.bootique.tapestry.di;

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
