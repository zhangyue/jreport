/**
 * Created by Zhang Yue on 2/11/2018
 */
package pers.yue.test.jreport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.yue.test.jreport.envinfo.EnvInfo;
import pers.yue.test.jreport.util.ReportUtil;
import pers.yue.common.util.ThreadUtil;
import pers.yue.test.util.FileTestUtil;
import pers.yue.common.util.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Publishes performance test report into HTML format.
 */
public class HtmlPerfTestReport extends HtmlTestReport implements TestReport {
    private static Logger logger = LoggerFactory.getLogger(ThreadUtil.getClassName());

    public HtmlPerfTestReport(
            String suiteName,
            Map<String, TestPackageResult> testResults,
            EnvInfo testEnvInfo,
            String outputDirectoryName
    ) {
        super(suiteName, testResults, testEnvInfo, outputDirectoryName);
    }

    @Override
    String generateDetail() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.generateDetail());

        for(TestPackageResult testPackageResult : testPackageResults.values()) {
            /*
            Test class banner.
             */
            generateClassBanner(sb, testPackageResult);

            int numOmittedPassedCases = 0;

            /*
            Test case detail table.
             */
            sb.append("<table border=0 width=1200 cellspacing=1>\n");
            for(TestClassResult testClassResult : testPackageResult.getTestClassResults()) {
                List<TestResult> testResults = new ArrayList<>();
                testResults.addAll(testClassResult.getFailedTestResults());
                testResults.addAll(testClassResult.getSkippedTestResults());
                testResults.addAll(testClassResult.getPassedTestResults());

                for (TestResult testResult : testResults) {
                    String testClassSimpleName = testClassResult.getTestClassName()
                            .substring(testClassResult.getTestClassName().lastIndexOf(".") + 1);
                    String classPath = testClassResult.getTestClassName().replace(".", "/");
                    String testNameHref = "<a href='" + GIT_LAB_URL_PREFIX + classPath + ".java' " +
                            "class='source' target='_blank'>"
                            + testClassSimpleName + "</a> - " + testResult.getName();

                    String statusWithColor = ReportUtil.generateStatusWithColor(testResult.getStatus());
                    String duration = ReportUtil.generateDuration(
                            testResult.getStartTimeInMillis(), testResult.getEndTimeInMillis());

                    File statResultFile = new File(outputDirectoryName + File.separator +
                            StringUtil.getClassSimpleName(testClassResult.getTestClassName()) + File.separator + testResult.getName());
                    String statResult = FileTestUtil.readStringFromFile(statResultFile);
                    statResult = statResult.replace("{\n", "").replace("\n}", "");

                    sb.append("<tr>");
                    sb.append("  <td width=5%>").append(statusWithColor).append("</td>");
                    sb.append("  <td width=40%>").append(testNameHref).append("</td>");
                    sb.append("  <td width=45%><pre>").append(statResult).append("</pre></td>");
                    sb.append("  <td width=10% align=right>").append(duration).append("</td>");
                    sb.append("</tr>\n");
                }
                numOmittedPassedCases += testClassResult.getPassedTestResults().size();
            }
            sb.append("</table>\n");
        }

        return sb.toString();
    }
}
