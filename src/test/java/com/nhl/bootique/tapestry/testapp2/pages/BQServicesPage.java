package com.nhl.bootique.tapestry.testapp2.pages;

import com.nhl.bootique.tapestry.testapp2.bq.BQEchoService;
import org.apache.tapestry5.ioc.annotations.Inject;

public class BQServicesPage {

    @Inject
    private BQEchoService echoService;

    public String getProperty() {
        return echoService.get("III");
    }
}
