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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.*;

import io.github.qishr.cascara.common.util.ArchiveFile;
import io.github.qishr.cascara.common.io.IOUtils;
import io.github.qishr.cascara.common.util.Properties;
import io.github.qishr.cascara.common.content.ResourceContent;
import io.github.qishr.cascara.common.diagnostic.LocalizableIOException;
import io.github.qishr.cascara.common.diagnostic.LocalizableRuntimeException;
import io.github.qishr.cascara.common.diagnostic.code.GenericDiagnosticCode;
import io.github.qishr.cascara.common.lang.ast.ScalarAstNode;
import io.github.qishr.cascara.lang.json.processor.JsonAstParser;
import io.github.qishr.cascara.lang.json.util.JsonOptions;
import io.github.qishr.cascara.lang.json.ast.JsonProperty;
import io.github.qishr.cascara.lang.json.ast.JsonObject;
import io.github.qishr.cascara.lang.json.ast.JsonNode;
import io.github.qishr.cascara.lang.json.ast.JsonArray;
import io.github.qishr.cascara.lang.xml.processor.XmlAstParser;
import io.github.qishr.cascara.schema.diagnostic.SchemaDiagnosticCode;
import io.github.qishr.cascara.schema.diagnostic.SchemaException;
import io.github.qishr.cascara.lang.xml.ast.XmlNode;

public class VsixPackage extends ArchiveFile {
    private Properties properties = new Properties();
    private Properties manifest = new Properties();
    private List<String> categories = new ArrayList<>();
    private List<VsixThemeInfo> themes = new ArrayList<>();
    private URI downloadOrigin = null;
    private URI previewUri = null;

    public static VsixPackage load(Path vsixPath) throws LocalizableIOException {
        String packageInfo = new String(extractFile(vsixPath, "extension/package.json"));
        String vsixManifest = new String(extractFile(vsixPath, "extension.vsixmanifest"));
        VsixPackage vsix = new VsixPackage(vsixPath);
        vsix.parseManifest(vsixManifest);
        vsix.parsePackageManifest(packageInfo);
        return vsix;
    }

    public static VsixPackage create(Path vsixPath) throws LocalizableIOException {
        VsixPackage vsix = new VsixPackage(vsixPath);
        return vsix;
    }

    private VsixPackage(Path vsixPath) {
        super(vsixPath);
    }

    public static VsixPackage fromJson(String jsonString) {
        JsonAstParser JsonAstParser = new JsonAstParser().setOptions(JsonOptions.JSON5);
        JsonObject json = null;
        if (JsonAstParser.parse(jsonString) instanceof JsonObject m) {
            json = m;
        } else {
            throw new SchemaException(SchemaDiagnosticCode.ROOT_MUST_BE_MAP);
        }

        // Use getEntries() from MappingAstNode
        io.github.qishr.cascara.lang.json.ast.JsonNode extensionFiles =
            (io.github.qishr.cascara.lang.json.ast.JsonNode) json.get("files");

        VsixPackage vsix = new VsixPackage(null);

        if (extensionFiles instanceof JsonObject filesMap) {
            vsix.getProperties().set("iconUri", getPropertyAsString(filesMap, "icon"));
            vsix.getProperties().set("manifestUri", getPropertyAsString(filesMap, "manifest"));
            vsix.getProperties().set("readmeUri", getPropertyAsString(filesMap, "readme"));
            vsix.getProperties().set("changelogUri", getPropertyAsString(filesMap, "changelog"));
            vsix.getProperties().set("licenseUri", getPropertyAsString(filesMap, "license"));
            vsix.getProperties().set("signatureUri", getPropertyAsString(filesMap, "signature"));
            vsix.getProperties().set("sha256uri", getPropertyAsString(filesMap, "sha256"));
            vsix.getProperties().set("publickeyUri", getPropertyAsString(filesMap, "publicKey"));
            vsix.getProperties().set("vsixmanifestUri", getPropertyAsString(filesMap, "vsixmanifest"));

            try {
                var downloadNode = filesMap.get("download");
                if (downloadNode instanceof ScalarAstNode scalar) {
                    vsix.downloadOrigin = new URI(scalar.asString());
                }
            } catch (URISyntaxException e) {
                System.err.println("Invalid download URL in package file: " + e.getMessage());
            }
        }

        vsix.getProperties().set("name", getPropertyAsString(json, "name"));
        vsix.getProperties().set("displayName", getPropertyAsString(json, "displayName"));
        //TODO: The rest

        try {
            // Get package info...
            String manifestUri = vsix.getProperties().getString("manifestUri");
            ResourceContent manifest = IOUtils.getResource(URI.create(manifestUri));
            vsix.parsePackageManifest(manifest.content());
        } catch (IOException e) {
            throw new LocalizableRuntimeException(e, GenericDiagnosticCode.ERROR, "Error parsing VSIX from URL: " + e.getMessage(), e);
        }

        return vsix;
    }

    // High-level method accepting raw strings/bytes instead of JSON/XML models
    public void addManifest(String manifestXmlContent) throws LocalizableIOException {
        addFile(manifestXmlContent, "extension.vsixmanifest");
    }

    public void addContentTypes(String contentTypesXmlContent) throws LocalizableIOException {
        addFile(contentTypesXmlContent, "[Content_Types].xml");
    }

    public void addPackageJson(String packageJsonContent) throws LocalizableIOException {
        addFile(packageJsonContent, "extension/package.json");
    }

    public URI getDownloadUri() {
        return downloadOrigin;
    }

    public URI getPreviewUri() {
        return previewUri;
    }

    public String getName() {
        if (manifest.getString("name") instanceof String s) {
            return s;
        }
        return properties.getString("name");
    }

    public String getDescription() {
        if (manifest.getString("description") instanceof String s) {
            return s;
        }
        return properties.getString("description");
    }

    public Path getPath() {
        return archivePath;
    }

    public Properties getProperties() {
        return properties;
    }

    public Properties getManifest() {
        return manifest;
    }

    public String getIconUri() {
        if (manifest.getString("iconUri") instanceof String s) {
            return s;
        }
        return properties.getString("iconUri");
    }

    public String getDisplayName() {
        if (manifest.getString("displayName") instanceof String s) {
            return s;
        }
        return properties.getString("displayName");
    }

    // TODO: Other getters for properties

    public List<String> getCategories() {
        return categories;
    }

    public List<VsixThemeInfo> getThemes() {
        return themes;
    }

    //
    //
    //

    private static String getPropertyAsString(JsonObject node, String name) {
        if (node == null) return "";
        var valueNode = node.get(name);
        if (valueNode instanceof ScalarAstNode scalar) {
            return scalar.asString();
        }
        return "";
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
                manifest.set(name, value);
            } else if (name.equals("categories") && entry.getValue() instanceof JsonArray seq) {
                for (var item : seq) {
                    if (item instanceof ScalarAstNode s) getCategories().add(s.asString());
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
                            getThemes().add(themeInfo);
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

	@Override
	public void close() throws Exception {
        super.close();
	}
}
