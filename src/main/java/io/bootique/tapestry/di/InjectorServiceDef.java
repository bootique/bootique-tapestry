package io.bootique.tapestry.di;


import com.google.inject.Injector;
import org.apache.tapestry5.ioc.ObjectCreator;
import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.ServiceBuilderResources;
import org.apache.tapestry5.ioc.def.ServiceDef;

import java.util.Collections;
import java.util.Set;

public class InjectorServiceDef implements ServiceDef {

    private Injector injector;
    private String serviceId;

    public InjectorServiceDef(Injector injector, String serviceId) {
        this.injector = injector;
        this.serviceId = serviceId;
    }

    @Override
    public ObjectCreator createServiceCreator(ServiceBuilderResources resources) {
        return () -> injector;
    }

    @Override
    public String getServiceId() {
        return serviceId;
    }

    @Override
    public Set<Class> getMarkers() {
        return Collections.emptySet();
    }

    @Override
    public Class getServiceInterface() {
        return Injector.class;
    }

    @Override
    public String getServiceScope() {
        return ScopeConstants.DEFAULT;
    }

    @Override
    public boolean isEagerLoad() {
        return false;
    }
}
