import java.util.ArrayList;

public class GoldStandard extends ArrayList<Label> {
    public void setList(ArrayList <Label> labels) {
        this.clear();
        this.addAll(labels);
    }
}
