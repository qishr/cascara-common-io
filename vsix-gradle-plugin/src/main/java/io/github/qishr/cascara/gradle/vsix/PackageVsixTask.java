package io.github.qishr.cascara.gradle.vsix;

import io.github.qishr.cascara.format.vsix.VsixPackage;
import io.github.qishr.cascara.format.vsix.VsixMetadata;
import io.github.qishr.cascara.format.vsix.RepositoryInfo;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.bundling.Zip;

import java.io.File;


import org.gradle.api.tasks.*;

import java.io.IOException;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;

import java.nio.file.Path;

public abstract class PackageVsixTask extends DefaultTask {

    // Metadata inputs
    @Input public abstract Property<String> getManifestName();
    @Input public abstract Property<String> getManifestVersion();
    @Input @Optional public abstract Property<String> getManifestId();
    @Input @Optional public abstract Property<String> getManifestDisplayName();
    @Input @Optional public abstract Property<String> getManifestDescription();
    @Input @Optional public abstract Property<String> getManifestPublisher();
    @Input @Optional public abstract Property<String> getManifestIcon();
    @Input @Optional public abstract ListProperty<String> getCategories();
    @Input @Optional public abstract MapProperty<String, String> getEngines();
    @Input @Optional public abstract MapProperty<String, String> getRepository();

    // File inputs
    @InputFile @Optional public abstract RegularFileProperty getReadme();
    @InputFile @Optional public abstract RegularFileProperty getLicense();
    @InputFile @Optional public abstract RegularFileProperty getChangeLog();
    @InputDirectory @Optional public abstract DirectoryProperty getImagesDir();
    @InputFiles public abstract ConfigurableFileCollection getThemeFiles();

    @OutputFile public abstract RegularFileProperty getOutputFile();

    @TaskAction
    public void buildPackage() throws Exception {
        File destination = getOutputFile().get().getAsFile();

        try (VsixPackage vsix = VsixPackage.create(destination.toPath())) {

            // Build package.json and vsixmanifest directly from task properties

            vsix.getIdentity()
                .setId(getManifestId().getOrNull())
                .setVersion(getManifestVersion().get())
                .setPublisher(getManifestPublisher().getOrNull());

            vsix.setName(getManifestName().get())
                .setVersion(getManifestVersion().get())
                .setDisplayName(getManifestDisplayName().getOrNull())
                .setDescription(getManifestDescription().getOrNull())
                .setPublisher(getManifestPublisher().getOrNull())
                .setIcon(getManifestIcon().getOrNull())
                .setCategories(getCategories().getOrElse(java.util.Collections.emptyList()))
                .setEngines(getEngines().getOrElse(java.util.Collections.emptyMap()))
                .setRepository(new RepositoryInfo()
                    .setType(getRepository().get().get("type"))
                    .setUrl(getRepository().get().get("url"))
                );
                //OrElse(new java.util.Collections.emptyMap()));

            if (getReadme().isPresent()) vsix.setReadme(getReadme().get().getAsFile().toPath());
            if (getLicense().isPresent()) vsix.setLicense(getLicense().get().getAsFile().toPath());
            if (getChangeLog().isPresent()) vsix.setChangeLog(getChangeLog().get().getAsFile().toPath());
            if (getImagesDir().isPresent()) vsix.addImagesFromDirectory(getImagesDir().get().getAsFile().toPath());

            for (File themeFile : getThemeFiles().getAsFileTree().getFiles()) {
                if (themeFile.isFile() && themeFile.getName().endsWith(".json")) {
                    // System.out.println("addThemeFile: " + themeFile.toPath());
                    vsix.addThemeFile(themeFile.toPath());
                }
            }
            // for (File themeFile : getThemeFiles().getFiles()) {
            //     for (File fileOrDir : getThemeFiles().getFiles()) {
            //         if (fileOrDir.isDirectory()) {
            //             vsix.addThemesFromDirectory(fileOrDir.toPath());
            //         } else if (fileOrDir.isFile() && fileOrDir.getName().endsWith(".json")) {
            //             vsix.addThemeFile(fileOrDir.toPath());
            //         }
            //     }
            //     //vsix.addThemeFile(themeFile.toPath());
            // }
        }
    }
}

// public abstract class PackageVsixTask extends DefaultTask {

//     @InputFile
//     public abstract RegularFileProperty getManifest();

//     @InputFile
//     public abstract RegularFileProperty getPackageJson();

//     @InputFile
//     @Optional
//     public abstract RegularFileProperty getChangeLog();

