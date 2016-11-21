package io.bootique.tapestry.testapp3.services;

public class DeferredServiceImpl implements DeferredService {

    @Override
    public String data() {
        return ":DeferredServiceImpl:";
    }
}
