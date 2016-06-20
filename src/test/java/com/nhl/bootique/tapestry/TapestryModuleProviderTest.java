package com.nhl.bootique.tapestry;

import com.nhl.bootique.test.junit.BQModuleProviderChecker;
import org.junit.Test;

public class TapestryModuleProviderTest {

    @Test
    public void testPresentInJar() {
        BQModuleProviderChecker.testPresentInJar(TapestryModuleProvider.class);
    }

}
