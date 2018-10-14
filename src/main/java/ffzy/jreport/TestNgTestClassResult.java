/**
 * Created by Zhang Yue on 2/11/2018
 */
package ffzy.jreport;

import org.testng.ITestResult;

import java.util.ArrayList;
import java.util.List;

public class TestNgTestClassResult extends TestClassResult {

    public TestNgTestClassResult(
            String testClassName,
            long startTimeInMillis,
            long endTimeInMillis,
            List<ITestResult> failedTestNgResults,
            List<ITestResult> skppedTestNgResults,
            List<ITestResult> passedTestNgResults
    ) {
        super(
                testClassName,
                startTimeInMillis,
                endTimeInMillis,
                testNgResultsToTestResults(failedTestNgResults),
                testNgResultsToTestResults(skppedTestNgResults),
                testNgResultsToTestResults(passedTestNgResults)
        );
    }

    private static List<TestResult> testNgResultsToTestResults (List<ITestResult> testNgResults) {
        List<TestResult> testResults = new ArrayList<>();
        for(ITestResult testNgResult : testNgResults) {
            TestResult testResult = new TestResult(
                    "0",
                    testNgResult.getName(),
                    testNgResult.getTestName(),
                    testNgResult.getStatus(),
                    testNgResult.getStartMillis(),
                    testNgResult.getEndMillis()
            );
            testResults.add(testResult);
        }
        return testResults;
    }
}
