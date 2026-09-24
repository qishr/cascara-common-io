package io.github.qishr.test.common.io;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import io.github.qishr.cascara.common.util.Cascara;
import io.github.qishr.cascara.test.shared.util.TestModulePackager;
import io.github.qishr.cascara.test.shared.util.VfsTestBase;

public abstract class CommonIoTestBase extends VfsTestBase {
    // private static final Level SPL_REPORTING_LEVEL = Level.DEBUG;
    // private static final Level TEST_REPORTING_LEVEL = Level.DEBUG;


    protected Path createCascaraIoProvider() throws IOException {
        Path providerAJar = Cascara.getModulePath().resolve("provider-a.jar");

        // Synthetic Module A with explicit module-info
        TestModulePackager.createSyntheticModuleJar(
            providerAJar,
            "0.10.0",
            Map.of(
                "module-info",
                "module provider.a { " +
                "    requires cascara.common; " +
                "    requires cascara.test.shared; " +
                "    exports com.example.providera; " +
                "    opens com.example.providera to cascara.common; " +
                "    provides io.github.qishr.cascara.common.service.ServiceProvider " +
                "        with com.example.providera.ProviderA;" +
                "}",

                "com.example.providera.ProviderA",
                "package com.example.providera; " +
                "import io.github.qishr.cascara.test.shared.service.TestService; " +
                "import io.github.qishr.cascara.common.property.Properties; " +
                "public class ProviderA implements TestService { " +
                "    public String getName() { return \"ProviderA\"; } " +
                "    public Properties getServiceProperties() { return null; } " +
                "}"
            ),
            List.of("cascara-common", "cti-shared"),
            "common-io-test"
        );
        return providerAJar;
    }

    protected Path createAlternativeCTS() throws IOException {
        Path providerBJar = Cascara.getModulePath().resolve("provider-b.jar");
        // Synthetic Module B with explicit module-info
        TestModulePackager.createSyntheticModuleJar(
            providerBJar,
            "0.10.0",
            Map.of(
                "module-info",
                "module provider.b { " +
                "    requires cascara.common; " +
                "    requires cascara.test.shared; " +
                "    exports com.example.providerb; " +
                "    opens com.example.providerb to cascara.common; " +
                "    provides io.github.qishr.cascara.common.service.ServiceProvider " +
                "        with com.example.providerb.ProviderB;" +
                "}",

                "com.example.providerb.ProviderB",
                "package com.example.providerb; " +
                "import io.github.qishr.cascara.test.shared.service.TestService; " +
                "import io.github.qishr.cascara.common.property.Properties; " +
                "public class ProviderB implements TestService { " +
                "    public String getName() { return \"ProviderB\"; } " +
                "    public Properties getServiceProperties() { return null; } " +
                "}"
            ),
            List.of("cascara-common", "cti-shared"),
            "common-io-test"
        );
        return providerBJar;
    }
}
