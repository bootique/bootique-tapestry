package io.bootique.tapestry;

import com.google.inject.Module;
import io.bootique.BQModuleProvider;

public class TapestryModuleProvider implements BQModuleProvider {

    @Override
    public Module module() {
        return new TapestryModule();
    }
}
