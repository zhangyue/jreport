/**
 * Created by Zhang Yue on 2/4/2018
 */
package pers.yue.test.jreport.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.xml.XmlSuite;
import pers.yue.test.jreport.*;
import pers.yue.common.util.ThreadUtil;

import java.util.List;
import java.util.Map;

/**
 * Generates test report at the end of test.
 */
public class TestNgReportListener implements IReporter {
    private static Logger logger = LoggerFactory.getLogger(ThreadUtil.getClassName());
    public static final String OUTPUT_DIRECTORY = "report";
    public static final String OUTPUT_FILE = "index.html";

    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        Map<String, TestPackageResult> testResults = new TestNgResultCollector(suites).collect();

        String suiteName = suites.get(0).getName();
        TestReport testReport = new HtmlTestReport(suiteName, testResults, TestEnvHandover.get(), OUTPUT_DIRECTORY);
        testReport.generate().publish(OUTPUT_FILE);
    }
}
