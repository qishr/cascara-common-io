package integration.test.fixtures.isolated;

import io.github.qishr.cascara.common.content.type.ContentTypeStore;
import io.github.qishr.cascara.common.diagnostic.report.GlobalReporter;
import io.github.qishr.cascara.common.diagnostic.report.Reporter;
import io.github.qishr.cascara.common.exec.AbstractExecutionTask;
import io.github.qishr.cascara.common.service.SPL;
import io.github.qishr.cascara.common.util.ContentType;
import io.github.qishr.cascara.common.util.ContentTypeResolver;

public class CtrTestTask extends AbstractExecutionTask<CtrTestInput, CtrTestOutput> {
    Reporter reporter = Reporter.forClass(CtrTestTask.class);

    @Override
    public CtrTestOutput run(CtrTestInput input) throws Exception {

        reporter.debug("Hello");

        SPL spl = SPL.getRoot();
        spl.registerClass(ContentTypeStore.class);

        ContentTypeResolver ctr = SPL.load(ContentTypeResolver.class);
        ContentType json = ctr.resolve("application/json");
        if (json == null) {
            System.err.println("Unable to resolve content type");
        }

        // List<ServiceMetadata> s = SPL.getRoot().getProviders();
        // for (ServiceMetadata m : s) {
        //     System.out.println(m.getTypeName());
        // }

        return new CtrTestOutput();
    }
}
