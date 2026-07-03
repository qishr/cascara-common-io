package io.github.qishr.cascara.common.io.provider;

import java.io.InputStream;
import java.net.URI;

import io.github.qishr.cascara.common.content.type.ContentTypeStore;
import io.github.qishr.cascara.common.diagnostic.LocalizableIOException;
import io.github.qishr.cascara.common.diagnostic.code.FileDiagnosticCode;
import io.github.qishr.cascara.common.io.ResourceStream;
import io.github.qishr.cascara.common.io.UriScheme;
import io.github.qishr.cascara.common.util.ContentType;

public class ResResourceProvider extends AbstractResourceProvider  {
    private Class<?> clazz;

    public ResResourceProvider(Class<?> clazz) {
        super(UriScheme.RES);
        this.clazz = clazz;
    }

    public ResResourceProvider() {
        super(UriScheme.RES);
    }

    public void setClass(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public ResourceStream getResourceAsStream(URI uri) throws LocalizableIOException {
        String path = uri.getSchemeSpecificPart().replace("//", "");

        InputStream is = clazz.getResourceAsStream(path);
        if (is == null && path.startsWith("/")) {
            path = path.substring(1);
            is = clazz.getResourceAsStream(path);
        }

        // TODO: If clazz is in a JPMS module, check if the module opens the package.
        // If it doesn't, put that detail in the exception.

        if (is == null) throw new LocalizableIOException(FileDiagnosticCode.FILE_NOT_FOUND, path);

        // Infer content type from filename
        ContentType contentType = ContentTypeStore.instance().resolve(fileNameExtension(path));

        if (contentType == null) {
            return new ResourceStream(is, null);
        }

        return new ResourceStream(is, contentType);
    }

    /// Returns the filename extension (including the dot), or null if there is none.
    private String fileNameExtension(String fileName) {
        int dot = fileName.lastIndexOf(".");
        if (dot > 0) {
            return fileName.substring(dot);
        }
        return null;
    }
}
