package io.github.qishr.cascara.common.io.provider;

import java.io.InputStream;
import java.net.URI;

import io.github.qishr.cascara.common.diagnostic.LocalizableException;
import io.github.qishr.cascara.common.diagnostic.code.FileDiagnosticCode;
import io.github.qishr.cascara.common.io.ResourceStream;

public class ResResourceProvider implements ResourceProvider {
    private Class<?> clazz;

    public ResResourceProvider(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public ResourceStream getResourceAsStream(URI uri) throws LocalizableException {
        String path = uri.getSchemeSpecificPart().replace("//", "");
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        InputStream is = clazz.getClassLoader().getResourceAsStream(path);
        if (is == null) throw new LocalizableException(FileDiagnosticCode.FILE_NOT_FOUND, path);
        return new ResourceStream(is, null);
    }
}
