package io.github.qishr.cascara.common.io.provider;

import java.net.URI;

import io.github.qishr.cascara.common.diagnostic.LocalizableIOException;
import io.github.qishr.cascara.common.io.ResourceStream;
import io.github.qishr.cascara.common.io.UriScheme;

public class CascaraResourceProvider extends AbstractResourceProvider {

    public CascaraResourceProvider() {
        super(UriScheme.CASCARA);
    }

    @Override
    public ResourceStream getResourceAsStream(URI uri) throws LocalizableIOException {
        throw new UnsupportedOperationException("Unimplemented method 'CascaraResourceProvider.getContentAsStream'");
    }

}
