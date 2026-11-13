// # License & Terms
//
// This file is part of **Cascara**.
//
// **Cascara** is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <https://www.gnu.org/licenses/>.
//
// ---
//
// ## Special Runtime Exception
//
// As a special exception, the copyright holders of this library give you
// permission to link this library with independent modules to produce an
// executable, regardless of the license terms of these independent modules,
// and to copy and distribute the resulting executable under terms of your
// choice, provided that you also meet, for each linked independent module,
// the terms and conditions of the license of that module.
//
// An independent module is a module which is not derived from or based on
// this library. If you modify this library, you may extend this exception
// to your version of the library, but you are not obligated to do so. If
// you do not wish to do so, delete this exception statement from your
// version.


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
