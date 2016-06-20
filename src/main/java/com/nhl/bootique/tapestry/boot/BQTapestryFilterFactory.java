package com.nhl.bootique.tapestry.boot;

import com.nhl.bootique.env.Environment;
import com.nhl.bootique.jetty.MappedFilter;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.SingleKeySymbolProvider;
import org.apache.tapestry5.internal.util.DelegatingSymbolProvider;
import org.apache.tapestry5.ioc.internal.services.MapSymbolProvider;
import org.apache.tapestry5.ioc.services.SymbolProvider;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BQTapestryFilterFactory {

    protected String urlPattern;
    protected int filterOrder;
    protected boolean productionMode;
    protected String name;

    public BQTapestryFilterFactory() {
        this.urlPattern = "/*";
        this.filterOrder = 1;
        this.name = "tapestry";
        this.productionMode = true;
    }

    public void setProductionMode(boolean productionMode) {
        this.productionMode = productionMode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFilterOrder(int filterOrder) {
        this.filterOrder = filterOrder;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public MappedFilter createTapestryFilter(Environment environment) {

        Map<String, String> params = new HashMap<>();
        params.put(SymbolConstants.PRODUCTION_MODE, Boolean.toString(productionMode));

        SymbolProvider symbolProvider = createSymbolProvider(environment);

        // TODO: support configuring extra tapestry modules...
        BQTapestryFilter filter = new BQTapestryFilter(name, symbolProvider, new Class[0]);

        return new MappedFilter(filter, Collections.singleton(urlPattern), name, params, filterOrder);
    }

    protected SymbolProvider createSymbolProvider(Environment environment) {
        return new DelegatingSymbolProvider(
                new MapSymbolProvider(environment.frameworkProperties()),
                new SingleKeySymbolProvider(SymbolConstants.EXECUTION_MODE, "production"));
    }
}
