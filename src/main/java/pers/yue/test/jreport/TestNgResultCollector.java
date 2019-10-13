/**
 * Created by Zhang Yue on 2/11/2018
 */
package pers.yue.test.jreport;

import pers.yue.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Collects test results from TestNG test results.
 */
public class TestNgResultCollector implements TestResultCollector {
    private static Logger logger = LoggerFactory.getLogger(ThreadUtil.getClassName());

    private List<ISuite> testNgSuites;
    protected Map<String, TestPackageResult> sortedTestResults = new TreeMap<>();

    public TestNgResultCollector(List<ISuite> testNgSuites) {
        this.testNgSuites = testNgSuites;
    }

    /**
     * Collects the test results and categorize by test package -> test class -> test methods.
     * @return
     */
    public Map<String, TestPackageResult> collect() {
        for (ISuite suite : testNgSuites) {
            logger.info("Suite name: {}", suite.getName());

            for(Map.Entry<String, ISuiteResult> entry : suite.getResults().entrySet()) {
                logger.info("Test name: {}", entry.getKey());

                sortTestResultsByTestPackage(entry.getValue().getTestContext(), sortedTestResults);
            }
        }

        return sortedTestResults;
    }

    /**
     * By default test results of all test classes are in a large map.
     * We want to sort them by test packages for easier reporting.
     * @param testContext
     * @param testPackageResults
     */
    private void sortTestResultsByTestPackage(ITestContext testContext, Map<String, TestPackageResult> testPackageResults) {
        Map<String, TestClassResult> testClassResults = new TreeMap<>();

        sortTestResultsByTestClass(testContext, testClassResults);

        for(TestClassResult testClassResult : testClassResults.values()) {
            String packageName = testClassResult.getTestClassName().substring(
                    0, testClassResult.getTestClassName().lastIndexOf("."));
            if(!testPackageResults.containsKey(packageName)) {
                logger.debug("Put package {}", packageName);
                testPackageResults.put(packageName, new TestPackageResult(packageName, testClassResult));
            } else {
                testPackageResults.get(packageName).addTestClassResult(testClassResult);
            }
        }
    }

    /**
     * By default test results of all test classes are in a large map.
     * We want to sort them by test classes for easier reporting.
     * @param testContext
     * @param testClassResults
     */
    private void sortTestResultsByTestClass(ITestContext testContext, Map<String, TestClassResult> testClassResults) {
        Map<String, List<ITestResult>[]> testResultsByClass = new TreeMap();

        logger.debug("Entry before sort: " + testResultsByClass.size());

        sortTestResultsByTestStatus(testContext.getFailedTests(), testResultsByClass, ITestResult.FAILURE);
        sortTestResultsByTestStatus(testContext.getSkippedTests(), testResultsByClass, ITestResult.SKIP);
        sortTestResultsByTestStatus(testContext.getPassedTests(), testResultsByClass, ITestResult.SUCCESS);
        sortTestResultsByTestStatus(testContext.getFailedButWithinSuccessPercentageTests(),
                testResultsByClass, ITestResult.SUCCESS_PERCENTAGE_FAILURE);

        IResultMap[] testConfigResultMaps = new IResultMap[3];
        testConfigResultMaps[0] = testContext.getFailedConfigurations();
        testConfigResultMaps[1] = testContext.getSkippedConfigurations();
        testConfigResultMaps[2] = testContext.getPassedConfigurations();
        Map<String, List<TestResult>> sortedTestConfigResults = sortTestConfigResultsByTestClass(testConfigResultMaps);

        logger.debug("Entry after sort: " + testResultsByClass.size());

        for(Map.Entry<String, List<ITestResult>[]> testResultByClass : testResultsByClass.entrySet()) {

            logger.info("Name: {}", testResultByClass.getKey());
            logger.info("Length: {}", testResultByClass.getValue().length);
            logger.info("FAIL: {}", testResultByClass.getValue()[ITestResult.FAILURE].size());
            logger.info("SKIP: {}", testResultByClass.getValue()[ITestResult.SKIP].size());
            logger.info("SUCCESS: {}", testResultByClass.getValue()[ITestResult.SUCCESS].size());

            testClassResults.put(
                    testResultByClass.getKey(),
                    new TestNgTestClassResult(
                            testResultByClass.getKey(),
                            getEarliestTestStartTime(
                                    sortedTestConfigResults.get(testResultByClass.getKey()), testResultByClass.getValue()),
                            getLatestTestEndTime(
                                    sortedTestConfigResults.get(testResultByClass.getKey()), testResultByClass.getValue()),
                            testResultByClass.getValue()[ITestResult.FAILURE],
                            testResultByClass.getValue()[ITestResult.SKIP],
                            testResultByClass.getValue()[ITestResult.SUCCESS]
                    )
            );
        }
    }

