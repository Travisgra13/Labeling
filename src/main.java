import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONArray;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

public class main {
    private final double ERROR = .5;

    private static GoldStandard getLabelsForGoldStandard(File goldStandardFile, GoldStandard goldStandard) throws FileNotFoundException {
        FileReader reader = new FileReader(goldStandardFile);
        JsonParser parser = new JsonParser();
        JsonArray labels = (JsonArray) parser.parse(reader);
        for (int i = 0; i < labels.size(); i++) {
            Gson gson = new Gson();
            Label newLabel = gson.fromJson(labels.get(i), Label.class);
            goldStandard.add(newLabel);
        }
        return goldStandard;
    }

    private static String findUserID(String filePath) {
        StringBuilder sb = new StringBuilder(filePath);
        int labelsIndex = sb.indexOf(".labels.json");
        sb.delete(labelsIndex, sb.length());
        return sb.toString();
    }

    private static ArrayList<Output> getResult(Label userLabel, GoldStandard goldStandard, String userID, String testName, String id) {
        //for each gold standard run against this label to see its result
        //TODO make sure you create the case where a label holds two different gold label at some point
        ArrayList<Output> outputs = new ArrayList<>();
        for (Label goldStandardLabel : goldStandard) {
            boolean rightType = false;
            boolean rightBounds = false;
            boolean missed = false;
            if (userLabel.getEventType().equals(goldStandardLabel.getEventType())) {
                rightType = true;
            }
            if (isRightBounds(userLabel, goldStandardLabel)) {
                rightBounds = true;
            }
            else if (isOrphan(userLabel, goldStandardLabel)) {
                missed = true;
            }

            String result = new Result(rightType, rightBounds, missed).getResult();
            //goldStandardLabel.getId()
            //id of gold standard or userLabel id
            Output newOutput = new Output(userID, testName, goldStandardLabel.getId(), result);
            if (!result.equals("Missed")) {
                outputs.add(newOutput);
            }
            else {
                System.out.println("hi");
            }

        }
        return outputs;
    }


    private static boolean isRightBounds(Label userLabel, Label goldStandardLabel) {
        if (goldStandardLabel.getStartTime() - .5 <= userLabel.getStartTime() && goldStandardLabel.getStartTime() + .5 >= userLabel.getStartTime()) {
            if (goldStandardLabel.getEndTime() - .5 <= userLabel.getEndTime() && goldStandardLabel.getEndTime() + .5 >= userLabel.getEndTime()) {
                return true;
            }
        }
        return false;
        //Either an orphan or wrong bounds
    }

    private static boolean isOrphan(Label userLabel, Label goldStandardLabel) {
        if (userLabel.getStartTime() <= goldStandardLabel.getStartTime() && userLabel.getEndTime() <= goldStandardLabel.getStartTime()) {
            return true;
        }
        if (userLabel.getStartTime() >= goldStandardLabel.getEndTime() && userLabel.getEndTime() >= goldStandardLabel.getEndTime()) {
            return true;
        }
        return false;
        //means it is just wrong bounds
    }

    private static void createCSV(File study, GoldStandard goldStandard, String taskName) throws FileNotFoundException {

        for (File userFile : study.listFiles()) {
            UserLabels userLabels = new UserLabels();
            FileReader reader = new FileReader(userFile);
            JsonParser parser = new JsonParser();
            JsonArray labels = (JsonArray) parser.parse(reader);
            for (int i = 0; i < labels.size(); i++) {
                Gson gson = new Gson();
                Label newLabel = gson.fromJson(labels.get(i), Label.class);
                userLabels.add(newLabel);

                String userID = findUserID(userFile.getName());
                String testName = taskName;
                String id = Integer.toString(newLabel.getId());
                getResult(newLabel, goldStandard, userID, testName, id);




                //TODO write output vars here
            }
            //All the userLabels
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        File homeFolder = new File(args[0]);
        File[] taskFolder = homeFolder.listFiles();
        for (File file : taskFolder) {
            File[] innerFolder = file.listFiles();
            //Each Task Folder has its own Gold Standard
            GoldStandard goldStandard = new GoldStandard();
            for (File innerFiles : innerFolder) {
                if (innerFiles.listFiles() == null) {
                    //TODO This is a gold standard
                    try {
                        goldStandard = getLabelsForGoldStandard(innerFiles, goldStandard);
                    }catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    createCSV(innerFiles, goldStandard, file.getName());
                }
            }
        }
    }
}
