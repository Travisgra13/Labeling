import java.util.ArrayList;

public class Output {
    private String userName;
    private String nameOfTest;
    private int id;
    private String bounds;
    private String type;
    private float startError;
    private float endError;
    private String shown;
    private ArrayList<Integer> overlaps;
    private int numOfOverlappedLabels;
    //TODO see if we need to add shown column

    public Output(String userName, String nameOfTest, int id, String bounds, String type, float startError, float endError, String shown, int numOfOverlappedLabels, ArrayList <Integer> overlaps) {
        this.userName = userName;
        this.nameOfTest = nameOfTest;
        this.id = id;
        this.bounds = bounds;
        this.type = type;
        this.startError = startError;
        this.endError = endError;
        this.shown = shown;
        this.numOfOverlappedLabels = numOfOverlappedLabels;
        this.overlaps = overlaps;
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

    public float getStartError() {
        return startError;
    }

    public void setStartError(float startError) {
        this.startError = startError;
    }

    public float getEndError() {
        return endError;
    }

    public void setEndError(float endError) {
        this.endError = endError;
    }

    public String getShown() {
        return shown;
    }

    public void setShown(String shown) {
        this.shown = shown;
    }

    public int getNumOfOverlappedLabels() {
        return numOfOverlappedLabels;
    }

    public void setNumOfOverlappedLabels(int numOfOverlappedLabels) {
        this.numOfOverlappedLabels = numOfOverlappedLabels;
    }

    public ArrayList<Integer> getOverlaps() {
        return overlaps;
    }

    public void setOverlaps(ArrayList<Integer> overlaps) {
        this.overlaps = overlaps;
    }

    public void addNewOverlappingLabel(ArrayList<Integer> labelIDs) {
        this.overlaps.addAll(labelIDs);
    }


}
