package io.bootique.tapestry;

import io.bootique.test.junit.BQModuleProviderChecker;
import org.junit.Test;

public class TapestryModuleProviderTest {

    @Test
    public void testPresentInJar() {
        BQModuleProviderChecker.testPresentInJar(TapestryModuleProvider.class);
    }

}
