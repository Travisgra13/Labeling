import org.json.JSONArray;

import java.io.File;

public class main {
    public static void main(String[] args){
        File homeFolder = new File(args[0]);
        File[] taskFolder = homeFolder.listFiles(); //TODO Check to make sure gold std is always index 0
        System.out.println("hi");


    }
}