    private void sortTestResultsByTestStatus(
            IResultMap resultMap,
            Map<String, List<ITestResult>[]> testResultsByClass,
            int status
    ) {
        for(ITestResult testNgResult : resultMap.getAllResults()) {
            logger.info(testNgResult.getTestClass() + " " + testNgResult.getName() + " " + testNgResult.getTestName() + " " + testNgResult.getStatus() + " ");

            if(!testResultsByClass.containsKey(testNgResult.getTestClass().getRealClass().getName())) {
                logger.debug("Put class {}", testNgResult.getTestClass().getRealClass().getName());
                testResultsByClass.put(testNgResult.getTestClass().getRealClass().getName(), new ArrayList[5]);
                for (
                        int i = 0;
                        i < testResultsByClass.get(testNgResult.getTestClass().getRealClass().getName()).length;
                        i++
                ) {
                    testResultsByClass.get(testNgResult.getTestClass().getRealClass().getName())[i] = new ArrayList<>();
                }
            }

            if (testResultsByClass.get(testNgResult.getTestClass().getRealClass().getName())[status] != null) {
                logger.debug("Add test {}", testNgResult.getName());
                testResultsByClass.get(testNgResult.getTestClass().getRealClass().getName())[status].add(testNgResult);
            }

        }
    }

    /**
     * To deal with the @BeforeSuite, @BeforeTest, @BeforeClass, @BeforeMethod and @AfterXXX methods.
     * @param resultMaps
     * @return
     */
    private Map<String, List<TestResult>> sortTestConfigResultsByTestClass(IResultMap[] resultMaps) {
        Map<String, List<TestResult>> testResults = new TreeMap<>();
        Set<ITestResult> allResults = new TreeSet();

        for(IResultMap resultMap : resultMaps) {
            allResults.addAll(resultMap.getAllResults());
        }

        for(ITestResult testNgResult : allResults) {
            logger.debug(testNgResult.getTestClass() + " " + testNgResult.getName() + " " + testNgResult.getStatus() + " ");

            Method realMethod = testNgResult.getMethod().getConstructorOrMethod().getMethod();
            BeforeSuite bs = realMethod.getAnnotation(BeforeSuite.class);
            AfterSuite as = realMethod.getAnnotation(AfterSuite.class);
            BeforeTest bt = realMethod.getAnnotation(BeforeTest.class);
            AfterTest at = realMethod.getAnnotation(AfterTest.class);
            if(bs != null || as != null || bt != null || at != null) {
                logger.info("Method {} is a suite level method. Ignore.", testNgResult.getMethod().getMethodName());
                continue;
            }

            if(!testResults.containsKey(testNgResult.getTestClass().getRealClass().getName())) {
                testResults.put(testNgResult.getTestClass().getRealClass().getName(), new ArrayList<TestResult>());
            }

            TestResult testResult = new TestResult(
                    "0",
                    testNgResult.getName(),
                    testNgResult.getTestName(),
                    testNgResult.getStatus(),
                    testNgResult.getStartMillis(),
                    testNgResult.getEndMillis()
            );
            testResults.get(testNgResult.getTestClass().getRealClass().getName()).add(testResult);
        }

        return testResults;
    }

    /**
     * Gets the earliest start time of the tests.
     * Used for determining when a test class starts.
     * @param testResults
     * @return
     */
    private long getEarliestTestStartTime(List<TestResult> testResults, List<ITestResult>[] testResultsByClass) {
        long startTimeInMillis = 0;

        if(testResults == null) {
            logger.info("testResults is null.");
        } else {
            for (TestResult testConfigResult : testResults) {
                startTimeInMillis = startTimeInMillis == 0
                        ? testConfigResult.getStartTimeInMillis() : startTimeInMillis;
                startTimeInMillis = startTimeInMillis > testConfigResult.getStartTimeInMillis()
                        ? testConfigResult.getStartTimeInMillis() : startTimeInMillis;
            }
        }

        if(testResultsByClass == null) {
            logger.info("testResultsByClass is null.");
        } else {
            for (List<ITestResult> testResultList : testResultsByClass) {
                for (ITestResult testResult : testResultList) {
                    startTimeInMillis = startTimeInMillis == 0
                            ? testResult.getStartMillis() : startTimeInMillis;
                    startTimeInMillis = startTimeInMillis > testResult.getStartMillis()
                            ? testResult.getStartMillis() : startTimeInMillis;
                }
            }
        }

        return startTimeInMillis;
    }

    /**
     * Gets the latest end time of the tests.
     * Used for determining when a test class ends.
     * @param testResults
     * @return
     */
    private long getLatestTestEndTime(List<TestResult> testResults, List<ITestResult>[] testResultsByClass) {
        long endTimeInMillis = 0;

        if(testResults == null) {
            logger.info("testResults is null.");
        } else {
            for(TestResult testConfigResult : testResults) {
                endTimeInMillis = endTimeInMillis < testConfigResult.getEndTimeInMillis()
                        ? testConfigResult.getEndTimeInMillis() : endTimeInMillis;
            }
        }

        if(testResultsByClass == null) {
            logger.info("testResultsByClass is null.");
        } else {
            for(List<ITestResult> testResultList : testResultsByClass) {
                for(ITestResult testResult : testResultList) {
                    endTimeInMillis = endTimeInMillis < testResult.getEndMillis()
                            ? testResult.getEndMillis() : endTimeInMillis;
                }
            }
        }

        return endTimeInMillis;
    }
}
