package io.github.qishr.cascara.common.io.provider;

import java.net.URI;

import io.github.qishr.cascara.common.diagnostic.LocalizableException;
import io.github.qishr.cascara.common.io.ResourceStream;

public class CascaraResourceProvider implements ResourceProvider {

    @Override
    public ResourceStream getResourceAsStream(URI uri) throws LocalizableException {
        throw new UnsupportedOperationException("Unimplemented method 'CascaraResourceProvider.getContentAsStream'");
    }

}
