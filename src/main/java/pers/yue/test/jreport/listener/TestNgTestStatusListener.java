/**
 * Created by Zhang Yue on 2/14/2018
 */
package pers.yue.test.jreport.listener;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import pers.yue.util.ThreadUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Updates real time test status by appending the status file with latest test case execution result.
 */
public class TestNgTestStatusListener implements ITestListener {
    private static Logger logger = LoggerFactory.getLogger(ThreadUtil.getClassName());

    String outputDirectoryName = TestNgReportListener.OUTPUT_DIRECTORY;
    String statusFileName = "status";
    File statusFile = new File(outputDirectoryName, statusFileName);

    public void onTestStart(ITestResult result) {
        try {
            String testName = result.getTestName() == null? result.getName() : result.getTestName();
            FileUtils.write(statusFile, result.getTestClass().getName() + "." + testName + " .... ", Charset.defaultCharset(), true);
        } catch (IOException e) {
            logger.error("Exception while write status file {}.", statusFile.getPath());
        }
    }

    public void onTestSuccess(ITestResult result) {
        onTestFinish("PASS");
    }

    public void onTestFailure(ITestResult result) {
        onTestFinish("FAIL");
    }

    public void onTestSkipped(ITestResult result) {
        onTestFinish("SKIP");
    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        onTestFinish("PWE");
    }

    public void onStart(ITestContext context) { }

    public void onFinish(ITestContext context) { }

    public void onTestFinish(String statusString) {
        try {
            FileUtils.write(statusFile, statusString + "\n", Charset.defaultCharset(), true);
        } catch (IOException e) {
            logger.error("Exception while write status file {}.", statusFile.getPath());
        }
    }
}
