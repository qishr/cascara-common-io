package io.github.qishr.cascara.common.io;

import java.io.InputStream;

import io.github.qishr.cascara.common.util.ContentType;

public final class ResourceStream {
    public final InputStream stream;
    public final ContentType contentType; // null for non-HTTP

    public ResourceStream(InputStream stream, ContentType contentType) {
        this.stream = stream;
        this.contentType = contentType;
    }
}
