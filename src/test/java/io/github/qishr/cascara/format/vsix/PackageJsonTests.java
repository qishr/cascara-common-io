package io.github.qishr.cascara.format.vsix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import io.github.qishr.cascara.common.diagnostic.LocalizableIOException;
import io.github.qishr.cascara.common.lang.ast.AstNode;
import io.github.qishr.cascara.lang.json.processor.JsonConverter;
import io.github.qishr.cascara.lang.json.processor.JsonSerializer;
import io.github.qishr.cascara.schema.Schema;
import io.github.qishr.cascara.schema.util.CascaraSchemaUri;
import io.github.qishr.cascara.schema.util.SchemaCompiler;
import io.github.qishr.cascara.schema.util.SchemaGenerator;

public class PackageJsonTests extends ArchiveTestBase {
    @Test
    void test() {
        PackageJsonFile jsonFile = new PackageJsonFile();

        // SchemaNode schemaNode;
        CascaraSchemaUri schemaUri = CascaraSchemaUri.of(getClass());
        SchemaGenerator generator = new SchemaGenerator();
        AstNode schemaDoc = generator.generate(jsonFile);
        SchemaCompiler compiler = new SchemaCompiler();
        Schema schema = compiler.compile(schemaDoc, schemaUri.toUri());
        // schemaNode = schema.getRoot();

        assertNotNull(schema);
        assertNotNull(schema.getRoot());

        // PlainMapNode decompiled = new SchemaDecompiler().decompile(schema);
        String json = new JsonConverter().toString(schemaDoc);

        assertNotNull(json);

        // System.out.println(json);



        jsonFile.setName("themeName");
        jsonFile.setDisplayName("displayName");
        jsonFile.setDescription("description");
        jsonFile.setVersion("0.1.0");
        jsonFile.setIcon("images/icon.png");
        jsonFile.setPublisher("Cascara");
        jsonFile.addEngine("vscode", "^1.103.0");
        jsonFile.addCategory("Themes");

        VsixThemeInfo theme = new VsixThemeInfo();
        theme.setLabel("themeLabel");
        theme.setUiTheme("themeUiTheme");
        theme.setPath("themePath");

        jsonFile.addTheme(theme);

        RepositoryInfo repo = new RepositoryInfo();
        repo.setType("git");
        repo.setUrl("git://blah");

        jsonFile.setRepository(repo);

        JsonSerializer serializer = new JsonSerializer();
        json = serializer.toString(jsonFile);

        System.out.println(json);
    }

    @Test
    void testPackageJsonAdd() throws LocalizableIOException {
        Path pkgPath = tempDir.resolve("package.vsix");
        VsixPackage pkg = VsixPackage.create(pkgPath);

        String pkgJson = """
              {
                "name": "Test Theme",
                "displayName": "Display Name",
                "version": "0.1.0"
              }
            """;

        pkg.addFile(pkgJson, ENTRY_PACKAGE_JSON);

        assertEquals("Test Theme", pkg.getName());
        assertEquals("Display Name", pkg.getDisplayName());
        assertEquals("0.1.0", pkg.getVersion());
    }

    @Test
    void testPackageJsonExtract() throws LocalizableIOException {
        Path pkgPath = tempDir.resolve("package.vsix");
        VsixPackage pkg = VsixPackage.create(pkgPath);
        pkg.setName("Test Theme");
        pkg.setDisplayName("Display Name");
        pkg.setVersion("0.1.0");
        String pkgJson = new String(pkg.extractFile("extension/package.json"));
        assertTrue(pkgJson.contains("Test Theme"));
        assertTrue(pkgJson.contains("Display Name"));
        assertTrue(pkgJson.contains("0.1.0"));
    }
}
