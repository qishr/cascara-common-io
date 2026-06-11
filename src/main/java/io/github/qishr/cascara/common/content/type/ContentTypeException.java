package io.github.qishr.cascara.common.content.type;

import io.github.qishr.cascara.common.diagnostic.LocalizableRuntimeException;
import io.github.qishr.cascara.common.diagnostic.code.DiagnosticCode;
import io.github.qishr.cascara.common.util.ContentType;

public class ContentTypeException extends LocalizableRuntimeException {
    private final ContentType contentType;

    public ContentTypeException(DiagnosticCode code, Object... details) {
        super(code, details);
        this.contentType = null;
    }

    public ContentTypeException(Throwable cause, DiagnosticCode code, Object... details) {
        super(cause, code, details);
        this.contentType = null;
    }

    public ContentType getContentType() {
        return contentType;
    }
}
