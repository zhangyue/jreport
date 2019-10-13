package pers.yue.jreport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import pers.yue.jreport.envinfo.EnvInfo;
import pers.yue.jreport.util.ReportUtil;
import pers.yue.jreport.util.ThreadUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Publishes test report into compact HTML format (usually for email).
 *
 * Created by Zhang Yue on 8/29/2019
 */
public class HtmlCompactTestReport extends HtmlTestReport implements TestReport {
    private static Logger logger = LoggerFactory.getLogger(ThreadUtil.getClassName());

    public HtmlCompactTestReport(
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

                    sb.append("<tr>");
                    sb.append("  <td width=5%>").append(statusWithColor).append("</td>");
                    sb.append("  <td width=80%>").append(testNameHref).append("</td>");
                    sb.append("  <td width=5%>").append(statusWithColor).append("</td>");
                    sb.append("  <td width=10% align=right>").append(duration).append("</td>");
                    sb.append("</tr>\n");
                }
                numOmittedPassedCases += testClassResult.getPassedTestResults().size();
            }
            sb.append("<tr><td width=5%>").append(ReportUtil.generateStatusWithColor(ITestResult.SUCCESS)).append("</td>")
                    .append("<td colspan=3>").append(numOmittedPassedCases).append(" passed cases omitted ...</td></tr>\n");
            sb.append("</table>\n");
        }

        return sb.toString();
    }
}
