import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONArray;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class main {
    private static PrintWriter writer;

    static {
        try {
            writer = new PrintWriter("label_outputs.txt", "UTF-8");
            writer.println("userName, nameOfTest, eventID, bounds, type, allRight, allWrong, typeRightBoundsWrong, boundsRightTypeWrong, NoAttempt, shown");
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
                    /*
                case 2:
                    bounds = "noAttempt";
                    type = "noAttempt";
                    break;
                    */

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
            if (result != 2) {

                Output newOutput = new Output(userID, testName, goldStandardLabel.getId(), bounds, type, null);
                outputs.add(newOutput);
            }

        }
        if (outputs.size() == 0) {
            Output missedOutput = new Output(userID, testName, -1, "NoAttempt", "NoAttempt", null);
            outputs.add(missedOutput);
        }
        return outputs;
    }

    private static ArrayList<Output> consolidateOverlaps(ArrayList<Output> currOutputs) {
        ArrayList finalList = new ArrayList();
        for (int i = 0; i < currOutputs.size(); i++) {
            int currId = currOutputs.get(i).getId();
            ArrayList<Output> newList = new ArrayList<>();
            for (int k = i + 1; k < currOutputs.size(); k++) {
                int peekId = currOutputs.get(k).getId();
                if (currId == peekId && currId != -1) { //at the end of this if, we can delete the element at peekID from currOutputs and set i -= 1
                    Output newOutput = new Output(currOutputs.get(i).getUserName(), currOutputs.get(i).getNameOfTest(), currId, "wrong", "correct", currOutputs.get(i).getShown());
                    if (newList.size() == 0) {
                        if (currOutputs.get(i).getType().equals("wrong") || currOutputs.get(k).getType().equals("wrong")) {
                            newOutput.setType("wrong");
                        }
                        newList.add(newOutput);
                    }
                    else {
                        if (newList.get(0).getType().equals("wrong") || currOutputs.get(k).getType().equals("wrong")) {
                            newList.get(0).setType("wrong");
                        }
                    }
                    currOutputs.set(i, newList.get(0));
                    currOutputs.remove(k);
                    k--;
                }
            }
            if (newList.size() == 0) {
                finalList.add(currOutputs.get(i));
            }
            else {
                finalList.add(newList.get(0));
                i--;
            }


        }
        return currOutputs;
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
        if (userLabel.getStartTime()  < goldStandardLabel.getStartTime() - .5 && userLabel.getEndTime() < goldStandardLabel.getStartTime() - .5) {
            return true;
        }
        if (userLabel.getStartTime() > goldStandardLabel.getEndTime() + .5 && userLabel.getEndTime() > goldStandardLabel.getEndTime() + .5) {
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
                outputs = consolidateOverlaps(outputs);
            }
            //All the userLabels
        }
        //writer.println(taskName);
        //writer.println(study.getName());
        for (Output output : outputs) {
            Gson gson = new Gson();
            output.setShown(shown(taskName, study.getName()));
            int bounds;
            if (output.getBounds().equals("correct")) {
                bounds = 1;
            }
            else {
                bounds = 0;
            }
            int type;
            if (output.getType().equals("correct")) {
                type = 1;
            }
            else {
                type = 0;
            }
            int allRight = 0;
            int allWrong = 0;
            int typeRightBoundsWrong = 0;
            int boundsRightTypeWrong = 0;
            int noAttempt = 0;

            if (output.getId() == -1) {
                noAttempt = 1;
            }
            else if (bounds == 1 && type == 1) {
                allRight = 1;
            }
            else if (bounds == 0 && type == 1) {
                typeRightBoundsWrong = 1;
            }
            else if (bounds == 0 && type == 0) {
                allWrong = 1;
            }
            else if (bounds == 1 && type == 0) {
                boundsRightTypeWrong = 1;
            }
            String myString = output.getUserName() + "," + output.getNameOfTest() + "," + output.getId() + "," + Integer.toString(bounds) + "," + Integer.toString(type) + ","
                    + Integer.toString(allRight) + "," + Integer.toString(allWrong) + "," + Integer.toString(typeRightBoundsWrong) + "," + Integer.toString(boundsRightTypeWrong)
                    + "," + Integer.toString(noAttempt) + "," + output.getShown();
            String outputString = gson.toJson(output);
            writer.println(myString);
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
