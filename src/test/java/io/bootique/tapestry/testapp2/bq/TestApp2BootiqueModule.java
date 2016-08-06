package io.bootique.tapestry.testapp2.bq;

import com.google.inject.Binder;
import com.google.inject.Module;

public class TestApp2BootiqueModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(BQEchoService.class);
    }
}
