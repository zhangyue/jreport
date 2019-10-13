/**
 * Created by Zhang Yue on 2/11/2018
 */
package pers.yue.test.jreport;

import java.util.Comparator;
import java.util.List;

public class TestClassResult {
    protected String testClassName;
    protected long startTimeInMillis;
    protected long endTimeInMillis;
    protected List<TestResult> failedTestResults;
    protected List<TestResult> skippedTestResults;
    protected List<TestResult> passedTestResults;

    public TestClassResult(
            String testClassName,
            long startTimeInMillis,
            long endTimeInMillis,
            List<TestResult> failedTestResults,
            List<TestResult> skippedTestResults,
            List<TestResult> passedTestResults
    ) {
        this.testClassName = testClassName;
        this.startTimeInMillis = startTimeInMillis;
        this.endTimeInMillis = endTimeInMillis;
        this.failedTestResults = failedTestResults;
        this.skippedTestResults = skippedTestResults;
        this.passedTestResults = passedTestResults;
    }

    public String getTestClassName() {
        return testClassName;
    }

    public void setTestClassName(String testClassName) {
        this.testClassName = testClassName;
    }

    public long getStartTimeInMillis() {
        return startTimeInMillis;
    }

    public void setStartTimeInMillis(long startTimeInMillis) {
        this.startTimeInMillis = startTimeInMillis;
    }

    public List<TestResult> getFailedTestResults() {
        failedTestResults.sort(Comparator.comparing(TestResult::getStartTimeInMillis));
        return failedTestResults;
    }

    public long getEndTimeInMillis() {
        return endTimeInMillis;
    }

    public void setEndTimeInMillis(long endTimeInMillis) {
        this.endTimeInMillis = endTimeInMillis;
    }

    public void setFailedTestResults(List<TestResult> failedTestResults) {
        this.failedTestResults = failedTestResults;
    }

    public List<TestResult> getSkippedTestResults() {
        skippedTestResults.sort(Comparator.comparing(TestResult::getStartTimeInMillis));
        return skippedTestResults;
    }

    public void setSkippedTestResults(List<TestResult> skippedTestResults) {
        this.skippedTestResults = skippedTestResults;
    }

    public List<TestResult> getPassedTestResults() {
        passedTestResults.sort(Comparator.comparing(TestResult::getStartTimeInMillis));
        return passedTestResults;
    }

    public void setPassedTestResults(List<TestResult> passedTestResults) {
        this.passedTestResults = passedTestResults;
    }

    public int getTotalNumOfTest() {
        return failedTestResults.size() + skippedTestResults.size() + passedTestResults.size();
    }

    public String getPassRate() {
        return 100 * passedTestResults.size() / getTotalNumOfTest() + "%";
    }
}
