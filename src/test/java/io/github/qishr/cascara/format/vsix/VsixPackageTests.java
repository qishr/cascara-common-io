package io.github.qishr.cascara.format.vsix;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.jupiter.api.io.TempDir;

import io.github.qishr.cascara.common.util.ArchiveFile.EntryInfo;

class VsixPackageTests {

    private static final String LICENSE_FILENAME = "LICENSE";
    private static final String README_FILENAME = "README.md";

    @TempDir
    Path tempDir;

    @Test
    void addDirectoryAddsFilesToArchive() throws Exception {
        Path sourceDir = tempDir.resolve("source");
        Files.createDirectories(sourceDir.resolve("images"));

        Files.writeString(
            sourceDir.resolve(LICENSE_FILENAME),
            "first file",
            StandardCharsets.UTF_8
        );

        Files.writeString(
            sourceDir.resolve(README_FILENAME),
            "first file",
            StandardCharsets.UTF_8
        );

        Files.writeString(
                sourceDir.resolve("images/icon.txt"),
                "second file",
                StandardCharsets.UTF_8
        );

        Path pkgPath = tempDir.resolve("package.vsix");
        VsixPackage pkg = VsixPackage.create(pkgPath);
        // pkg.addDirectory(sourceDir);
        pkg.setLicense(sourceDir.resolve(LICENSE_FILENAME));
        pkg.setReadme(sourceDir.resolve(README_FILENAME));
        pkg.close();


        assertTrue(Files.exists(pkgPath));

        VsixPackage actual = VsixPackage.load(pkgPath);
        List<EntryInfo> files = actual.listFiles();
        assertContainsFile("extension/LICENSE.md", files, pkgPath);
        assertContainsFile("extension/README.md", files, pkgPath);
    }

    private void assertContainsFile(String fileName, List<EntryInfo> files, Path pkgPath) throws IOException {
        for (EntryInfo info : files) {
            if (info.getPath().equals(fileName)) {
                return;
            }
        }
        showContents(pkgPath);
        assertTrue(false, "File missing: " + fileName);
    }

    private void showContents(Path zipFile) throws IOException {
        try (ZipInputStream zip = new ZipInputStream(
                Files.newInputStream(zipFile))) {

            ZipEntry entry;

            while ((entry = zip.getNextEntry()) != null) {
                System.out.println(entry.getName());
            }
        }
    }
}
