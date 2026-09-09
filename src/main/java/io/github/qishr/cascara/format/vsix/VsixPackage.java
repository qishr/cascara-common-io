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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.qishr.cascara.common.diagnostic.LocalizableIOException;
import io.github.qishr.cascara.common.diagnostic.LocalizableRuntimeException;
import io.github.qishr.cascara.common.diagnostic.code.GenericDiagnosticCode;
import io.github.qishr.cascara.common.util.ArchiveFile;
import io.github.qishr.cascara.lang.json.ast.JsonNode;
import io.github.qishr.cascara.lang.json.ast.JsonObject;
import io.github.qishr.cascara.lang.json.processor.JsonAstParser;
import io.github.qishr.cascara.lang.json.processor.JsonSerializer;
import io.github.qishr.cascara.lang.json.util.JsonOptions;
import io.github.qishr.cascara.lang.xml.ast.XmlNode;
import io.github.qishr.cascara.lang.xml.processor.XmlAstParser;
import io.github.qishr.cascara.schema.diagnostic.SchemaDiagnosticCode;
import io.github.qishr.cascara.schema.diagnostic.SchemaException;

public class VsixPackage extends ArchiveFile {
    private static final String EXTENSION_DIR = "extension/";
    private static final String IMAGES_DIR = EXTENSION_DIR + "images/";
    private static final String THEMES_DIR = EXTENSION_DIR + "themes/";

    private static final String CHANGELOG_ENTRY = EXTENSION_DIR + "CHANGELOG.md";
    private static final String CONTENT_TYPES_ENTRY = EXTENSION_DIR + "[Content_Types].xml";
    private static final String LICENSE_ENTRY = EXTENSION_DIR + "LICENSE.md";
    private static final String MANIFEST_XML_ENTRY = EXTENSION_DIR + "extension.vsixmanifest";
    private static final String PACKAGE_JSON_ENTRY = EXTENSION_DIR + "package.json";
    private static final String README_ENTRY = EXTENSION_DIR + "README.md";

    private boolean closed = false;
    // private VsixMetadata metadata = new VsixMetadata();
    private PackageJsonFile pkgJsonFile;
    private Set<String> optionalFiles = new HashSet<>();
    private Map<String,VsixThemeInfo> themes = new HashMap<>();

    @FunctionalInterface
    private interface SpecialFileHandler {
        void handle(Path sourcePath) throws LocalizableIOException;
    }

    @FunctionalInterface
    private interface SpecialContentHandler {
        void handle(String content) throws LocalizableIOException;
    }

    @FunctionalInterface
    private interface WildcardFileHandler {
        void handle(Path sourcePath, String entryName) throws LocalizableIOException;
    }

    @FunctionalInterface
    private interface WildcardContentHandler {
        void handle(String content, String entryName) throws LocalizableIOException;
    }


    private final Map<String, SpecialFileHandler> fileHandlers = Map.of(
        PACKAGE_JSON_ENTRY, this::addPackageJsonFile,
        MANIFEST_XML_ENTRY, this::addManifestXmlFile
    );

    private final Map<String, SpecialContentHandler> contentHandlers = Map.of(
        PACKAGE_JSON_ENTRY, this::setPackageJsonContent,
        MANIFEST_XML_ENTRY, this::setManifestXmlContent
    );

    private final Map<String, WildcardContentHandler> wildcardContentHandlers = Map.of(
        THEMES_DIR, this::addThemeContent
    );

    private final Map<String, WildcardFileHandler> wildcardFileHandlers = Map.of(
        THEMES_DIR, this::addThemeFile
    );

    private VsixPackage(Path vsixPath, boolean create) throws LocalizableIOException {
        super(vsixPath, create);
    }

    //
    // Static Methods
    //

    public static VsixPackage load(Path vsixPath) throws LocalizableIOException {
        String packageInfo = new String(extractFile(vsixPath, PACKAGE_JSON_ENTRY));
        String vsixManifest = new String(extractFile(vsixPath, MANIFEST_XML_ENTRY));
        VsixPackage vsix = new VsixPackage(vsixPath, false);
        vsix.parseManifestXml(vsixManifest);
        vsix.parsePackageManifest(packageInfo);
        return vsix;
    }

    public static VsixPackage create(Path vsixPath) throws LocalizableIOException {
        VsixPackage vsix = new VsixPackage(vsixPath, true);
        vsix.pkgJsonFile = new PackageJsonFile();
        return vsix;
    }

