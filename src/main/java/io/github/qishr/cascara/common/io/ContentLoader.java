package io.github.qishr.cascara.common.io;

import java.io.IOException;
import java.net.URI;

import io.github.qishr.cascara.common.content.ResourceContent;

public interface ContentLoader {
    ResourceContent getContent(URI uri) throws IOException;
}
