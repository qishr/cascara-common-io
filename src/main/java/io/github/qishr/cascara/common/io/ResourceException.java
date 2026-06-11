package io.github.qishr.cascara.common.io;

import io.github.qishr.cascara.common.diagnostic.code.DiagnosticCode;

import io.github.qishr.cascara.common.diagnostic.LocalizableException;

public final class ResourceException extends LocalizableException {
    public ResourceException(DiagnosticCode code, Object... details) {
        super(code, details);
    }
}
