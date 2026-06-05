package io.github.qishr.cascara.common.content.type;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.github.qishr.cascara.common.util.ContentTypeResolver;
import io.github.qishr.cascara.common.util.Properties;
import io.github.qishr.cascara.common.diagnostic.NoOpReporter;
import io.github.qishr.cascara.common.diagnostic.Reporter;
import io.github.qishr.cascara.common.util.ContentType;
import io.github.qishr.cascara.common.data.Table;
import io.github.qishr.cascara.lang.yaml.processor.YamlSerializer;

public class ContentTypeStore implements ContentTypeResolver {
    private static final Path cascaraDir = Paths.get(System.getProperty("user.home")).resolve(".cascara");
    private static final Path registryPath = cascaraDir.resolve("canonical-content-types.yaml");

    private static ContentTypeStore instance;
    private static Properties serviceProperties;

    private Reporter reporter = new NoOpReporter();
    private ContentTypeRegistry contentTypeRegistry;

    private ContentTypeStore() {
        init();
    }

    @Override
    public Properties getServiceProperties() {
        if (serviceProperties == null) {
            serviceProperties = new Properties();
        }
        return serviceProperties;
    }

    private void init() {
        YamlSerializer serializer = new YamlSerializer();
        if (Files.exists(registryPath)) {
            String yamlContent;
            try {
                yamlContent = Files.readString(registryPath);
            } catch (IOException e) {
                throw new ContentTypeException("Failed to read content type registry", e);
            }
            contentTypeRegistry = serializer.fromText(yamlContent, ContentTypeRegistry.class);
        } else {
            contentTypeRegistry = new ContentTypeRegistry();
        }
    }

    public static ContentTypeStore instance() {
        if (instance == null) {
            instance = new ContentTypeStore();
            // instance.init();
        }
        return instance;
    }

    public void setReporter(Reporter reporter) {
        if (reporter == null) {
            this.reporter = new NoOpReporter();
        } else {
            this.reporter = reporter;
        }
    }

    public List<ContentType> getAll() throws ContentTypeException {
        return contentTypeRegistry.getRecords();
    }

    public void addAll(List<? extends ContentType> contentTypes) {
        List<ContentType> allContentTypes = new ArrayList<>(contentTypeRegistry.getRecords());
        for (ContentType type : contentTypes) {
            allContentTypes.add(type);
        }
        normalizeAndPersist(allContentTypes);
    }

    public void add(ContentType contentType) {
        List<ContentType> allContentTypes = new ArrayList<>(contentTypeRegistry.getRecords());
        allContentTypes.add(contentType);
        normalizeAndPersist(allContentTypes);
    }

    public ContentType resolve(String type) {
        for (ContentType contentType : contentTypeRegistry.getRecords()) {
            if (contentType.getCanonicalId().equals(type)
                || contentType.getName().equalsIgnoreCase(type)
                || contentType.getMimeTypes().contains(type)
                || contentType.getSuffixes().contains(type)
            ){
                return contentType;
            }
        }
        return null;
    }

    //
    //
    //

    private void normalizeAndPersist(List<ContentType> allContentTypes) {
        YamlSerializer serializer = new YamlSerializer();
        ContentTypeNormalizer normalizer = new ContentTypeNormalizer();
        normalizer.setReporter(reporter);
        List<MergedContentType> types = normalizer.normalize(allContentTypes);
        reconcile(types, contentTypeRegistry);

        debugOutputTypes(contentTypeRegistry.getRecords());

        String yamlContent = serializer.toText(contentTypeRegistry);
        try {
            Files.writeString(registryPath, yamlContent);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ContentTypeException("Failed to update content type registry", e);
        }
    }

    public void reconcile(List<MergedContentType> types, ContentTypeRegistry registry) {
        List<ContentType> updated = new ArrayList<>();

        for (MergedContentType group : types) {

            ContentType match = findMatch(group, registry);

            if (match != null) {
                // Reuse canonical ID and name
                group.setCanonicalId(match.getCanonicalId());
                group.setName(match.getName());

                // Update registry record with any new MIME types or suffixes
                mergeLists(match.getMimeTypes(), group.getMimeTypes());
                mergeLists(match.getSuffixes(), group.getSuffixes());

                updated.add(match);
            } else {
                // New canonical type
                ContentType rec = new ContentType();
                rec.setCanonicalId(group.getCanonicalId());
                rec.setName(group.getName());
                // rec.moduleId = group.getModuleId(); // TODO
                rec.getMimeTypes().addAll(group.getMimeTypes());
                rec.getSuffixes().addAll(group.getSuffixes());

                updated.add(rec);
            }
        }

        // Replace registry contents
        registry.setRecords(updated);
    }

    private ContentType findMatch(
        MergedContentType group,
        ContentTypeRegistry registry
    ) {
        for (ContentType rec : registry.getRecords()) {

            // Strong match: identical MIME sets
            if (rec.getMimeTypes().containsAll(group.getMimeTypes()) &&
                group.getMimeTypes().containsAll(rec.getMimeTypes())) {
                return rec;
            }

            // Weak match: any MIME overlap
            for (String mime : group.getMimeTypes()) {
                if (rec.getMimeTypes().contains(mime)) {
                    return rec;
                }
            }

            // Weak match: any suffix overlap
            for (String suf : group.getSuffixes()) {
                if (rec.getSuffixes().contains(suf)) {
                    return rec;
                }
            }
        }
        return null;
    }

    private void mergeLists(List<String> target, Set<String> source) {
        for (String item : source) {
            if (!target.contains(item)) {
                target.add(item);
            }
        }
    }

    //
    // Diagnostics
    //

    public void debugOutputTypes(List<ContentType> types) {
        Table table = new Table();
        table.addColumn("Canonical ID");
        table.addColumn("Name");
        for (ContentType type : types) {
            table.addRow(type.getCanonicalId(), type.getName());
        }
        try (StringWriter writer = new StringWriter()){
            table.render(writer);
            reporter.debug("Canonical Content Types\n" + writer.toString());
        } catch (IOException e) {
            reporter.error(null, "Failed to write debug output: " + e.getMessage());
        }
    }
}
