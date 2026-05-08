package io.github.qishr.cascara.common.content.type;

import java.util.List;
import java.util.Set;

import io.github.qishr.cascara.common.util.ContentType;

/// A runtime‑merged content type produced by the ContentTypeNormalizer.
/// This object represents a group of module‑declared ContentType instances
/// that were determined to be equivalent based on MIME type intersection
/// and suffix matching.
///
/// MergedContentType is *ephemeral*. It is created fresh each time the
/// normalizer runs and is reconciled against the persistent canonical
/// registry to determine stable canonical IDs.
///
/// This class is NOT persisted and should never be used as a long‑term
/// identifier for content types.
public final class MergedContentType {
    private String canonicalId;

    private String name;

    private Set<String> suffixes;

    private Set<String> mimeTypes;

    private List<ContentType> originalDeclarations;

    public MergedContentType(
            String canonicalId,
            String canonicalName,
            Set<String> mergedSuffixes,
            Set<String> mergedMimeTypes,
            List<ContentType> originals
    ) {
        this.canonicalId = canonicalId;
        this.name = canonicalName;
        this.suffixes = mergedSuffixes;
        this.mimeTypes = mergedMimeTypes;
        this.originalDeclarations = originals;
    }

    public String getCanonicalId() {
        return canonicalId;
    }

    public String getName() {
        return name;
    }

    public Set<String> getSuffixes() {
        return suffixes;
    }

    public Set<String> getMimeTypes() {
        return mimeTypes;
    }

    public List<ContentType> getOriginalDeclarations() {
        return originalDeclarations;
    }

    public void setCanonicalId(String canonicalId) {
        this.canonicalId = canonicalId;
    }

    public void setName(String canonicalName) {
        this.name = canonicalName;
    }

    public void setSuffixes(Set<String> mergedSuffixes) {
        this.suffixes = mergedSuffixes;
    }

    public void setMimeTypes(Set<String> mergedMimeTypes) {
        this.mimeTypes = mergedMimeTypes;
    }

    public void setOriginalDeclarations(List<ContentType> originalDeclarations) {
        this.originalDeclarations = originalDeclarations;
    }
}
