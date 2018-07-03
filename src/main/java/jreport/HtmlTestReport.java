/**
 * Created by Zhang Yue on 2/11/2018
 */
package jreport;

import jreport.envinfo.EnvInfo;
import jreport.util.DateUtil;
import jreport.util.ThreadUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Publishes test report into HTML format.
 */
public class HtmlTestReport extends AbstractTestReport implements TestReport {
    private static Logger logger = LoggerFactory.getLogger(ThreadUtil.getClassName());

    private String html;
    private String summary;
    private String outputDirectoryName;
    private final String HTML_FILE_NAME = "index.html";
    private final String SUMMARY_FILE_NAME = "summary";

    private final String GIT_LAB_URL_PREFIX
            = "http://git.jd.com/cloud-storage/oss-test/blob/master/jss_test/src/main/java/";
    private final String TEST_JOURNAL_PATH_TO_FILE
            = ".." + File.separator + "log" + File.separator + "jss_test.log";
    private final String TEST_RESULT_ARCHIVE_LINK = "http://git.jd.com/cloud-storage/jdcloud-test-ci-results/tree/master/jss";
    private final String TEST_RESULT_ARCHIVE_DISPLAY = "jdcloud-test-ci-results/jss";

    public HtmlTestReport(
            String suiteName,
            Map<String, TestPackageResult> testResults,
            EnvInfo envInfo,
            String outputDirectoryName
    ) {
        super(suiteName, testResults, envInfo);
        this.outputDirectoryName = outputDirectoryName;
    }

    public TestReport generate() {
        html = generateHtml();
        summary = getResultSummary();
        return this;
    }

    public void publish() {
        publishHtml();
    }

    private String generateHtml() {
        String currentTimeStr = DateUtil.formatTime(DateUtil.getCurrentTime(),
                DateUtil.FORMAT_SIMPLE, TimeZone.getTimeZone("GMT+8"));

        StringBuilder body = new StringBuilder();
        body.append("<h3>" + getResultSummary() + "</h3>\n");
        body.append("<h5>Report generated at " + currentTimeStr + "</h5>\n");
        // Place holder for email.
        body.append("<!--<p class='info'><a href=#>See this report in web browser</a></p>-->\n");
        body.append(generateSummary());
        body.append(generateEnvInfo());
        body.append(generateDetail());

        String head = "" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n" +
                "<style type=\"text/css\">\n" +
                "h3 { font-family:Arial; }\n" +
                "h5 { font-family:Arial; }\n" +
                "td { font-family:Arial; font-size: 14px; }\n" +
                "td.th { font-family:Arial; font-size: 14px; font-weight: bold}\n" +
                "td.packagebanner { font-family:Arial; font-size: 14px; font-weight: bold}\n" +
                "table { border-collapse: collapse; }\n" +
                "p.info { font-family: Arial; font-size: 14px; }\n" +
                "a.source {text-decoration: none}\n" +
                "</style>";

        StringBuilder htmlBuilder = new StringBuilder("<!DOCTYPE html>\n");
        htmlBuilder.append("<html>\n");
        htmlBuilder.append("<head>\n").append(head).append("</head>\n");
        htmlBuilder.append("<body>\n").append(body).append("</body>\n");
        htmlBuilder.append("</html>\n");
        return htmlBuilder.toString();
    }

    private String generateEnvInfo() {
        StringBuilder sb = new StringBuilder();

        sb.append("<hr align=left width=1200>\n");

        sb.append("<table border=0 width=1200 cellspacing=1>\n");
        sb.append(generateEnvLine("IP", envInfo.getIpInfo(), true));
        sb.append(generateEnvLine("Endpoint", envInfo.getEndpointInfo(), true));
        // User info is not necessary for now.
//        sb.append(generateEnvLine("User", envInfo.getUserInfo(), false));
        sb.append(generateEnvLine("Configuration", envInfo.getConfigInfo()));
        sb.append(generateEnvLine("JSS Branch", "$jss_branch"));
        sb.append(generateEnvLine("Test Journal", "<a href='" + TEST_JOURNAL_PATH_TO_FILE + "'>jss_test.log</a>"));
        sb.append(generateEnvLine("CI Test Result Archive", "<a href='" + TEST_RESULT_ARCHIVE_LINK + "' target=_blank>" + TEST_RESULT_ARCHIVE_DISPLAY + "</a>"));
        sb.append("</table>");

        return sb.toString();
    }

    private String generateEnvLine(String name, String value) {
        return  generateEnvLine(name, Arrays.asList(value), true);
    }

    private String generateEnvLine(String name, List<String> values, boolean singleRow) {
        StringBuilder sb = new StringBuilder();
        if(singleRow) {
            sb.append("<tr>\n");
            sb.append("<td class='th'>").append(name).append("</td>\n");
            sb.append("<td>").append(String.join(" | ", values)).append("</td>\n");
            sb.append("</tr>\n");
        } else {
            for (int i = 0; i < values.size(); i++) {
                sb.append("<tr>\n");
                if (i == 0) {
                    sb.append("  <td class='th' rowspan=").append(values.size()).append(">").append(name).append("</td>\n");
                }
                sb.append("  <td>").append(values.get(i)).append("</td>\n");
                sb.append("</tr>\n");
            }
        }

        return sb.toString();
    }

