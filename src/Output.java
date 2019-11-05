public class Output {
    private String userName;
    private String nameOfTest;
    private int id;
    private String result;
    private String shown;
    //TODO see if we need to add shown column

    public Output(String userName, String nameOfTest, int id, String result, String shown) {
        this.userName = userName;
        this.nameOfTest = nameOfTest;
        this.id = id;
        this.result = result;
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getShown() {
        return shown;
    }

    public void setShown(String shown) {
        this.shown = shown;
    }
}