    //
    // Extract and Add Interceptors
    //

    @Override
    public byte[] extractFile(String entryName) {
        // Return the virtual package.json as the actual file isn't written until close/flush time.
        if (entryName.equals(PACKAGE_JSON_ENTRY)) {
            return getPackageJsonContent().getBytes();
        } else {
            return super.extractFile(entryName);
        }
    }

    @Override
    public void addFile(Path sourcePath, String entryName) throws LocalizableIOException {
        String folderName = folderName(entryName);
        WildcardFileHandler wildcardHandler = wildcardFileHandlers.get(folderName);
        if (wildcardHandler != null) {
            wildcardHandler.handle(sourcePath, entryName);
            return;
        }
        SpecialFileHandler fileHandler = fileHandlers.get(entryName);
        if (fileHandler != null) {
            fileHandler.handle(sourcePath);
        } else {
            super.addFile(sourcePath, entryName);
        }
    }

    @Override
    public void addFile(String content, String entryName) throws LocalizableIOException {
        String folderName = folderName(entryName);
        WildcardContentHandler wildcardHandler = wildcardContentHandlers.get(folderName);
        if (wildcardHandler != null) {
            wildcardHandler.handle(content, entryName);
            return;
        }
        SpecialContentHandler contentHandler = contentHandlers.get(entryName);
        if (contentHandler != null) {
            contentHandler.handle(content);
        } else {
            super.addFile(content, entryName);
        }
    }

    private String folderName(String entryName) {
        int p = entryName.lastIndexOf('/');
        if (p > 0) {
            return entryName.substring(0, p + 1);
        }
        return "";
    }

    //
    // Getters and Setters
    //

    public String getName() {
        return pkgJsonFile.getName();
    }

    public VsixPackage setName(String s) {
        pkgJsonFile.setName(s);
        return this;
    }

    public String getDisplayName() {
        return pkgJsonFile.getDisplayName();
    }

    public VsixPackage setDisplayName(String s) {
        pkgJsonFile.setDisplayName(s);
        return this;
    }

    public String getVersion() {
        return pkgJsonFile.getVersion();
    }

    public VsixPackage setVersion(String s) {
        pkgJsonFile.setVersion(s);
        return this;
    }

    public String getDescription() {
        return pkgJsonFile.getDescription();
    }

    public VsixPackage setDescription(String s) {
        pkgJsonFile.setDescription(s);
        return this;
    }

    public String getPublisher() {
        return pkgJsonFile.getPublisher();
    }

    public VsixPackage setPublisher(String s) {
        pkgJsonFile.setPublisher(s);
        return this;
    }

    public String getIcon() {
        return pkgJsonFile.getIcon();
    }

    public VsixPackage setIcon(String s) {
        pkgJsonFile.setIcon(s);
        return this;
    }

    public RepositoryInfo getRepository() {
        return pkgJsonFile.getRepository();
        // if (repository == null) {
        //     repository = new RepositoryInfo();
        // }
        // return repository;
    }

    public VsixPackage setRepository(RepositoryInfo o) {
        pkgJsonFile.setRepository(o);
        return this;
    }

    //
    // Optional Files
    //

    public void setChangeLog(Path path) throws LocalizableIOException {
        addFile(path, CHANGELOG_ENTRY);
    }

    public void setLicense(Path path) throws LocalizableIOException {
        addFile(path, LICENSE_ENTRY);
    }

    public void setReadme(Path path) throws LocalizableIOException {
        addFile(path, README_ENTRY);
    }

    //
    // Themes and Images
    //

    public List<VsixThemeInfo> getThemes() {
        return themes.values().stream()
            .collect(Collectors.toList());
    }

    public void addThemesFromDirectory(Path sourcePath) throws LocalizableIOException {
        List<LocalizableIOException> exceptions = new ArrayList<>();
        walk(sourcePath, THEMES_DIR, exceptions, (source, entryPath) -> addThemeNoException(source, entryPath, exceptions));
        if (!exceptions.isEmpty()) {
            throw exceptions.getFirst();
        }
    }

    public void addImagesFromDirectory(Path path) throws LocalizableIOException {
        addDirectory(path, IMAGES_DIR);
    }

