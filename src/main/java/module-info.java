module cascara.common.io {
    requires java.net.http;
    requires transitive cascara.common;
    requires cascara.lang.yaml;

    // uses io.github.qishr.cascara.common.lang.processor.AstConverter;
    // uses io.github.qishr.cascara.common.lang.processor.Emitter;
    // uses io.github.qishr.cascara.common.lang.processor.Parser;

    exports io.github.qishr.cascara.common.content;
    exports io.github.qishr.cascara.common.content.type;
    exports io.github.qishr.cascara.common.io;
    exports io.github.qishr.cascara.common.io.filewatcher;
    exports io.github.qishr.cascara.common.io.provider;

    opens io.github.qishr.cascara.common.content;
}
