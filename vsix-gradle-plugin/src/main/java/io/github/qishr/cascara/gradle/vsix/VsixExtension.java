package io.github.qishr.cascara.gradle.vsix;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;

import javax.inject.Inject;

public abstract class VsixExtension {

    public abstract RegularFileProperty getReadme();
    public abstract RegularFileProperty getLicense();
    public abstract RegularFileProperty getChangeLog();
    public abstract DirectoryProperty getImagesDir();
    public abstract ConfigurableFileCollection getThemes();

    // Nested Manifest Specs
    public abstract Property<String> getName();
    public abstract Property<String> getDisplayName();
    public abstract Property<String> getDescription();
    public abstract Property<String> getVersion();
    public abstract Property<String> getId();
    public abstract Property<String> getPublisher();
    public abstract Property<String> getIcon();
    public abstract ListProperty<String> getCategories();
    public abstract MapProperty<String, String> getEngines();
    public abstract MapProperty<String, String> getRepository();

    @Inject
    public VsixExtension(ObjectFactory objects) {
        // Sensible defaults
        getCategories().convention(objects.listProperty(String.class));
        getEngines().convention(objects.mapProperty(String.class, String.class));
        getRepository().convention(objects.mapProperty(String.class, String.class));
    }

    public void categories(String... items) {
        getCategories().addAll(items);
    }
}