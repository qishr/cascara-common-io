package io.github.qishr.cascara.common.content.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.qishr.cascara.common.diagnostic.StandardReporter;
import io.github.qishr.cascara.common.diagnostic.Diagnostic.Level;
import io.github.qishr.cascara.common.util.ContentType;

public class NormalizerTests {
    @Test
    void noDuplicateTextType() {
        List<ContentType> contentTypes = List.of(
            new ContentType("Text")
                .withSuffix(".text")
                .withType("text/*"),
            new ContentType("Text")
                .withSuffix(".text")
                .withType("text/plain")
        );

        ContentTypeRegistry registry = new ContentTypeRegistry();
        ContentTypeNormalizer normalizer = new ContentTypeNormalizer();
        normalizer.setReporter(new StandardReporter().setLevel(Level.DEBUG));

        List<MergedContentType> merged = normalizer.normalize(contentTypes);
        ContentTypeStore.instance().reconcile(merged, registry);

        ContentTypeStore.instance().debugOutputTypes(registry.getRecords());

        assertEquals(1, merged.size());
    }

    @Test
    void noTextTypeWithWildcard() {
        List<ContentType> contentTypes = List.of(
            new ContentType("Text")
                .withSuffix(".text")
                .withType("text/*")
                .withType("text/plain")
        );

        ContentTypeRegistry registry = new ContentTypeRegistry();
        ContentTypeNormalizer normalizer = new ContentTypeNormalizer();
        normalizer.setReporter(new StandardReporter().setLevel(Level.DEBUG));

        List<MergedContentType> merged = normalizer.normalize(contentTypes);
        ContentTypeStore.instance().reconcile(merged, registry);

        ContentTypeStore.instance().debugOutputTypes(registry.getRecords());

        assertEquals(1, merged.size());
    }
}
