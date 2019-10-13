package pers.yue.test.jreport.test;

import pers.yue.test.jreport.envinfo.EnvInfo;
import pers.yue.test.jreport.envinfo.EnvInfoProvider;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by zhangyue182 on 07/03/2018
 */
public class TestNgTest {
    @BeforeSuite
    public void setup() {
        EnvInfo envInfo = new EnvInfo("192.168.0.1", "foo-bar.com", "user11", new HashMap<>());
        EnvInfoProvider envInfoProvider = new EnvInfoProvider(envInfo);
    }

    @Test
    public void test1() {

    }

    @Test
    public void test2() {

    }
}
