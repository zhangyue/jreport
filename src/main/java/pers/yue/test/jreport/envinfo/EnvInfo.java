package pers.yue.test.jreport.envinfo;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangyue182 on 2018/02/23
 */
public class EnvInfo {
    private List<String> ipInfo;
    private List<String> endpointInfo;
    private List<String> ip2Info = null;
    private List<String> endpoint2Info = null;
    private List<String> userInfo;
    private Map<String, String> configInfo;

    public EnvInfo(List<String> ipInfo, List<String> endpointInfo, List<String> userInfo, Map<String, String> configInfo) {
        this.ipInfo = ipInfo;
        this.endpointInfo = endpointInfo;
        this.userInfo = userInfo;
        this.configInfo = configInfo;
    }

    public EnvInfo(String ipInfo, String endpointInfo, String userInfo, Map<String, String> configInfo) {
        this.ipInfo = Collections.singletonList(ipInfo);
        this.endpointInfo = Collections.singletonList(endpointInfo);
        this.userInfo = Collections.singletonList(userInfo);
        this.configInfo = configInfo;
    }

    public EnvInfo(
            List<String> ipInfo, List<String> endpointInfo,
            List<String> ip2Info, List<String> endpoint2Info,
            List<String> userInfo, Map<String, String> configInfo
    ) {
        this.ipInfo = ipInfo;
        this.endpointInfo = endpointInfo;
        this.ip2Info = ip2Info;
        this.endpoint2Info = endpoint2Info;
        this.userInfo = userInfo;
        this.configInfo = configInfo;
    }

    public List<String> getIpInfo() {
        return ipInfo;
    }

    public List<String> getEndpointInfo() {
        return endpointInfo;
    }

    public List<String> getIp2Info() {
        return ip2Info;
    }

    public List<String> getEndpoint2Info() {
        return endpoint2Info;
    }

    public List<String>  getUserInfo() {
        return userInfo;
    }

    public Map<String, String> getConfigInfo() {
        return configInfo;
    }
}
