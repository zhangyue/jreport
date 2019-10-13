/**
 * Created by Zhang Yue on 2/11/2018
 */
package pers.yue.test.jreport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.yue.test.jreport.envinfo.EnvInfo;
import pers.yue.common.util.ThreadUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;

public abstract class AbstractTestReport implements TestReport {
    private static Logger logger = LoggerFactory.getLogger(ThreadUtil.getClassName());

    private String suiteName;
    Map<String, TestPackageResult> testPackageResults;
    EnvInfo testEnvInfo;
    int overallTotal = 0, overallPassed = 0, overallFailed = 0, overallSkipped = 0, overallDurationInSecond = 0;
    private String passRate = "0%";
    protected Properties properties;

    public AbstractTestReport(String suiteName, Map<String, TestPackageResult> testPackageResults, EnvInfo testEnvInfo) {
        this.suiteName = suiteName;
        this.testPackageResults = testPackageResults;
        this.testEnvInfo = testEnvInfo;
        calculateOverallResult();
        properties = loadConf();
    }

    private void calculateOverallResult() {
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

    String getResultSummary() {
        return getSuiteName() + " - " + getPassRate() + " passed";
    }

    private Properties loadConf() {
        String propertiesFileName = "conf" + File.separator + "report.properties";
        Properties properties = new Properties();
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(propertiesFileName))) {
            properties.load(reader);
        } catch (IOException e) {
            logger.warn("{} when {}.", e.getClass().getSimpleName(), ThreadUtil.getMethodName(), e);
        }
        return properties;
    }

    boolean isSkipPassedCases() {
        String lightWeightSuitesPropertyStr = properties.getProperty("light_weight_suites");
        if(lightWeightSuitesPropertyStr == null) {
            return false;
        }
        String[] lightWeightSuiteNames = lightWeightSuitesPropertyStr.split(",");

        for(String lightWeightSuiteName : lightWeightSuiteNames) {
            if(getSuiteName().equals(lightWeightSuiteName.trim())) {
                return true;
            }
        }
        return false;
    }

}
