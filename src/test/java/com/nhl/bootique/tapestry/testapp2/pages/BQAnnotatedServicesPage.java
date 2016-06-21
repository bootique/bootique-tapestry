package com.nhl.bootique.tapestry.testapp2.pages;

import com.nhl.bootique.annotation.Args;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Arrays;
import java.util.stream.Collectors;

public class BQAnnotatedServicesPage {

    @Args
    @Inject
    private String[] args;

    public String getProperty() {
        return Arrays.asList(args).stream().collect(Collectors.joining("_"));
    }

}
