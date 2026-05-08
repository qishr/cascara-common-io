package io.github.qishr.cascara.common.content.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.qishr.cascara.common.diagnostic.SimpleReporter;
import io.github.qishr.cascara.common.diagnostic.Diagnostic.Level;
import io.github.qishr.cascara.common.util.ContentType;

public class NormalizerTests {
    @Test
    void noDuplicateTextType() {
        List<ContentType> contentTypes = List.of(
            new ContentType("Text")
                .withSuffix(".text")
                .withMimeType("text/*"),
            new ContentType("Text")
                .withSuffix(".text")
                .withMimeType("text/plain")
        );

        ContentTypeRegistry registry = new ContentTypeRegistry();
        ContentTypeNormalizer normalizer = new ContentTypeNormalizer();
        normalizer.setReporter(new SimpleReporter().setLevel(Level.DEBUG));

        List<MergedContentType> merged = normalizer.normalize(contentTypes);
        ContentTypes.reconcile(merged, registry);

        ContentTypes.debugOutputTypes(registry.getRecords());

        assertEquals(1, merged.size());
    }

    @Test
    void noTextTypeWithWildcard() {
        List<ContentType> contentTypes = List.of(
            new ContentType("Text")
                .withSuffix(".text")
                .withMimeType("text/*")
                .withMimeType("text/plain")
        );

        ContentTypeRegistry registry = new ContentTypeRegistry();
        ContentTypeNormalizer normalizer = new ContentTypeNormalizer();
        normalizer.setReporter(new SimpleReporter().setLevel(Level.DEBUG));

        List<MergedContentType> merged = normalizer.normalize(contentTypes);
        ContentTypes.reconcile(merged, registry);

        ContentTypes.debugOutputTypes(registry.getRecords());

        assertEquals(1, merged.size());
    }
}
