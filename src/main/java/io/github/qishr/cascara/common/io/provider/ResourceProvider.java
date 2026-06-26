package io.github.qishr.cascara.common.io.provider;

import java.net.URI;

import io.github.qishr.cascara.common.diagnostic.LocalizableException;
import io.github.qishr.cascara.common.io.ResourceStream;
import io.github.qishr.cascara.common.service.ServiceProvider;

public interface ResourceProvider extends ServiceProvider {
    ResourceStream getResourceAsStream(URI uri) throws LocalizableException;
}
