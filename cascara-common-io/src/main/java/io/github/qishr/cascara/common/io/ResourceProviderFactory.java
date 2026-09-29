package io.github.qishr.cascara.common.io;

import io.github.qishr.cascara.common.io.provider.ResourceProvider;
import io.github.qishr.cascara.common.service.AbstractServiceProviderFactory;
import io.github.qishr.cascara.common.service.CapabilityQueries;
import io.github.qishr.cascara.common.service.SPL;
import io.github.qishr.cascara.common.service.ServiceException;

public class ResourceProviderFactory extends AbstractServiceProviderFactory {
    public ResourceProviderFactory() {
        super();
    }

    public ResourceProviderFactory(SPL layer) {
        super(layer);
    }

    public ResourceProvider getResourceProvider(String uriScheme) throws ServiceException {
        return createServiceProvider(
            ResourceProvider.class,
            CapabilityQueries.hasExactValue(ResourceProvider.URI_SCHEME, uriScheme)
        );
    }

}
