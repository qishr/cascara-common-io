package integration.test.isolated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;

import io.github.qishr.cascara.lang.json.token.JsonToken;
import org.junit.jupiter.api.Test;

import io.github.qishr.cascara.common.diagnostic.report.GlobalReporter;
import io.github.qishr.cascara.common.diagnostic.report.LocalReporter;
import io.github.qishr.cascara.common.diagnostic.Diagnostic.Level;
import io.github.qishr.cascara.common.exec.IsolatedExecutor;
import io.github.qishr.cascara.common.exec.IsolatedExecutor.Response;
import io.github.qishr.cascara.common.exec.JvmOptions;
import io.github.qishr.cascara.common.exec.ipc.IpcClient;
import integration.test.fixtures.isolated.CtrTestInput;
import integration.test.fixtures.isolated.CtrTestOutput;
import integration.test.fixtures.isolated.CtrTestTask;
import io.github.qishr.cascara.common.lang.processor.Serializer;
import io.github.qishr.cascara.lang.json.processor.JsonAstParser;
import io.github.qishr.cascara.lang.json.processor.JsonSerializer;
import io.github.qishr.cascara.lang.json.processor.JsonTokenizer;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

import io.github.qishr.cascara.common.data.TextualTable;
import io.github.qishr.cascara.common.util.StringUtils;
import io.github.qishr.cascara.common.util.TermUtils;
import io.github.qishr.cascara.lang.json.token.JsonErrorToken;
import io.github.qishr.cascara.lang.json.token.JsonToken;

public class IsolatedContentTypeResolverTest extends IsolatedTestBase {
    @Test
    void test_loadsContentTypeResolver() throws Exception {
        CtrTestInput input = new CtrTestInput();

        // GlobalReporter.forClass(IsolatedContentTypeResolverTest.class)
        //     .setShowProblemCodes(true)
        //     .setLevel(Level.DEBUG);

        GlobalReporter.forClass(CtrTestTask.class)
            .setShowProblemCodes(true)
            .setLevel(Level.DEBUG);

        boolean ipcDebug = false;

        JvmOptions options = new JvmOptions()
            .setModuleName(CtrTestTask.class.getModule().getName()) // Forces --module-path launch
            .setModulePath(getModulePath())
            .setSystemProperty("casc.report.level." + CtrTestTask.class.getName(), "DEBUG")
            // .setSystemProperty("cascara.ipc.debug", "true")
            // .setSystemProperty(IpcClient.DEBUG_PROP, String.valueOf(ipcDebug))



            //
            // This is the setting to test with:
            .setSystemProperty("cascara.spl.discover", "false")
            //

            .setTimeout(Duration.ofSeconds(5))
            .setDebug(PROCESS_DEBUG_ENABLED);

        IsolatedExecutor exec = IsolatedExecutor.forTask(CtrTestTask.class, CtrTestOutput.class)
            .setIpcDebugEnabled(ipcDebug)
            .setDiagnosticForwardingEnabled(true)
            .setOptions(options);

        // Serializer<?> ipcSerializer = exec.getSerializer();
        // if (ipcSerializer instanceof JsonSerializer serializer) {
        //     JsonAstParser parser = serializer.getParser();
        //     JsonTokenizer tokenizer = parser.getTokenizer();
        //     tokenizer.setReporter(
        //         new LocalReporter().setLevel(Level.TRACE)
        //     );
        //     // List<JsonToken> tokens = tokenizer.tokenize("{   \"n\": 0 }");
        //     // dumpTokens(tokens);
        //     // System.out.println("Debug");
        // }

        Response<CtrTestOutput> result = exec.run(input);

        // Response<CtrTestOutput> result = IsolatedExecutor.run(
        //     CtrTestTask.class,
        //     input,
        //     CtrTestOutput.class,
        //     options);

        debug(result);

        assertTrue(result.getSystemErr().isEmpty());

        // CtrTestOutput output = result.orElseThrow();
        // assertEquals(0, output.n);
    }


    public static void dumpTokens(List<JsonToken> tokens) {
        dumpTokens(new PrintWriter(System.out), tokens, -1);
    }

    public static void dumpTokens(List<JsonToken> tokens, int maxColumnWidth) {
        dumpTokens(new PrintWriter(System.out), tokens, maxColumnWidth);
    }

    public static void dumpTokens(Writer writer, List<JsonToken> tokens) {
        dumpTokens(writer, tokens, -1);
    }

    public static void dumpTokens(Writer writer, List<JsonToken> tokens, int maxColumnWidth) {
        try {
            writer.write("\n");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // System.out.println("------------TOKENS-----------");
        TextualTable table = new TextualTable()
                .setStyle(TextualTable.Style.ROUNDED)
                .setMaxColumnWidth(maxColumnWidth)
                .setBorderColor(TermUtils.ANSI_WHITE)
                .addColumn("#")
                .addColumn("Token")
                .addColumn("Location")
                .addColumn("Lexeme")
                .addColumn("Content");

        int tokenIdent = 0;

        for (int i = 0; i < tokens.size(); i++) {
            JsonToken t = tokens.get(i);
            switch(t.getType()) {
                case ERROR:
                    JsonErrorToken et = (JsonErrorToken)t;
                    table.addRow(
                            String.format("%2d", i),
                            t.getType().toString(),
                            String.format("L:%-3d C:%-3d", t.getStartLine(), t.getStartColumn()),
                            "", et.getCode().getFormat()
                    );
                    break;
                case STRING, COMMENT:
                    table.addRow(
                            String.format("%2d", i),
                            "  ".repeat(tokenIdent) + t.getType().toString(),
                            String.format("L:%-3d C:%-3d", t.getStartLine(), t.getStartColumn()),
                            StringUtils.debugString(t.getLexeme()),
                            StringUtils.debugString(t.getContent())
                    );
                    break;
                default:
                    // if (t.getType() == YamlTokenType.DEDENT && tokenIdent > 0) {
                    //     tokenIdent--;
                    // }
                    table.addRow(
                            String.format("%2d", i),
                            "  ".repeat(tokenIdent) + t.getType().toString(),
                            String.format("L:%-3d C:%-3d", t.getStartLine(), t.getStartColumn()), "", ""
                    );
                    // if (t.getType() == JsonTokenType.INDENT) {
                    //     tokenIdent++;
                    // }
            }
        }

        try {
            table.render(writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
