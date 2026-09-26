package test;

import io.github.qishr.cascara.common.content.type.ContentTypeStore;
import io.github.qishr.cascara.common.service.ServiceProviderLayer;
import io.github.qishr.cascara.common.util.ContentTypeResolver;
import io.github.qishr.cascara.test.common.junit.util.VfsTestBase;

public abstract class CommonIoTestBase extends VfsTestBase {
    protected ContentTypeStore contentTypeStore() {
        ContentTypeResolver resolver = ServiceProviderLayer.loadDefault(ContentTypeResolver.class);
        return (ContentTypeStore) resolver;
    }
}
