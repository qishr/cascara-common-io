package io.github.qishr.cascara.common.io;

import java.io.IOException;
import java.net.URI;

import io.github.qishr.cascara.common.content.ResourceContent;
import io.github.qishr.cascara.common.diagnostic.LocalizableException;

public interface ContentLoader {
    ResourceContent getContent(URI uri) throws LocalizableException;
}
