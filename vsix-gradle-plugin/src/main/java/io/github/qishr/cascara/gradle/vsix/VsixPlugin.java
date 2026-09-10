package io.github.qishr.cascara.gradle.vsix;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class VsixPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        VsixExtension extension = project.getExtensions().create("vsix", VsixExtension.class);

        project.getTasks().register("packageVsix", PackageVsixTask.class, task -> {
            task.setGroup("distribution");
            task.setDescription("Packages VS Code extension VSIX archive");

            // Wire Extension files -> Task inputs
            task.getReadme().set(extension.getReadme());
            task.getLicense().set(extension.getLicense());
            task.getChangeLog().set(extension.getChangeLog());
            task.getImagesDir().set(extension.getImagesDir());

            // Collect explicitly added theme files or theme build directory
            task.getThemeFiles().from(extension.getThemes());

            // Bind structured manifest fields
            task.getManifestName().convention(extension.getName().orElse(project.getName()));
            task.getManifestVersion().convention(extension.getVersion().orElse(project.provider(() -> project.getVersion().toString())));
            task.getManifestDisplayName().set(extension.getDisplayName());
            task.getManifestDescription().set(extension.getDescription());
            task.getManifestId().set(extension.getId());
            task.getManifestPublisher().set(extension.getPublisher());
            task.getManifestIcon().set(extension.getIcon());
            task.getCategories().set(extension.getCategories());
            task.getEngines().set(extension.getEngines());
            task.getRepository().set(extension.getRepository());

            // Default output location
            task.getOutputFile().convention(
                project.getLayout().getBuildDirectory().file("distributions/" + project.getName() + ".vsix")
            );
        });
    }
}