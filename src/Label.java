import java.io.Serializable;

public class Label implements Serializable {
    private float time;
    private float endTime;
    private String type;
    private int id;
    private String testName;
    private String shown;
    private String study;
    private String userName;

    public Label(float time, float endTime, String type, int id, String testName, String shown, String study, String userName) {
        this.time = time;
        this.endTime = endTime;
        this.type = type;
        this.id = id;
        this.testName = testName;
        this.shown = shown;
        this.study = study;
        this.userName = userName;
    }

    public float getStartTime() {
        return time;
    }

    public void setStartTime(float time) {
        this.time = time;
    }

    public float getEndTime() {
        return endTime;
    }

    public void setEndTime(float endTime) {
        this.endTime = endTime;
    }

    public String getEventType() {
        return type;
    }

    public void setEventType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getShown() {
        return shown;
    }

    public void setShown(String shown) {
        this.shown = shown;
    }

    public String getStudy() {
        return study;
    }

    public void setStudy(String study) {
        this.study = study;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
