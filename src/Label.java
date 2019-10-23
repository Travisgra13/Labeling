public class Label {
    private float time;
    private float endTime;
    private String type;
    private int id;

    public Label(float time, float endTime, String type, int id) {
        this.time = time;
        this.endTime = endTime;
        this.type = type;
        this.id = id;
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
}
