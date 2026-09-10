package io.github.qishr.cascara.format.vsix;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;



public class ManifestXmlTests  extends ArchiveTestBase {
    @Test
    void test() throws IOException {
        Path sourceDir = tempDir.resolve("source");
        Files.createDirectories(sourceDir.resolve("images"));

        Path pkgPath = tempDir.resolve("package.vsix");
        VsixPackage pkg = VsixPackage.create(pkgPath);
        pkg.getIdentity()
            .setId("v-id")
            .setVersion("v-version")
            .setPublisher("v-publisher");
        pkg.setVersion("0.1.0");
        pkg.flush();

        String manifestXml = new String(pkg.extractFile(ENTRY_MANIFEST_XML));
        assertTrue(manifestXml.contains("v-id"));
        assertTrue(manifestXml.contains("v-publisher"));
        assertTrue(manifestXml.contains("0.1.0"));
    }
}
