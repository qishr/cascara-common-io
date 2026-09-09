// # License & Terms
//
// This file is part of **Cascara**.
//
// **Cascara** is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <https://www.gnu.org/licenses/>.
//
// ---
//
// ## Special Runtime Exception
//
// As a special exception, the copyright holders of this library give you
// permission to link this library with independent modules to produce an
// executable, regardless of the license terms of these independent modules,
// and to copy and distribute the resulting executable under terms of your
// choice, provided that you also meet, for each linked independent module,
// the terms and conditions of the license of that module.
//
// An independent module is a module which is not derived from or based on
// this library. If you modify this library, you may extend this exception
// to your version of the library, but you are not obligated to do so. If
// you do not wish to do so, delete this exception statement from your
// version.

package io.github.qishr.cascara.format.vsix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import io.github.qishr.cascara.common.diagnostic.LocalizableIOException;
import io.github.qishr.cascara.common.util.ArchiveFile.EntryInfo;

class VsixPackageTests extends ArchiveTestBase {

    @Test
    void addDirectoryAddsFilesToArchive() throws Exception {
        Path sourceDir = tempDir.resolve("source");
        Files.createDirectories(sourceDir.resolve("images"));

        Files.writeString(
            sourceDir.resolve(LICENSE_F),
            "first file",
            StandardCharsets.UTF_8
        );

        Files.writeString(
            sourceDir.resolve(README_F),
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
        pkg.setLicense(sourceDir.resolve(LICENSE_F));
        pkg.setReadme(sourceDir.resolve(README_F));
        pkg.close();


        assertTrue(Files.exists(pkgPath));

        VsixPackage actual = VsixPackage.load(pkgPath);
        List<EntryInfo> files = actual.listFiles();
        assertContainsFile("extension/LICENSE.md", files, pkgPath);
        assertContainsFile("extension/README.md", files, pkgPath);

        // Schema schema = new SchemaResolver().getSchemaForClass(VsixMetadata.class);
        // PlainMapNode decompiled = new SchemaDecompiler().decompile(schema);
        // String json = new JsonConverter().toString(decompiled);
        // assertNotNull(json);
    }

    @Test
    void testPackageJsonSimple() throws LocalizableIOException {
        Path pkgPath = tempDir.resolve("package.vsix");
        VsixPackage pkg = VsixPackage.create(pkgPath);
        pkg.setName("Test Theme");
        pkg.setDisplayName("Display Name");
        pkg.setVersion("0.1.0");
        assertEquals("Test Theme", pkg.getName());
        assertEquals("Display Name", pkg.getDisplayName());
        assertEquals("0.1.0", pkg.getVersion());
    }
}
