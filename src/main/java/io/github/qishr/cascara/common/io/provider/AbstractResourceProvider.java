package io.github.qishr.cascara.common.io.provider;

import io.github.qishr.cascara.common.io.UriScheme;
import io.github.qishr.cascara.common.util.Properties;

public abstract class AbstractResourceProvider implements ResourceProvider {
    private Properties properties;
    private String uriScheme;

    protected AbstractResourceProvider(UriScheme uriScheme) {
        this.uriScheme = uriScheme.asString();
    }

    protected AbstractResourceProvider(String uriScheme) {
        this.uriScheme = uriScheme;
    }

    @Override
    public Properties getServiceProperties() {
        if (properties == null) {
            properties = new Properties();
            properties.set("uriScheme", uriScheme);
        }
        return properties;
    }
}
