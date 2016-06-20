package com.nhl.bootique.tapestry;

import com.google.inject.Module;
import com.nhl.bootique.BQModuleProvider;

public class TapestryModuleProvider implements BQModuleProvider {

    @Override
    public Module module() {
        return new TapestryModule();
    }
}
