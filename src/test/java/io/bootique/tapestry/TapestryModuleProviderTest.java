package io.bootique.tapestry;

import com.nhl.bootique.test.junit.BQModuleProviderChecker;
import io.bootique.tapestry.TapestryModuleProvider;
import org.junit.Test;

public class TapestryModuleProviderTest {

    @Test
    public void testPresentInJar() {
        BQModuleProviderChecker.testPresentInJar(TapestryModuleProvider.class);
    }

}
