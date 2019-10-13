/**
 * Created by Zhang Yue on 2/11/2018
 */
package pers.yue.jreport;

public interface TestReport {
    /**
     * Generates test report.
     * @return
     */
    TestReport generate();

    /**
     * Gets the test suite name.
     * @return
     */
    String getSuiteName();

    /**
     * Gets the pass rate of test result.
     * @return
     */
    String getPassRate();

    /**
     * Publish the test report.
     */
    void publish(String outputFile);
}
