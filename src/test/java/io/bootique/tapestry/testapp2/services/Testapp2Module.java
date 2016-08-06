package io.bootique.tapestry.testapp2.services;

import org.apache.tapestry5.ioc.ServiceBinder;

public class Testapp2Module {

    public static void bind(ServiceBinder binder) {
        binder.bind(EchoService.class);
    }
}
