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

import java.nio.file.Path;

import io.github.qishr.cascara.common.diagnostic.LocalizableIOException;
import io.github.qishr.cascara.common.diagnostic.LocalizableRuntimeException;
import io.github.qishr.cascara.common.diagnostic.code.GenericDiagnosticCode;
import io.github.qishr.cascara.common.lang.ast.ScalarAstNode;
import io.github.qishr.cascara.common.util.ArchiveFile;
import io.github.qishr.cascara.common.util.Properties;
import io.github.qishr.cascara.lang.json.ast.JsonArray;
import io.github.qishr.cascara.lang.json.ast.JsonNode;
import io.github.qishr.cascara.lang.json.ast.JsonObject;
import io.github.qishr.cascara.lang.json.ast.JsonProperty;
import io.github.qishr.cascara.lang.json.processor.JsonAstParser;
import io.github.qishr.cascara.lang.json.util.JsonOptions;
import io.github.qishr.cascara.lang.xml.ast.XmlNode;
import io.github.qishr.cascara.lang.xml.processor.XmlAstParser;
import io.github.qishr.cascara.schema.diagnostic.SchemaDiagnosticCode;
import io.github.qishr.cascara.schema.diagnostic.SchemaException;

public class VsixPackage extends ArchiveFile {
    private static final String EXTENSION_DIR = "extension/";
    private static final String IMAGES_DIR = EXTENSION_DIR + "images/";
    private static final String THEMES_DIR = EXTENSION_DIR + "themes/";

    private static final String CHANGELOG_FILENAME = EXTENSION_DIR + "CHANGELOG.md";
    private static final String CONTENT_TYPES_FILENAME = EXTENSION_DIR + "[Content_Types].xml";
    private static final String LICENSE_FILENAME = EXTENSION_DIR + "LICENSE.md";
    private static final String MANIFEST_XML_FILENAME = EXTENSION_DIR + "extension.vsixmanifest";
    private static final String PACKAGE_JSON_FILENAME = EXTENSION_DIR + "package.json";
    private static final String README_FILENAME = EXTENSION_DIR + "README.md";

    private boolean closed = false;
    private VsixMetadata metadata = new VsixMetadata();
    private Properties properties = new Properties();

    private VsixPackage(Path vsixPath, boolean create) throws LocalizableIOException {
        super(vsixPath, create);
    }

    //
    // Static Methods
    //

    public static VsixPackage load(Path vsixPath) throws LocalizableIOException {
        String packageInfo = new String(extractFile(vsixPath, "extension/package.json"));
        String vsixManifest = new String(extractFile(vsixPath, "extension.vsixmanifest"));
        VsixPackage vsix = new VsixPackage(vsixPath, false);
        vsix.parseManifest(vsixManifest);
        vsix.parsePackageManifest(packageInfo);
        return vsix;
    }

    public static VsixPackage create(Path vsixPath) throws LocalizableIOException {
        VsixPackage vsix = new VsixPackage(vsixPath, true);
        return vsix;
    }

    //
    // Getters
    //

    public Path getPath() {
        return archivePath;
    }

    public Properties getProperties() {
        return properties;
    }

    public VsixMetadata getMetadata() {
        return metadata;
    }

    //
    // Optional Files
    //

    public void setChangeLog(Path path) throws LocalizableIOException {
        addFile(path, EXTENSION_DIR + CHANGELOG_FILENAME);
    }

    public void setLicense(Path path) throws LocalizableIOException {
        addFile(path, EXTENSION_DIR + LICENSE_FILENAME);
    }

    public void setReadme(Path path) throws LocalizableIOException {
        addFile(path, EXTENSION_DIR + README_FILENAME);
    }

    //
    // Optional Directories
    //

    public void addThemesFromDirectory(Path path) throws LocalizableIOException {
        addDirectory(path, EXTENSION_DIR + "themes");
        // TOOD: extract metadata from theme JSON into Package JSON
    }

    public void addImagesFromDirectory(Path path) throws LocalizableIOException {
        addDirectory(path, IMAGES_DIR);
    }

    public void addThemeFile(Path path) throws LocalizableIOException {
        addFile(path, THEMES_DIR + path.getFileName());
        // TOOD: extract metadata from theme JSON into Package JSON
    }

    //
    //
    //

	@Override
	public void close() throws Exception {
        if (!closed) {
            flush();
            super.close();
        }
	}

	public void flush() throws LocalizableIOException {
        if (!closed) {
            String contentTypesXmlContent = buildContentTypesXmlContent();
            String manifestXmlContent = buildManifestXmlContent();
            String packageJsonContent = buildPackageJsonContent();
            addFile(contentTypesXmlContent, CONTENT_TYPES_FILENAME);
            addFile(manifestXmlContent, MANIFEST_XML_FILENAME);
            addFile(packageJsonContent, PACKAGE_JSON_FILENAME);
        }
	}

