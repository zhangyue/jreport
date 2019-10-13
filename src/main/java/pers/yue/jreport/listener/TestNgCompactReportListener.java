/**
 * Created by Zhang Yue on 8/29/2019
 */
package pers.yue.jreport.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.xml.XmlSuite;
import pers.yue.jreport.*;
import pers.yue.jreport.util.ThreadUtil;

import java.util.List;
import java.util.Map;

/**
 * Generates test report at the end of test.
 */
public class TestNgCompactReportListener implements IReporter {
    private static Logger logger = LoggerFactory.getLogger(ThreadUtil.getClassName());
    public static final String OUTPUT_DIRECTORY = "report";
    public static final String OUTPUT_FILE = "email.html";

    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        Map<String, TestPackageResult> testResults = new TestNgResultCollector(suites).collect();

        String suiteName = suites.get(0).getName();
        TestReport testReport = new HtmlCompactTestReport(suiteName, testResults, TestEnvHandover.get(), OUTPUT_DIRECTORY);
        testReport.generate().publish(OUTPUT_FILE);
    }
}
