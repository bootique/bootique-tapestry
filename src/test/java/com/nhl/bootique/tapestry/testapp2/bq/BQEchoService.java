package com.nhl.bootique.tapestry.testapp2.bq;

/**
 * Created by andrus on 6/21/16.
 */
public class BQEchoService {

    public String get(String in) {
        return "{" + in + "}";
    }
}
