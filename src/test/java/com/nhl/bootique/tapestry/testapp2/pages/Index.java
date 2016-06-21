package com.nhl.bootique.tapestry.testapp2.pages;

import com.nhl.bootique.tapestry.testapp2.services.EchoService;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Index {

    @Inject
    private EchoService echoService;

    public String getProperty() {
        return echoService.get("III");
    }
}
