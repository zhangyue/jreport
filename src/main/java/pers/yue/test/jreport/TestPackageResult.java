/**
 * Created by Zhang Yue on 2/11/2018
 */
package pers.yue.test.jreport;

import pers.yue.common.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TestPackageResult {
    private static Logger logger = LoggerFactory.getLogger(ThreadUtil.getClassName());

    protected String testPackageName;
    protected List<TestClassResult> testClassResults = new ArrayList<>();
    protected long startTimeInMillis;
    protected long endTimeInMillis;
    protected List<TestResult> failedTestResults = new ArrayList<>();
    protected List<TestResult> skippedTestResults = new ArrayList<>();
    protected List<TestResult> passedTestResults = new ArrayList<>();
    protected int totalNumOfTests = 0;

    public TestPackageResult(
            String testPackageName,
            TestClassResult testClassResult
    ) {
        this.testPackageName = testPackageName;
        addTestClassResult(testClassResult);
    }

    public String getTestPackageName() {
        return testPackageName;
    }

    public void setTestPackageName(String testPackageName) {
        this.testPackageName = testPackageName;
    }

    public List<TestClassResult> getTestClassResults() {
        return testClassResults;
    }

    public void setTestClassResults(List<TestClassResult> testClassResults) {
        this.testClassResults = new ArrayList<>();
        addTestClassResults(testClassResults);
    }

    public void addTestClassResult(TestClassResult testClassResult) {
        List<TestClassResult> testClassResults = new ArrayList<>();
        testClassResults.add(testClassResult);
        addTestClassResults(testClassResults);;
    }

    public void addTestClassResults(List<TestClassResult> testClassResults) {
        this.testClassResults.addAll(testClassResults);

        for(TestClassResult testClassResult : testClassResults) {
            failedTestResults.addAll(testClassResult.getFailedTestResults());
            skippedTestResults.addAll(testClassResult.getSkippedTestResults());
            passedTestResults.addAll(testClassResult.getPassedTestResults());

            startTimeInMillis = startTimeInMillis == 0
                    ? testClassResult.getStartTimeInMillis() : startTimeInMillis;

            startTimeInMillis = Math.min(startTimeInMillis, testClassResult.getStartTimeInMillis());

            endTimeInMillis = Math.max(endTimeInMillis, testClassResult.getEndTimeInMillis());

            totalNumOfTests += testClassResult.getTotalNumOfTest();

            logger.debug("Start: {}", startTimeInMillis);
            logger.debug("End: {}", endTimeInMillis);
        }
    }

    public long getStartTimeInMillis() {
        return startTimeInMillis;
    }

    public List<TestResult> getFailedTestResults() {
        return failedTestResults;
    }

    public long getEndTimeInMillis() {
        return endTimeInMillis;
    }

    public List<TestResult> getSkippedTestResults() {
        return skippedTestResults;
    }

    public List<TestResult> getPassedTestResults() {
        return passedTestResults;
    }

    public int getTotalNumOfTest() {
        return failedTestResults.size() + skippedTestResults.size() + passedTestResults.size();
    }

    public String getPassRate() {
        return 100 * passedTestResults.size() / getTotalNumOfTest() + "%";
    }
}
