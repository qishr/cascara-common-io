package io.github.qishr.cascara.common.io;

import java.net.URI;

import io.github.qishr.cascara.common.content.ResourceContent;
import io.github.qishr.cascara.common.diagnostic.LocalizableIOException;

public interface ContentLoader {
    ResourceContent getContent(URI uri) throws LocalizableIOException;
}