//     @InputFile
//     @Optional
//     public abstract RegularFileProperty getContentTypes();

//     @InputFile
//     @Optional
//     public abstract RegularFileProperty getLicense();

//     @InputFile
//     @Optional
//     public abstract RegularFileProperty getReadme();

//     @InputDirectory
//     @Optional
//     public abstract DirectoryProperty getImagesDir();

//     @InputDirectory
//     @Optional
//     public abstract DirectoryProperty getThemesDir();


//     @OutputFile
//     public abstract RegularFileProperty getOutputFile();

//     @TaskAction
//     public void buildPackage() throws Exception {
//         File destination = getOutputFile().get().getAsFile();

//         if (destination.getParentFile() != null) {
//             destination.getParentFile().mkdirs();
//         }

//         try (VsixPackage vsix = VsixPackage.create(destination.toPath())) {
//             // High-level VsixPackage API calls
//             vsix.setPackageJson(getPackageJson().get().getAsFile().toPath());
//             vsix.setManifest(getManifest().get().getAsFile().toPath());

//             if (getChangeLog().isPresent()) {
//                 vsix.setChangeLog(getChangeLog().get().getAsFile().toPath());
//             }

//             if (getContentTypes().isPresent()) {
//                 vsix.setContentTypes(getContentTypes().get().getAsFile().toPath());
//             }

//             if (getLicense().isPresent()) {
//                 vsix.setLicense(getLicense().get().getAsFile().toPath());
//             }

//             if (getReadme().isPresent()) {
//                 vsix.setReadme(getReadme().get().getAsFile().toPath());
//             }

//             if (getImagesDir().isPresent()) {
//                 vsix.addImagesFromDirectory(getImagesDir().get().getAsFile().toPath());
//             }

//             if (getThemesDir().isPresent()) {
//                 vsix.addThemesFromDirectory(getThemesDir().get().getAsFile().toPath());
//             }

//             vsix.flush();
//         }
//     }
// }


// // public abstract class PackageVsixTask extends Zip {

// //     @Input
// //     @Optional
// //     public abstract Property<String> getManifestXml();

// //     @Input
// //     @Optional
// //     public abstract Property<String> getContentTypesXml();

// //     public PackageVsixTask() {
// //         // Set extension format defaults
// //         getArchiveExtension().set("vsix");
// //     }

// //     // Convenience methods for programmatic manifest injection
// //     public void addManifest(String manifestXmlContent) {
// //         getManifestXml().set(manifestXmlContent);
// //     }

// //     public void addContentTypes(String contentTypesXmlContent) {
// //         getContentTypesXml().set(contentTypesXmlContent);
// //     }

// //     @TaskAction
// //     @Override
// //     protected void copy() {
// //         // Let Zip handle copy specs (from, into, include, rename)
// //         super.copy();

// //         File targetVsix = getArchiveFile().get().getAsFile();

// //         // Inject in-memory XML manifests via VsixPackage if provided
// //         if (getManifestXml().isPresent() || getContentTypesXml().isPresent()) {
// //             try (VsixPackage vsix = VsixPackage.create(targetVsix.toPath())) {
// //                 if (getManifestXml().isPresent()) {
// //                     vsix.addManifest(getManifestXml().get());
// //                 }
// //                 if (getContentTypesXml().isPresent()) {
// //                     vsix.addContentTypes(getContentTypesXml().get());
// //                 }
// //             } catch (Exception e) {
// //                 throw new RuntimeException("Failed to stamp VSIX manifest headers", e);
// //             }
// //         }
// //     }
// // }

// // // public abstract class PackageVsixTask extends DefaultTask {

// // //     @InputDirectory
// // //     public abstract DirectoryProperty getSourceDir();

// // //     @OutputFile
// // //     public abstract RegularFileProperty getOutputFile();

// // //     @TaskAction
// // //     public void packageVsix() throws Exception {
// // //         File sourceDir = getSourceDir().get().getAsFile();
// // //         File destination = getOutputFile().get().getAsFile();

// // //         // Ensure parent directories exist
// // //         if (destination.getParentFile() != null) {
// // //             destination.getParentFile().mkdirs();
// // //         }

// // //         try (VsixPackage vsix = VsixPackage.create(destination.toPath())) {
// // //             // Add extension manifest ([Content_Types].xml, extension.vsixmanifest, etc.)
// // //             vsix.addDirectory(sourceDir.toPath());
// // //         }
// // //     }
// // // }