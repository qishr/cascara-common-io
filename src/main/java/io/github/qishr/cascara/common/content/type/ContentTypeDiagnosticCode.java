package io.github.qishr.cascara.common.content.type;

import io.github.qishr.cascara.common.diagnostic.code.DiagnosticCode;

public enum ContentTypeDiagnosticCode implements DiagnosticCode {
    REGISTRY_READ_ERROR("CCT-001", "Failed to read content type registry"),
    REGISTRY_UPDATE_ERROR("CCT-002", "Failed to update content type registry");

    private final String code;
    private final String message;

    ContentTypeDiagnosticCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override public String getCode() { return code; }
    @Override public String getMessage() { return message; }
}