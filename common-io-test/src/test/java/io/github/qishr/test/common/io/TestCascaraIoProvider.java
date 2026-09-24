package io.github.qishr.test.common.io;

import java.net.URI;

import io.github.qishr.cascara.common.diagnostic.LocalizableIOException;
import io.github.qishr.cascara.common.io.ResourceStream;
import io.github.qishr.cascara.common.io.provider.AbstractResourceProvider;
import io.github.qishr.cascara.common.util.UriScheme;

public class TestCascaraIoProvider extends AbstractResourceProvider {

    public TestCascaraIoProvider() {
        super(UriScheme.CASCARA);
    }

    @Override
    public ResourceStream getResourceAsStream(URI uri) throws LocalizableIOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getResourceAsStream'");
    }

}
