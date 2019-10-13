/**
 * Created by Zhang Yue on 2/4/2018
 */
package pers.yue.jreport.listener;

import pers.yue.jreport.HtmlTestReport;
import pers.yue.jreport.TestNgResultCollector;
import pers.yue.jreport.TestPackageResult;
import pers.yue.jreport.TestReport;
import pers.yue.jreport.envinfo.EnvInfoProvider;
import pers.yue.jreport.util.ThreadUtil;
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