    public void addThemeFile(Path path, String entryName) throws LocalizableIOException {
        super.addFile(path, THEMES_DIR + path.getFileName());
        // extract metadata from theme JSON into Package JSON
        String jsonString;
		try {
			jsonString = Files.readString(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            return;
		}
        extractUiThemeMetadata(jsonString);
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
            enumerateOptionalFiles();
            String contentTypesXmlContent = getContentTypesXmlContent();
            String manifestXmlContent = getManifestXmlContent();
            String packageJsonContent = getPackageJsonContent();
            addFile(contentTypesXmlContent, CONTENT_TYPES_ENTRY);
            addFile(manifestXmlContent, MANIFEST_XML_ENTRY);
            addFile(packageJsonContent, PACKAGE_JSON_ENTRY);
        }
	}

    //
    // Themes
    //

    private void addThemeContent(String content, String entryName)  throws LocalizableIOException {
        // TODO: Add the actual file
        extractUiThemeMetadata(content);
    }

    private void extractUiThemeMetadata(String jsonString) {
        JsonAstParser jsonAstParser = new JsonAstParser().setOptions(JsonOptions.JSON5);
        JsonNode rootNode = jsonAstParser.parse(jsonString);
        if (rootNode instanceof JsonObject rootObject) {
            String name = rootObject.getString("name");
            String type = rootObject.getString("type");
            boolean semanticHighlighting = rootObject.getBoolean("semanticHighlighting");

            VsixThemeInfo theme = new VsixThemeInfo();

            theme.setName(name);
            theme.setType(type);
            theme.setSemanticHighlighting(semanticHighlighting);

            themes.put(name, theme);

        } else {
            throw new SchemaException(SchemaDiagnosticCode.ROOT_MUST_BE_MAP);
        }
    }

    private void addThemeNoException(Path sourcePath, Path entryPath, List<LocalizableIOException> exceptions) {
        try {
            addThemeFile(sourcePath, null);
        } catch (LocalizableIOException e) {
            exceptions.add(e);
        }
    }

    //
    // package.json methods
    //

    private void addPackageJsonFile(Path sourcePath) throws LocalizableIOException {
        try {
			String content = Files.readString(sourcePath);
            setPackageJsonContent(content);
		} catch (IOException e) {
            throw new LocalizableIOException(e, GenericDiagnosticCode.IO_ERROR, e.getMessage());
		}
    }

    private void setPackageJsonContent(String content) throws LocalizableIOException {
        parsePackageManifest(content);
    }

    private String getPackageJsonContent() {
        JsonSerializer serializer = new JsonSerializer();
        return serializer.toString(pkgJsonFile);
    }

    private void parsePackageManifest(String jsonString) throws LocalizableIOException {
        if (jsonString == null || jsonString.isBlank()) return;
        JsonSerializer serializer = new JsonSerializer();
        pkgJsonFile = serializer.fromString(jsonString, PackageJsonFile.class);
    }

    //
    // XML Manifest methods
    //

    private void addManifestXmlFile(Path sourcePath) throws LocalizableIOException {
        // TODO
        System.out.println("TODO");
    }

    private void setManifestXmlContent(String content) throws LocalizableIOException {
        parseManifestXml(content);
    }

    private String getManifestXmlContent() {
        // TODO
        System.out.println("TODO");
        return _getManifestXmlContent();
    }

    private void parseManifestXml(String manifest) throws LocalizableIOException {
        if (manifest == null || manifest.isBlank()) return;
        try {
            XmlAstParser XmlAstParser = new XmlAstParser();
            XmlNode xml = XmlAstParser.parse(manifest);
            XmlNode metadataNode = xml.getChild("Metadata");
            XmlNode iconNode = metadataNode.getChild("Icon");
            if (iconNode != null) {
                String iconPath = iconNode.getTextValue();
                setIcon(iconPath);
            }
        }catch (Exception e) {
            e.printStackTrace();
            throw new LocalizableRuntimeException(e, GenericDiagnosticCode.ERROR, e.getMessage());
        }
    }

    // TODO: This needs to be dynamic
    private String _getManifestXmlContent() {
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

        if (optionalFiles.contains(README_ENTRY)) {
            sb.append("\t\t\t<Asset Type=\"Microsoft.VisualStudio.Services.Content.Details\" Path=\"extension/README.md\" Addressable=\"true\" />\n");
        }

        if (optionalFiles.contains(CHANGELOG_ENTRY)) {
            sb.append("\t\t\t<Asset Type=\"Microsoft.VisualStudio.Services.Content.Changelog\" Path=\"extension/CHANGELOG.md\" Addressable=\"true\" />\n");
        }

        sb.append("\t\t</Assets>\n");
        sb.append("\t</PackageManifest>");
        return sb.toString();
    }

