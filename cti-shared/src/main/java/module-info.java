module cascara.test.shared {
    requires transitive cascara.common;
    requires java.compiler;
    requires org.junit.jupiter.api;

    exports io.github.qishr.cascara.test.shared.service;
    exports io.github.qishr.cascara.test.shared.util;

    opens io.github.qishr.cascara.test.shared.util to org.junit.platform.commons;
}
