/**
 * Created by Zhang Yue on 2/4/2018
 */
package ffzy.jreport.listener;

import ffzy.jreport.HtmlTestReport;
import ffzy.jreport.TestNgResultCollector;
import ffzy.jreport.TestPackageResult;
import ffzy.jreport.TestReport;
import jreport.*;
import ffzy.jreport.envinfo.EnvInfoProvider;
import ffzy.jreport.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;
import org.testng.xml.XmlSuite;

import java.util.List;
import java.util.Map;

/**
 * Generates test report at the end of test.
 */
public class TestNgReportListener implements IReporter {
    private static Logger logger = LoggerFactory.getLogger(ThreadUtil.getClassName());
    public static final String OUTPUT_DIRECTORY = "report";

    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        Map<String, TestPackageResult> testResults = new TestNgResultCollector(suites).collect();

        String suiteName = suites.get(0).getName();
        TestReport testReport = new HtmlTestReport(suiteName, testResults, EnvInfoProvider.getEnvInfo(), OUTPUT_DIRECTORY);
        testReport.generate().publish();
    }
}
