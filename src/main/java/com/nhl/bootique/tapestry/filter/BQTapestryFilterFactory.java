package com.nhl.bootique.tapestry.filter;

import com.nhl.bootique.jetty.MappedFilter;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.ioc.internal.services.MapSymbolProvider;
import org.apache.tapestry5.ioc.services.SymbolProvider;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BQTapestryFilterFactory {

    protected String urlPattern;
    protected int filterOrder;

    protected String name;

    // properties from http://tapestry.apache.org/configuration.html

    protected boolean productionMode;
    protected String executionModes;
    protected String supportedLocales;
    protected String charset;
    protected String appPackage;

    public BQTapestryFilterFactory() {
        this.urlPattern = "/*";
        this.filterOrder = 1;
        this.name = "tapestry";

        this.productionMode = true;
        this.executionModes = "production";
        this.charset = "UTF-8";
    }

    public void setProductionMode(boolean productionMode) {
        this.productionMode = productionMode;
    }

    public void setExecutionModes(String executionModes) {
        this.executionModes = executionModes;
    }

    public void setSupportedLocales(String supportedLocales) {
        this.supportedLocales = supportedLocales;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public void setFilterOrder(int filterOrder) {
        this.filterOrder = filterOrder;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public MappedFilter createTapestryFilter() {


        SymbolProvider symbolProvider = createSymbolProvider();

        // TODO: support configuring extra tapestry modules...
        BQTapestryFilter filter = new BQTapestryFilter(name, symbolProvider, new Class[0]);

        return new MappedFilter(filter, Collections.singleton(urlPattern), name, filterOrder);
    }

    protected SymbolProvider createSymbolProvider() {

        // TODO: support more Tapestry symbols

        Map<String, String> params = new HashMap<>();
        params.put(SymbolConstants.PRODUCTION_MODE, Boolean.toString(productionMode));
        params.put(SymbolConstants.EXECUTION_MODE, executionModes);
        params.put(SymbolConstants.CHARSET, charset);

        // compression should be configured at the Jetty level
        params.put(SymbolConstants.GZIP_COMPRESSION_ENABLED, "false");

        params.put(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM, Objects.requireNonNull(appPackage));

        if(supportedLocales != null) {
            params.put(SymbolConstants.SUPPORTED_LOCALES, supportedLocales);
        }

        return new MapSymbolProvider(params);
    }
}
