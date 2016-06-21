package com.nhl.bootique.tapestry.testapp2.services;

public class EchoService {

    public String get(String in) {
        return "[" + in + "]";
    }
}