    private String generateEnvLine(String name, Map<String, String> values) {
        StringBuilder sb = new StringBuilder();

        boolean isName = true;
        for (Map.Entry entry : values.entrySet()) {
            sb.append("<tr>\n");
            if (isName) {
                sb.append("  <td class='th' rowspan=").append(values.size()).append(">").append(name).append("</td>\n");
                isName = false;
            }
            sb.append("  <td>").append(entry.getKey()).append(" = ").append(entry.getValue()).append("</td>\n");
            sb.append("</tr>\n");
        }

        return sb.toString();
    }

    private String generateSummary() {
        StringBuilder sb = new StringBuilder();

        sb.append("<hr align=left width=1200>\n");

        sb.append("<table border=0 width=1200 cellspacing=1>\n");
        sb.append("<tr>\n");
        sb.append("  <td class='th' align=center  width=40%>Package</td>\n");
        sb.append("  <td class='th' align=right witdh=10%>Passed</td>\n");
        sb.append("  <td class='th' align=right witdh=10%>Failed</td>\n");
        sb.append("  <td class='th' align=right witdh=10%>Skipped</td>\n");
        sb.append("  <td class='th' align=right witdh=10%>Total</td>\n");
        sb.append("  <td class='th' align=right witdh=10%>Pass rate</td>\n");
        sb.append("  <td class='th' align=right witdh=10%>Duration</td>\n");
        sb.append("</tr>\n");

        for(TestPackageResult testPackageResult : testPackageResults.values()) {
            int total = testPackageResult.getTotalNumOfTest();
            int passed = testPackageResult.getPassedTestResults().size();
            int failed = testPackageResult.getFailedTestResults().size();
            int skipped = testPackageResult.getSkippedTestResults().size();
            String durationString = ReportUtil.generateDuration(
                    testPackageResult.getStartTimeInMillis(), testPackageResult.getEndTimeInMillis());

            sb.append("<tr>\n");
            sb.append("  <td><a href='#")
                    .append(testPackageResult.getTestPackageName()).append("'>")
                    .append(testPackageResult.getTestPackageName()).append("</a></td>\n");
            sb.append("  <td align=right>").append(passed).append("</td>\n");
            sb.append("  <td align=right>").append(failed).append("</td>\n");
            sb.append("  <td align=right>").append(skipped).append("</td>\n");
            sb.append("  <td align=right>").append(total).append("</td>\n");
            sb.append("  <td align=right>").append(testPackageResult.getPassRate()).append("</td>\n");
            sb.append("  <td align=right>").append(durationString).append("</td>\n");
            sb.append("</tr>\n");
        }

        sb.append("<tr>\n");
        sb.append("  <td class='th' align=center>Overall</td>\n");
        sb.append("  <td class='th' align=right>").append(overallPassed).append("</td>\n");
        sb.append("  <td class='th' align=right>").append(overallFailed).append("</td>\n");
        sb.append("  <td class='th' align=right>").append(overallSkipped).append("</td>\n");
        sb.append("  <td class='th' align=right>").append(overallTotal).append("</td>\n");
        sb.append("  <td class='th' align=right>").append(getPassRate()).append("</td>\n");
        String overallDuration = overallDurationInSecond + " s";
        // Show in minute if duration is larger than 180 seconds.
        if(overallDurationInSecond > 180) {
            if(overallDurationInSecond % 60 > 10) {
                overallDuration = overallDurationInSecond / 60 + 1 + "m";
            } else {
                overallDuration = overallDurationInSecond / 60 + "m";
            }
        }
        sb.append("  <td class='th' align=right>").append(overallDuration).append("</td>\n");
        sb.append("</tr>\n");
        sb.append("</table>");

        return sb.toString();
    }

    private String generateDetail() {
        StringBuilder sb = new StringBuilder();

        for(TestPackageResult testPackageResult : testPackageResults.values()) {
            /*
            Test class banner.
             */
            sb.append("<hr align=left width=1200>\n");
            sb.append("<table border=0 width=1200 cellspacing=1>\n");
            sb.append("<tr>\n");
            sb.append("  <td class='packagebanner'>")
                    .append("<a name='" + testPackageResult.getTestPackageName() + "'>")
                    .append(testPackageResult.getTestPackageName())
                    .append("</a>")
                    .append("</td>\n");
            sb.append("  <td align=right>")
                    .append(DateUtil.formatTime(
                            testPackageResult.getStartTimeInMillis(),
                            DateUtil.FORMAT_SIMPLE, TimeZone.getTimeZone("GMT+8")))
                    .append("</td>\n");
            sb.append("</tr>\n");;
            sb.append("</table>\n");
            sb.append("<hr align=left width=1200>\n");

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

                    sb.append("<tr>");
                    sb.append("  <td width=80%>").append(testNameHref).append("</td>");
                    sb.append("  <td width=10%>").append(statusWithColor).append("</td>");
                    sb.append("  <td width=10% align=right>").append(duration).append("</td>");
                    sb.append("</tr>\n");
                }
            }
            sb.append("</table>\n");
        }

        return sb.toString();
    }

    private void publishHtml() {
        logger.info("Report file: {}", outputDirectoryName + File.separator + HTML_FILE_NAME);

        writeToFile(outputDirectoryName, HTML_FILE_NAME, html);
        writeToFile(outputDirectoryName, SUMMARY_FILE_NAME, getResultSummary() + "\n");
    }

    private void writeToFile(String directoryName, String fileName, String content) {
        File outputFile = new File(directoryName, fileName);
        outputFile.getParentFile().mkdirs();

        try {
            outputFile.createNewFile();
            FileUtils.writeStringToFile(outputFile, content, Charset.defaultCharset());
        } catch (IOException e) {
            logger.error("Exception when write to file {}.", outputFile.getPath(), e);
        }
    }
}
