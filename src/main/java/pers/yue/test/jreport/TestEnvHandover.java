package pers.yue.test.jreport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.yue.test.jreport.envinfo.EnvInfo;
import pers.yue.common.util.ThreadUtil;

/**
 * Created by Zhang Yue on 5/12/2019
 */
public class TestEnvHandover {
    private static Logger logger = LoggerFactory.getLogger(ThreadUtil.getClassName());

    private static EnvInfo testEnvInfo;

    public static void set(EnvInfo testEnvInfo) {
        TestEnvHandover.testEnvInfo = testEnvInfo;
    }

    public static EnvInfo get() {
        if(testEnvInfo == null) {
            logger.error("TestEnvInfo not initialized yet.");
            return null;
        }
        return TestEnvHandover.testEnvInfo;
    }
}
