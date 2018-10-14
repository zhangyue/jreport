/**
 * Created by Zhang Yue on 2/11/2018
 */
package ffzy.jreport;

public class TestResult {
    private String id;
    private String name;
    private String testName;
    private int status;
    private long startTimeInMillis;
    private long endTimeInMillis;
    private String knownBug;

    public TestResult(String id, String name, String testName, int status, long startTimeInMillis, long endTimeInMillis) {
        this.id = id;
        this.name = name;
        this.testName = testName;
        this.status = status;
        this.startTimeInMillis = startTimeInMillis;
        this.endTimeInMillis = endTimeInMillis;
    }


    public TestResult(String id, String name, String testName, int status, long startTimeInMillis, long endTimeInMillis, String knownBug) {
        this.id = id;
        this.name = name;
        this.testName = testName;
        this.status = status;
        this.startTimeInMillis = startTimeInMillis;
        this.endTimeInMillis = endTimeInMillis;
        this.knownBug = knownBug;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getStartTimeInMillis() {
        return startTimeInMillis;
    }

    public void setStartTimeInMillis(long startTimeInMillis) {
        this.startTimeInMillis = startTimeInMillis;
    }

    public long getEndTimeInMillis() {
        return endTimeInMillis;
    }

    public void setEndTimeInMillis(long endTimeInMillis) {
        this.endTimeInMillis = endTimeInMillis;
    }

    public String getKnownBug() {
        return knownBug;
    }

    public void setKnownBug(String knownBug) {
        this.knownBug = knownBug;
    }

    public String toReportString() {
        String testStatusString = ReportUtil.generateStatus(status);
        String duration = ReportUtil.generateDuration(endTimeInMillis, startTimeInMillis);

        return id + " " + name + " " + testName + " " + testStatusString + " " + duration + " " + knownBug;
    }

    @Override
    public String toString() {
        return "TestResult{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", testName='" + testName + '\'' +
                ", status=" + status +
                ", startTimeInMillis=" + startTimeInMillis +
                ", endTimeInMillis=" + endTimeInMillis +
                ", knownBug='" + knownBug + '\'' +
                '}';
    }
}