    //
    // Private Methods
    //

    private String buildContentTypesXmlContent() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        sb.append("<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\"><Default Extension=\".json\" ContentType=\"application/json\"/><Default Extension=\".vsixmanifest\" ContentType=\"text/xml\"/><Default Extension=\".md\" ContentType=\"text/markdown\"/></Types>\n");
        return sb.toString();
    }

    private String buildManifestXmlContent() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        sb.append("\t<PackageManifest Version=\"2.0.0\" xmlns=\"http://schemas.microsoft.com/developer/vsx-schema/2011\" xmlns:d=\"http://schemas.microsoft.com/developer/vsx-schema-design/2011\">\n");
        sb.append("\t\t<Metadata>\n");
        sb.append("\t\t\t<Identity Language=\"en-US\" Id=\"cascara\" Version=\"1.0.0\" Publisher=\"qishr\" />\n");
        sb.append("\t\t\t<DisplayName>Cascara Themes</DisplayName>\n");
        sb.append("\t\t\t<Description xml:space=\"preserve\">Cascara Themes</Description>\n");
        sb.append("\t\t\t<Tags>theme,color-theme,__web_extension</Tags>\n");
        sb.append("\t\t\t<Categories>Themes</Categories>\n");
        sb.append("\t\t\t<GalleryFlags>Public</GalleryFlags>\n");
        sb.append("\t\t\t<Properties>\n");
        sb.append("\t\t\t\t<Property Id=\"Microsoft.VisualStudio.Code.Engine\" Value=\"^1.103.0\" />\n");
        sb.append("\t\t\t\t<Property Id=\"Microsoft.VisualStudio.Code.ExtensionDependencies\" Value=\"\" />\n");
        sb.append("\t\t\t\t<Property Id=\"Microsoft.VisualStudio.Code.ExtensionPack\" Value=\"\" />\n");
        sb.append("\t\t\t\t<Property Id=\"Microsoft.VisualStudio.Code.ExtensionKind\" Value=\"ui,workspace,web\" />\n");
        sb.append("\t\t\t\t<Property Id=\"Microsoft.VisualStudio.Code.LocalizedLanguages\" Value=\"\" />\n");
        sb.append("\t\t\t\t<Property Id=\"Microsoft.VisualStudio.Services.GitHubFlavoredMarkdown\" Value=\"true\" />\n");
        sb.append("\t\t\t\t<Property Id=\"Microsoft.VisualStudio.Services.Content.Pricing\" Value=\"Free\"/>\n");
        sb.append("\t\t\t</Properties>\n");
        sb.append("\t\t</Metadata>\n");
        sb.append("\t\t<Installation>\n");
        sb.append("\t\t\t<InstallationTarget Id=\"Microsoft.VisualStudio.Code\"/>\n");
        sb.append("\t\t</Installation>\n");
        sb.append("\t\t<Dependencies/>\n");
        sb.append("\t\t<Assets>\n");
        sb.append("\t\t\t<Asset Type=\"Microsoft.VisualStudio.Code.Manifest\" Path=\"extension/package.json\" Addressable=\"true\" />\n");
        sb.append("\t\t\t<Asset Type=\"Microsoft.VisualStudio.Services.Content.Details\" Path=\"extension/README.md\" Addressable=\"true\" />\n");
        sb.append("\t\t\t<Asset Type=\"Microsoft.VisualStudio.Services.Content.Changelog\" Path=\"extension/CHANGELOG.md\" Addressable=\"true\" />\n");
        sb.append("\t\t</Assets>\n");
        sb.append("\t</PackageManifest>");
        return sb.toString();
    }

    private String buildPackageJsonContent() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"$schema\": \"vscode://schemas/vscode-extensions\",\n");
        sb.append("  \"name\": \"cascara-retro-theme\",\n");
        sb.append("  \"displayName\": \"Cascara Retro Theme\",\n");
        sb.append("  \"description\": \"Cascara Retro Theme\",\n");
        sb.append("  \"version\": \"1.0.2\",\n");
        sb.append("  \"icon\": \"images/icon.png\",\n");
        sb.append("  \"publisher\": \"Cascara\",\n");
        sb.append("  \"engines\": {\n");
        sb.append("    \"vscode\": \"^1.103.0\"\n");
        sb.append("  },\n");
        sb.append("  \"categories\": [\n");
        sb.append("    \"Themes\"\n");
        sb.append("  ],\n");
        sb.append("  \"contributes\": {\n");
        sb.append("    \"themes\": [\n");
        sb.append("      {\n");
        sb.append("        \"label\": \"Cascara Retro Amber on Bright Beige\",\n");
        sb.append("        \"uiTheme\": \"vs-dark\",\n");
        sb.append("        \"path\": \"./themes/retro-amber-on-bright-beige.json\"\n");
        sb.append("      },\n");
        sb.append("      {\n");
        sb.append("        \"label\": \"Cascara Retro Amber on Dark Beige\",\n");
        sb.append("        \"uiTheme\": \"vs-dark\",\n");
        sb.append("        \"path\": \"./themes/retro-amber-on-dull-beige.json\"\n");
        sb.append("      },\n");
        sb.append("      {\n");
        sb.append("        \"label\": \"Cascara Retro Amber on Heavy Metal\",\n");
        sb.append("        \"uiTheme\": \"vs-dark\",\n");
        sb.append("        \"path\": \"./themes/retro-amber-on-heavy-metal.json\"\n");
        sb.append("      },\n");
        sb.append("      {\n");
        sb.append("        \"label\": \"Cascara Retro Green on Bright Beige\",\n");
        sb.append("        \"uiTheme\": \"vs-dark\",\n");
        sb.append("        \"path\": \"./themes/retro-green-on-bright-beige.json\"\n");
        sb.append("      },\n");
        sb.append("      {\n");
        sb.append("        \"label\": \"Cascara Retro Green on Dark Beige\",\n");
        sb.append("        \"uiTheme\": \"vs-dark\",\n");
        sb.append("        \"path\": \"./themes/retro-green-on-dull-beige.json\"\n");
        sb.append("      },\n");
        sb.append("      {\n");
        sb.append("        \"label\": \"Cascara Retro Green on Heavy Metal\",\n");
        sb.append("        \"uiTheme\": \"vs-dark\",\n");
        sb.append("        \"path\": \"./themes/retro-green-on-heavy-metal.json\"\n");
        sb.append("      }\n");
        sb.append("    ]\n");
        sb.append("  },\n");
        sb.append("  \"repository\": {\n");
        sb.append("    \"type\": \"git\",\n");
        sb.append("    \"url\": \"git+https://github.com/sandydunlop/vscode-cascara-retro-theme.git\"\n");
        sb.append("  }\n");
        sb.append("}\n");
        return sb.toString();
    }

    private void parseManifest(String manifest) throws LocalizableIOException {
        if (manifest == null || manifest.isBlank()) return;
        try {
            XmlAstParser XmlAstParser = new XmlAstParser();

            XmlNode xml = XmlAstParser.parse(manifest);
            XmlNode metadataNode = xml.getChild("Metadata");
            XmlNode iconNode = metadataNode.getChild("Icon");
            if (iconNode != null) {
                String iconPath = iconNode.getTextValue();
                properties.set("iconUri", iconPath);
            }
        }catch (Exception e) {
            e.printStackTrace();
            throw new LocalizableRuntimeException(e, GenericDiagnosticCode.ERROR, e.getMessage());
        }
    }

    private void parsePackageManifest(String jsonString) throws LocalizableIOException {
        if (jsonString == null || jsonString.isBlank()) return;
        JsonAstParser JsonAstParser = new JsonAstParser().setOptions(JsonOptions.JSON5);
        JsonObject json;
        JsonNode rootNode = JsonAstParser.parse(jsonString);
        if (rootNode instanceof JsonObject m) {
            json = m;
        } else {
            throw new SchemaException(SchemaDiagnosticCode.ROOT_MUST_BE_MAP);
        }

        for (JsonProperty entry : json.getEntries()) {
            String name = entry.getKey();
            if (entry.getValue() instanceof ScalarAstNode scalar) {
                String value = resolveVariables(scalar.asString());
                metadata.set(name, value);
            } else if (name.equals("categories") && entry.getValue() instanceof JsonArray seq) {
                for (var item : seq) {
                    if (item instanceof ScalarAstNode s) {
                        metadata.getCategories().add(s.asString());
                    }
                }
            } else if (name.equals("contributes") && entry.getValue() instanceof JsonObject contributes) {
                var themesNode = contributes.get("themes");
                if (themesNode instanceof JsonArray themesSeq) {
                    for (var themeEntry : themesSeq) {
                        if (themeEntry instanceof JsonObject themeMap) {
                            VsixThemeInfo themeInfo = new VsixThemeInfo();
                            for (JsonProperty propEntry : themeMap.getEntries()) {
                                String propKey = propEntry.getKey();
                                if (propEntry.getValue() instanceof ScalarAstNode s) {
                                    themeInfo.getProperties().set(propKey, resolveVariables(s.asString()));
                                }
                            }
                            metadata.getThemes().add(themeInfo);
                        }
                    }
                }
            }
        }
    }

    private String resolveVariables(String value) {
        // TODO: Improve this
        if (value.startsWith("%")) {
            if (value.length() > 2) {
                String varName = value.substring(1, value.length() - 1);
                String varValue = properties.getString(varName);
                if (varValue != null) {
                    value = varValue;
                }
            }
        }
        return value;
    }
}
