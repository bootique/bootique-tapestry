package io.bootique.tapestry.testapp3.pages;

import io.bootique.tapestry.testapp3.services.DeferredService;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Page1 {

    @Inject
    private DeferredService service;

    public String getData() {
        return service.data();
    }

}
