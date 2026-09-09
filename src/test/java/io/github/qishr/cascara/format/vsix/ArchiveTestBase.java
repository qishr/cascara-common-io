package io.github.qishr.cascara.format.vsix;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.jupiter.api.io.TempDir;

import io.github.qishr.cascara.common.util.ArchiveFile.EntryInfo;

public abstract class ArchiveTestBase {
    protected static final String LICENSE_F = "LICENSE";
    protected static final String README_F = "README.md";

    protected static final String EXTENSION_DIR = "extension/";
    protected static final String IMAGES_DIR = EXTENSION_DIR + "images/";
    protected static final String THEMES_DIR = EXTENSION_DIR + "themes/";

    protected static final String CHANGELOG_ENTRY = EXTENSION_DIR + "CHANGELOG.md";
    protected static final String CONTENT_TYPES_ENTRY = EXTENSION_DIR + "[Content_Types].xml";
    protected static final String LICENSE_ENTRY = EXTENSION_DIR + "LICENSE.md";
    protected static final String MANIFEST_XML_ENTRY = EXTENSION_DIR + "extension.vsixmanifest";
    protected static final String PACKAGE_JSON_ENTRY = EXTENSION_DIR + "package.json";
    protected static final String README_ENTRY = EXTENSION_DIR + "README.md";

    @TempDir
    protected Path tempDir;

    protected void assertContainsFile(String fileName, List<EntryInfo> files, Path pkgPath) throws IOException {
        for (EntryInfo info : files) {
            if (info.getPath().equals(fileName)) {
                return;
            }
        }
        showContents(pkgPath);
        assertTrue(false, "File missing: " + fileName);
    }

    protected void showContents(Path zipFile) throws IOException {
        try (ZipInputStream zip = new ZipInputStream(
                Files.newInputStream(zipFile))) {

            ZipEntry entry;

            while ((entry = zip.getNextEntry()) != null) {
                System.out.println(entry.getName());
            }
        }
    }

}
