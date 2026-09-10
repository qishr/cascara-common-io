package io.github.qishr.cascara.format.vsix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

public class ThemeTests extends ArchiveTestBase {

    @Test
    void testAddThemeFromString() throws IOException {
        Path pkgPath = tempDir.resolve("package.vsix");
        VsixPackage pkg = VsixPackage.create(pkgPath);

        String themeJson = """
              {
                "name": "Test Theme",
                "type": "dark",
                "semanticHighlighting": true
              }
            """;

        pkg.addFile(themeJson, THEMES_DIR + "test.json");

        assertFalse(pkg.getThemes().isEmpty());

        VsixThemeInfo theme = pkg.getThemes().getFirst();

        assertEquals("Test Theme", theme.getName());
        assertEquals("dark", theme.getType());
        assertEquals(true, theme.getSemanticHighlighting());
    }

    @Test
    void testAddThemeFileFile() throws Exception {
        Path sourceDir = tempDir.resolve("source");
        Files.createDirectories(sourceDir.resolve("themes"));

        Path pkgPath = tempDir.resolve("package.vsix");
        VsixPackage pkg = VsixPackage.create(pkgPath);

        String fileName = "test.json";
        Path filePath = sourceDir.resolve(fileName);
        String themeJson = """
              {
                "name": "Test Theme",
                "type": "dark",
                "semanticHighlighting": true
              }
            """;
        Files.writeString(
            filePath, themeJson, StandardCharsets.UTF_8
        );

        pkg.addFile(filePath, THEMES_DIR + fileName);

        assertFalse(pkg.getThemes().isEmpty());

        VsixThemeInfo theme = pkg.getThemes().getFirst();

        pkg.close();

        assertEquals("Test Theme", theme.getName());
        assertEquals("dark", theme.getType());
        assertEquals(true, theme.getSemanticHighlighting());
    }
}
