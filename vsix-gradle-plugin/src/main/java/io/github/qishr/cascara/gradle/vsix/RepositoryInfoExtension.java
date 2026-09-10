package io.github.qishr.cascara.gradle.vsix;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

import javax.inject.Inject;

public abstract class RepositoryInfoExtension {

    public abstract Property<String> getType();
    public abstract Property<String> getUrl();

    @Inject
    public RepositoryInfoExtension(ObjectFactory objects) {
    }
}