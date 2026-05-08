package io.github.qishr.cascara.common.content.type;

import io.github.qishr.cascara.common.util.CascaraRuntimeException;
import io.github.qishr.cascara.common.util.ContentType;

public class ContentTypeException extends CascaraRuntimeException {
    private final ContentType contentType;

    public ContentTypeException(String message) {
        super(message);
        this.contentType = null;
    }

    public ContentTypeException(String message, ContentType contentType) {
        super(message + ": " + contentType.getCanonicalId());
        this.contentType = contentType;
    }

    public ContentTypeException(String message, Exception e) {
        super(message, e);
        this.contentType = null;
    }

    public ContentType getContentType() {
        return contentType;
    }
}
