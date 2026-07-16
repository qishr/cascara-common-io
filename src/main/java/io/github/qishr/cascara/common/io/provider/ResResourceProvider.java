package io.github.qishr.cascara.common.io.provider;

import java.io.InputStream;
import java.net.URI;

import io.github.qishr.cascara.common.content.type.ContentTypeStore;
import io.github.qishr.cascara.common.diagnostic.LocalizableIOException;
import io.github.qishr.cascara.common.io.ResourceStream;
import io.github.qishr.cascara.common.io.UriScheme;
import io.github.qishr.cascara.common.util.ContentType;
import io.github.qishr.cascara.common.util.JreUtil;

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
        InputStream is = JreUtil.getResourceAsStream(clazz, path);

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
