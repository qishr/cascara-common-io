package io.github.qishr.cascara.common.io.provider;

import java.net.URI;

import io.github.qishr.cascara.common.diagnostic.LocalizableException;
import io.github.qishr.cascara.common.io.ResourceStream;

public interface ResourceProvider {
    ResourceStream getResourceAsStream(URI uri) throws LocalizableException;
}
