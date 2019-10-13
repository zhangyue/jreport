/**
 * Created by Zhang Yue on 2/11/2018
 */
package pers.yue.test.jreport.util;

import org.dom4j.DocumentHelper;
import org.dom4j.io.HTMLWriter;
import org.dom4j.io.OutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import pers.yue.test.jreport.exceptions.runtime.TestRunException;
import pers.yue.util.ThreadUtil;

import java.io.StringWriter;

public class ReportUtil {
    private static Logger logger = LoggerFactory.getLogger(ThreadUtil.getClassName());

    public static String generateStatus(int status) {
        String testStatusString;
        switch(status) {
            case ITestResult.SUCCESS: testStatusString = "PASS"; break;
            case ITestResult.FAILURE: testStatusString = "FAIL"; break;
            case ITestResult.SKIP: testStatusString = "SKIP"; break;
            default: throw new TestRunException("Invalid status");
        }
        return testStatusString;
    }

    public static String generateStatusWithColor(int status) {
        String testStatusString;
        switch(status) {
            case ITestResult.SUCCESS: testStatusString = "<font color='green'>PASS</font>"; break;
            case ITestResult.FAILURE: testStatusString = "<font color='red'>FAIL</font>"; break;
            case ITestResult.SKIP: testStatusString = "<font color='olive'>SKIP</font>"; break;
            default: throw new TestRunException("Invalid status");
        }
        return testStatusString;
    }

    public static String generateDuration(long startTimeInMillis, long endTimeInMillis) {
        float durationMillis = endTimeInMillis - startTimeInMillis;
        float durationSeconds = durationMillis / 1000;
        String duration;

        if(durationMillis < 1000) {
            duration = (int)durationMillis + " ms";
        } else if(durationMillis < 10000) {
            duration = String.format("%.2f", durationSeconds) + " s";
        } else {
            if(durationMillis % 1000 >= 400) {
                durationSeconds += 1;
            }
            duration = (int)durationSeconds + " s";
        }

        return duration;
    }

    public static String toPrettyHtml(String str) {
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("utf-8");
            StringWriter writer = new StringWriter();
            HTMLWriter htmlWriter = new HTMLWriter(writer, format);
            htmlWriter.write(DocumentHelper.parseText(str));
            htmlWriter.close();
            return writer.toString();
        } catch (Exception e) {
            logger.error("Exception when format html string.");
            throw new TestRunException(e);
        }
    }
}