    //
    // Content Types
    //

    private String getContentTypesXmlContent() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        sb.append("<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\"><Default Extension=\".json\" ContentType=\"application/json\"/><Default Extension=\".vsixmanifest\" ContentType=\"text/xml\"/><Default Extension=\".md\" ContentType=\"text/markdown\"/></Types>\n");
        return sb.toString();
    }

    //
    // Helpers
    //

    private void enumerateOptionalFiles() {
        optionalFiles.clear();
        try {
			List<EntryInfo> files = listFiles();
            for (EntryInfo file : files) {
                if (file.getPath().equals(README_ENTRY)) {
                    optionalFiles.add(README_ENTRY);
                }
                if (file.getPath().equals(CHANGELOG_ENTRY)) {
                    optionalFiles.add(CHANGELOG_ENTRY);
                }
                if (file.getPath().equals(LICENSE_ENTRY)) {
                    optionalFiles.add(LICENSE_ENTRY);
                }
            }
		} catch (LocalizableIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    //
    // Old code.
    // TODO: What was resolveVariables for?
    //

    // private void _parsePackageManifest(String jsonString) throws LocalizableIOException {
    //     if (jsonString == null || jsonString.isBlank()) return;


    //     Properties properties = new Properties();
    //     JsonAstParser jsonAstParser = new JsonAstParser().setOptions(JsonOptions.JSON5);
    //     JsonObject json;
    //     JsonNode doc = jsonAstParser.parse(jsonString);
    //     if (!(doc instanceof JsonObject root)) {
    //         throw new SchemaException(SchemaDiagnosticCode.ROOT_MUST_BE_MAP);
    //     }

    //     setName(root.getString("name"));

    //     JsonArray categories = root.getArray("categories");
    //     if (categories != null && !categories.isEmpty()) {
    //         for (JsonNode catNode : categories) {
    //             if (catNode instanceof JsonScalar scalar) {
    //                 String catName = scalar.asString();
    //                 // todo

    //             }
    //         }
    //     }

    //     // if (rootNode instanceof JsonObject m) {
    //     //     json = m;
    //     // } else {
    //     //     throw new SchemaException(SchemaDiagnosticCode.ROOT_MUST_BE_MAP);
    //     // }

    //     // for (JsonProperty entry : json.getEntries()) {
    //     //     String name = entry.getKey();
    //     //     if (entry.getValue() instanceof ScalarAstNode scalar) {


    //     //         // todo: Test this
    //     //         String value = resolveVariables(scalar.asString(), properties);
    //     //         metadata.set(name, value);


    //     //     } else if (name.equals("categories") && entry.getValue() instanceof JsonArray seq) {
    //     //         for (var item : seq) {
    //     //             if (item instanceof ScalarAstNode s) {
    //     //                 metadata.getCategories().add(s.asString());
    //     //             }
    //     //         }
    //     //     } else if (name.equals("contributes") && entry.getValue() instanceof JsonObject contributes) {
    //     //         var themesNode = contributes.get("themes");
    //     //         if (themesNode instanceof JsonArray themesSeq) {
    //     //             for (var themeEntry : themesSeq) {
    //     //                 if (themeEntry instanceof JsonObject themeMap) {
    //     //                     VsixThemeInfo themeInfo = new VsixThemeInfo();
    //     //                     for (JsonProperty propEntry : themeMap.getEntries()) {
    //     //                         String propKey = propEntry.getKey();
    //     //                         if (propEntry.getValue() instanceof ScalarAstNode s) {
    //     //                             themeInfo.getProperties().set(propKey, resolveVariables(s.asString(), properties));
    //     //                         }
    //     //                     }
    //     //                     metadata.getThemes().add(themeInfo);
    //     //                 }
    //     //             }
    //     //         }
    //     //     }
    //     // }
    // }

    // private String resolveVariables(String value, Properties properties) {
    //     // todo: Improve this
    //     if (value.startsWith("%")) {
    //         if (value.length() > 2) {
    //             String varName = value.substring(1, value.length() - 1);
    //             String varValue = properties.getString(varName);
    //             if (varValue != null) {
    //                 value = varValue;
    //             }
    //         }
    //     }
    //     return value;
    // }
}
