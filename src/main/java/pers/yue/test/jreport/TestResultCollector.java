/**
 * Created by Zhang Yue on 2/11/2018
 */
package pers.yue.test.jreport;

import java.util.Map;

public interface TestResultCollector {
    /**
     * Collects the test results and categorize by test package -> test class -> test methods.
     * @return
     */
    Map<String, TestPackageResult> collect();
}
