import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONArray;

import java.io.*;
import java.util.ArrayList;

public class main {
    private static PrintWriter writer;

    static {
        try {
            writer = new PrintWriter("label_outputs.txt", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static GoldStandard getLabelsForGoldStandard(File goldStandardFile, GoldStandard goldStandard) throws FileNotFoundException {
        FileReader reader = new FileReader(goldStandardFile);
        JsonParser parser = new JsonParser();
        JsonArray labels = null;
        try {
            labels = (JsonArray) parser.parse(reader);
        }catch (Exception e) {
            e.printStackTrace();
        }
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

            int result = new Result(rightType, rightBounds, missed).determineResult();
            //goldStandardLabel.getId()
            //id of gold standard or userLabel id
            String bounds = null;
            String type = null;

            switch(result) {
                case 1:
                    bounds = "correct";
                    type = "correct";
                    break;
                case 2:
                    bounds = "missed";
                    type = "missed";
                    break;
                case 3:
                    bounds = "wrong";
                    type = "correct";
                    break;
                case 4:
                    bounds = "correct";
                    type = "wrong";
                    break;
                case 5:
                    bounds = "wrong";
                    type = "wrong";
            }

            Output newOutput = new Output(userID, testName, goldStandardLabel.getId(), bounds, type, null);
            if (result != 2) {
                outputs.add(newOutput);
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

    private static String shown(String taskName, String studyGroup) {
        switch (taskName) {
            case "drywall-red" :
                if (studyGroup.equals("StudyE")) {
                    return "Data";
                }
                else if (studyGroup.equals("StudyF")) {
                    return "Video";
                }
                else if (studyGroup.equals("StudyG")) {
                    return "Data";
                }
                else if (studyGroup.equals("StudyH")) {
                    return "Video";
                }
                break;
            case "drywall-blue":
                if (studyGroup.equals("StudyE")) {
                    return "Both";
                }
                else if (studyGroup.equals("StudyF")) {
                    return "Both";
                }
                else if (studyGroup.equals("StudyG")) {
                    return "Both";
                }
                else if (studyGroup.equals("StudyH")) {
                    return "Both";
                }
                break;
            case "drywall-pink" :
                if (studyGroup.equals("StudyE")) {
                    return "Data";
                }
                else if (studyGroup.equals("StudyF")) {
                    return "Data";
                }
                else if (studyGroup.equals("StudyG")) {
                    return "None";
                }
                else if (studyGroup.equals("StudyH")) {
                    return "None";
                }
                break;
            case "drywall-orange":
                if (studyGroup.equals("StudyE")) {
                    return "Video";
                }
                else if (studyGroup.equals("StudyF")) {
                    return "Data";
                }
                else if (studyGroup.equals("StudyG")) {
                    return "Video";
                }
                else if (studyGroup.equals("StudyH")) {
                    return "Data";
                }
                break;
            case "gait-red" :
                if (studyGroup.equals("StudyE")) {
                    return "Data";
                }
                else if (studyGroup.equals("StudyF")) {
                    return "Video";
                }
                else if (studyGroup.equals("StudyG")) {
                    return "Data";
                }
                else if (studyGroup.equals("StudyH")) {
                    return "Video";
                }
                break;
            case "gait-blue":
                if (studyGroup.equals("StudyE")) {
                    return "Both";
                }
                else if (studyGroup.equals("StudyF")) {
                    return "Both";
                }
                else if (studyGroup.equals("StudyG")) {
                    return "Both";
                }
                else if (studyGroup.equals("StudyH")) {
                    return "Both";
                }
                break;
            case "gait-pink" :
                if (studyGroup.equals("StudyE")) {
                    return "None";
                }
                else if (studyGroup.equals("StudyF")) {
                    return "Fix";
                    //TODO Figure this person out
                }
                else if (studyGroup.equals("StudyG")) {
                    return "Data";
                }
                else if (studyGroup.equals("StudyH")) {
                    return "Data";
                }
                break;
            case "gait-yellow":
                if (studyGroup.equals("StudyE")) {
                    return "Video";
                }
                else if (studyGroup.equals("StudyF")) {
                    return "Data";
                }
                else if (studyGroup.equals("StudyG")) {
                    return "Video";
                }
                else if (studyGroup.equals("StudyH")) {
                    return "Data";
                }
                break;
        }
        return null;
    }

    private static void createCSV(File study, GoldStandard goldStandard, String taskName) throws FileNotFoundException, UnsupportedEncodingException {
        ArrayList<Output> outputs = new ArrayList<>();
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
                outputs.addAll(getResult(newLabel, goldStandard, userID, testName, id));
            }
            //All the userLabels
        }
        writer.println(taskName);
        writer.println(study.getName());
        for (Output output : outputs) {
            Gson gson = new Gson();
            output.setShown(shown(taskName, study.getName()));
            String outputString = gson.toJson(output);
            writer.println(outputString);
        }
    }

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
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
        writer.close();
    }
}
