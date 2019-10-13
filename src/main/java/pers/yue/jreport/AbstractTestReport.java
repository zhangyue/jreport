/**
 * Created by Zhang Yue on 2/11/2018
 */
package pers.yue.jreport;

import pers.yue.jreport.envinfo.EnvInfo;
import pers.yue.jreport.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class AbstractTestReport implements TestReport {
    private static Logger logger = LoggerFactory.getLogger(ThreadUtil.getClassName());

    protected String suiteName;
    protected Map<String, TestPackageResult> testPackageResults;
    protected EnvInfo envInfo;
    protected int overallTotal = 0, overallPassed = 0, overallFailed = 0, overallSkipped = 0, overallDurationInSecond = 0;
    protected String passRate = "0%";

    public AbstractTestReport(String suiteName, Map<String, TestPackageResult> testPackageResults, EnvInfo envInfo) {
        this.suiteName = suiteName;
        this.testPackageResults = testPackageResults;
        this.envInfo = envInfo;
        calculateOverallResult();
    }

    protected void calculateOverallResult() {
        for(TestPackageResult testPackageResult : testPackageResults.values()) {
            overallTotal += testPackageResult.getTotalNumOfTest();
            overallPassed += testPackageResult.getPassedTestResults().size();
            overallFailed += testPackageResult.getFailedTestResults().size();
            overallSkipped += testPackageResult.getSkippedTestResults().size();
            overallDurationInSecond +=
                    (testPackageResult.getEndTimeInMillis() - testPackageResult.getStartTimeInMillis()) / 1000;
        }

        if(overallTotal == 0) {
            passRate = "0%";
        } else {
            passRate = 100 * overallPassed / overallTotal + "%";
        }
    }

    public String getSuiteName() {
        return suiteName;
    }

    public String getPassRate() {
        return passRate;
    }

    public String getResultSummary() {
        String summary = getSuiteName() + " - " + getPassRate() + " passed";
        return summary;
    }
}
