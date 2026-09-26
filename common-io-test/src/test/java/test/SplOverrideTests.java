package test;

import io.github.qishr.cascara.common.util.Cascara;
import io.github.qishr.cascara.common.util.ContentTypeResolver;
import io.github.qishr.cascara.common.io.provider.ResourceProvider;
import io.github.qishr.cascara.common.service.ServiceMetadata;
import io.github.qishr.cascara.common.service.ServiceProviderLayer;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SplOverrideTests extends CommonIoTestBase {

    @Test
    void test_modulesLoad() throws IOException {
        ServiceProviderLayer rootLayer = ServiceProviderLayer.getRoot();
        List<String> modules = rootLayer.getModules();
        assertTrue(modules.contains("cascara.common.io"));
    }

    @Test
    void test_splPrefsOverridesSystemIoProvider() throws IOException {
        spl.registerClass(TestContentTypeStore.class);

        Path propsFile = Cascara.getSplPropertiesPath();
        Files.writeString(
            propsFile,
            TestContentTypeStore.class.getName() + "=" +
            TestContentTypeStore.class.getName() + "\n",
            StandardOpenOption.TRUNCATE_EXISTING,
            StandardOpenOption.CREATE
        );

        ResourceProvider provider = ServiceProviderLayer.loadDefault(ResourceProvider.class);
        assertNotNull(provider);

        // TODO: Verify the provider works
    }

    @Test
    void test_splPrefsOverridesContentTypeStore() throws IOException {
        spl.registerClass(TestContentTypeStore.class);

        // Verify Alternative CTS is present
        List<ServiceMetadata> providers = spl.findAllProviders(ContentTypeResolver.class);
        assertEquals(2, providers.size());

        boolean found = false;
        for (ServiceMetadata m : providers) {
            if (m.getType().equals(TestContentTypeStore.class)) {
                found = true;
            }
        }

        assertTrue(found);
    }
}
