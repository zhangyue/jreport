package ffzy.jreport.envinfo;

public class EnvInfoProvider {
    private static EnvInfo envInfo;

    public EnvInfoProvider(EnvInfo envInfo) {
        EnvInfoProvider.envInfo = envInfo;
    }
    public static EnvInfo getEnvInfo() {
        return EnvInfoProvider.envInfo;
    }
}
