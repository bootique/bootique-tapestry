package io.bootique.tapestry;

import io.bootique.tapestry.testapp3.services.DeferredService;
import io.bootique.tapestry.testapp3.services.DeferredServiceImpl;
import org.apache.tapestry5.ioc.ServiceBinder;

// intentionally using module location that is not recognized by Tapestry automatically
public class TestApp3Module {

    public static void bind(ServiceBinder binder) {
        binder.bind(DeferredService.class, DeferredServiceImpl.class);
    }
}
