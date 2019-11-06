public class Output {
    private String userName;
    private String nameOfTest;
    private int id;
    private String bounds;
    private String type;
    private String shown;
    //TODO see if we need to add shown column

    public Output(String userName, String nameOfTest, int id, String bounds, String type, String shown) {
        this.userName = userName;
        this.nameOfTest = nameOfTest;
        this.id = id;
        this.bounds = bounds;
        this.type = type;
        this.shown = shown;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNameOfTest() {
        return nameOfTest;
    }

    public void setNameOfTest(String nameOfTest) {
        this.nameOfTest = nameOfTest;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBounds() {
        return bounds;
    }

    public void setBounds(String bounds) {
        this.bounds = bounds;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getShown() {
        return shown;
    }

    public void setShown(String shown) {
        this.shown = shown;
    }
}
