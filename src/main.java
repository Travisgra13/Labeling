import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class main {
    private static PrintWriter writer;
    private static Map<String, ArrayList<Label>> goldStandardsMap = new HashMap<>();
    private static Map<String, ArrayList<Label>> userLabelsMap = new HashMap<>();
    private static ArrayList<Output> allOutputs = new ArrayList<>();
    private static int numCorrect = 0;
    private static int numTypeCorrect = 0;
    private static int numBoundsCorrect = 0;
    private static int numIncorrect = 0;
    private static int totalGait = 0;
    static {
        try {
            writer = new PrintWriter("label_outputs.txt", "UTF-8");
            writer.println("userName, nameOfTest, eventID, bounds, type, allRight, allWrong, typeRightBoundsWrong, boundsRightTypeWrong, NoAttempt, startError, endError, shown, numOfOverlappedLabels");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static void getLabelsForGoldStandard(File goldStandardFile) throws FileNotFoundException {
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
            if (isZeroLabel(newLabel)) {
                continue;
            }

            if (!goldStandardsMap.containsKey(newLabel.getTestName())) {
                ArrayList<Label> newLabelList = new ArrayList<>();
                newLabelList.add(newLabel);
                goldStandardsMap.put(newLabel.getTestName(), newLabelList);
            }
            else {
                goldStandardsMap.get(newLabel.getTestName()).add(newLabel);
            }

        }

    }

    private static void getLabelsForUserLabels(File userLabelFile) throws FileNotFoundException {
        FileReader reader = new FileReader(userLabelFile);
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
            if (isZeroLabel(newLabel)) {
                continue;
            }

            if (!userLabelsMap.containsKey(newLabel.getTestName())) {
                ArrayList<Label> newLabelList = new ArrayList<>();
                newLabelList.add(newLabel);
                userLabelsMap.put(newLabel.getTestName(), newLabelList);
            }
            else {
                userLabelsMap.get(newLabel.getTestName()).add(newLabel);
            }

        }
    }

    private static String findUserID(String filePath) {
        StringBuilder sb = new StringBuilder(filePath);
        int labelsIndex = sb.indexOf(".labels.json");
        sb.delete(labelsIndex, sb.length());
        return sb.toString();
    }

    private static float findError(float goldStandardTime, float userTime) {
        return (userTime - goldStandardTime);
    }

    private static void testAgainstRelevantUserLabels(Label goldStandardLabel, ArrayList<Label> relevantUserLabels) {
        //multiple users
        String previousUser = "";
        ArrayList<Output> userOutputs = new ArrayList<>();
        for (int i = 0; i < relevantUserLabels.size(); i++) {
            Label userLabel = relevantUserLabels.get(i);
            //Assuming there are no zero labels by this point
            if (i == relevantUserLabels.size() - 1) {
                userOutputs.add(getResult(goldStandardLabel, userLabel));
                allOutputs.addAll(consolidateOverlaps(userOutputs));
                return;
            }
            if (!userLabel.getUserName().equals(previousUser)) {
                //add noAttempts?
                allOutputs.addAll(consolidateOverlaps(userOutputs));
                userOutputs = new ArrayList<>();
            }
            userOutputs.add(getResult(goldStandardLabel, userLabel));
            previousUser = userLabel.getUserName();

        }
    }

    private static Output getResult(Label goldStandardLabel, Label userLabel) {
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
            float startError = findError(goldStandardLabel.getStartTime(), userLabel.getStartTime());
            float endError = findError(goldStandardLabel.getEndTime(), userLabel.getEndTime());
            ArrayList<Integer> array = new ArrayList<>();
            array.add(userLabel.getId());
            Output newOutput = new Output(userLabel.getUserName(), userLabel.getTestName(), goldStandardLabel.getId(), bounds, type, startError, endError, userLabel.getShown(), 1, array);
            return newOutput;
        }

        Output missedOutput = new Output(userLabel.getUserName(), userLabel.getTestName(), -1, "NoAttempt", "NoAttempt", 0.0f, 0.0f, userLabel.getShown(), 0, new ArrayList<>());
        return missedOutput;
    }

    private static ArrayList<Output> getResult(Label userLabel, GoldStandard goldStandard, String userID, String testName, String id) {
        //for each gold standard run against this label to see its result
        //TODO make sure you create the case where a label holds two different gold label at some point
        ArrayList<Output> outputs = new ArrayList<>();
        if (isZeroLabel(userLabel)) {
            return outputs;
        }
        for (Label goldStandardLabel : goldStandard) {
            boolean rightType = false;
            boolean rightBounds = false;
            boolean missed = false;
            if (userLabel.getUserName().equals("aqua1") && userLabel.getTestName().equals("drywall-blue") && userLabel.getId() == 2 && goldStandardLabel.getId() == 3)  {
                System.out.println("Found it");
            }
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
                if (testName.equals("drywall-orange") && (userID).equals("aqua1")) {
                }
                float startError = findError(goldStandardLabel.getStartTime(), userLabel.getStartTime());
                float endError = findError(goldStandardLabel.getEndTime(), userLabel.getEndTime());
                ArrayList<Integer> array = new ArrayList<>();
                array.add(userLabel.getId());
                Output newOutput = new Output(userID, testName, goldStandardLabel.getId(), bounds, type, startError, endError, null, 1, array);
                outputs.add(newOutput);
            }

        }
        if (outputs.size() == 0) {
            Output missedOutput = new Output(userID, testName, -1, "NoAttempt", "NoAttempt", 0.0f, 0.0f, null, 0, new ArrayList<>());
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
                    //Output newOutput = new Output(currOutputs.get(i).getUserName(), currOutputs.get(i).getNameOfTest(), currId, "correct", "correct", 0.0f, 0.0f, currOutputs.get(i).getShown(), currOutputs.get(i).getNumOfOverlappedLabels() + 1);
                    if (!currOutputs.get(i).getNameOfTest().equals(currOutputs.get(k).getNameOfTest())) {
                        System.out.println("broken");
                    }
                    float startAbsErrorFirst = Math.abs(currOutputs.get(i).getStartError());
                    float endAbsErrorFirst = Math.abs(currOutputs.get(i).getEndError());
                    float startAbsErrorSecond = Math.abs(currOutputs.get(k).getStartError());
                    float endAbsErrorSecond = Math.abs(currOutputs.get(k).getEndError());
                    float sumFirst = startAbsErrorFirst + endAbsErrorFirst;
                    float sumSecond = startAbsErrorSecond + endAbsErrorSecond;
                    Output result;
                    if (sumFirst < sumSecond) {
                        result = currOutputs.get(i);
                        result.addNewOverlappingLabel(currOutputs.get(k).getOverlaps());
                    }
                    else {
                        result = currOutputs.get(k);
                        result.addNewOverlappingLabel(currOutputs.get(i).getOverlaps());
                    }

                    int before = result.getNumOfOverlappedLabels();
                    result.setNumOfOverlappedLabels(before + 1);



                    if (newList.size() == 0) {
                        newList.add(result);
                    }
                    else {
                       newList.set(0, result);
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
        if (userLabel.getStartTime()  < goldStandardLabel.getStartTime() && userLabel.getEndTime() < goldStandardLabel.getStartTime()) {
            return true;
        }
        if (userLabel.getStartTime() > goldStandardLabel.getEndTime() && userLabel.getEndTime() > goldStandardLabel.getEndTime()) {
            return true;
        }
        return false;
        //means it is just wrong bounds
    }

    private static void countCorrectGaitEvents(ArrayList<Output> allOutputs) {
        String init = "Bounds, ID, numOfOverlapped, Type, Username, StartError, EndError, Overlapping Labels \n ";
        StringBuilder sb = new StringBuilder(init);
        String prevUser = "";
        for (Output output : allOutputs) {
            if (output.getUserName() == null) {
                continue;
            }
           //if (output.getNameOfTest().contains("drywall-blue")) {
                StringBuilder overLaps = new StringBuilder();
                for (int i = 0; i < output.getNumOfOverlappedLabels(); i++) {
                    if (i == 0 && output.getNumOfOverlappedLabels() != 1) {
                        overLaps.append("(" + output.getOverlaps().get(i) + ", ");
                    }
                    else if (i == 0 && output.getNumOfOverlappedLabels() == 1) {
                        overLaps.append("(" + output.getOverlaps().get(i) + ")");
                    }
                    else if (i == output.getNumOfOverlappedLabels() - 1) {
                        overLaps.append(output.getOverlaps().get(i) + ")");
                    }
                    else {
                        overLaps.append(output.getOverlaps().get(i) + ", ");
                    }

               }
               if (!prevUser.equals(output.getUserName())) {
                   sb.append('\n');
                   prevUser = output.getUserName();
               }
                if (output.getBounds().equals("wrong") && output.getType().equals("correct")) {
                    sb.append(output.getBounds() + ", " + output.getId() + ", " + output.getNumOfOverlappedLabels() + ", " + output.getType() + ", " + output.getUserName() + ", " + output.getStartError() + ", " + output.getEndError() + ", " + overLaps.toString() + ", ISNUMTYPE ONLY" + '\n');
                }
                else {
                    if (!output.getBounds().equals("NoAttempt")) {
                        sb.append(output.getBounds() + ", " + output.getId() + ", " + output.getNumOfOverlappedLabels() + ", " + output.getType() + ", " + output.getUserName() + ", " + output.getStartError() + ", " + output.getEndError() + ", " + overLaps.toString() + '\n');
                    }
                }
               if (output.getBounds().equals("correct") && output.getType().equals("correct")) {
                    numCorrect++;
                    if (output.getNameOfTest().contains("gait-blue")) {
                    }
                }
                else if (output.getBounds().equals("correct") && output.getType().equals("wrong")) {
                    numBoundsCorrect++;

                }
                else if (output.getBounds().equals("wrong") && output.getType().equals("correct")) {
                    numTypeCorrect++;
                }
                else if (output.getBounds().equals("wrong") && output.getType().equals("wrong")) {
                    numIncorrect++;
                }
                totalGait++;
            }
        //}
        if (sb.toString().contains("correct") || sb.toString().contains("wrong")) {
            System.out.println(sb.toString());
        }
    }

    private static void createCSV() throws IOException {
        ArrayList<Integer> ids = new ArrayList<>();
        ArrayList<Output> userOutputs = new ArrayList<>();

        for (Map.Entry goldStandard: goldStandardsMap.entrySet()) {
            String testName = goldStandard.getKey().toString();
            ArrayList<Label> goldStandardLabels = (ArrayList<Label>) goldStandard.getValue();
            for (Label goldLabel: goldStandardLabels) {
                ArrayList<Label> relevantUsers = userLabelsMap.get(testName);
                testAgainstRelevantUserLabels(goldLabel, relevantUsers);
            }
        }


        for (Output output : allOutputs) {
            Gson gson = new Gson();
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

            if (output.getId() == -1 || output.getBounds().equals("NoAttempt")) {
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
                    + "," + Integer.toString(noAttempt) + "," + Float.toString(output.getStartError()) + "," + Float.toString(output.getEndError()) + "," + output.getShown() + "," + Integer.toString(output.getNumOfOverlappedLabels());
            if (output.getNumOfOverlappedLabels() == 1) {

            }
            String outputString = gson.toJson(output);
            writer.println(myString);
        }
        countCorrectGaitEvents(allOutputs);
    }

    private static boolean isZeroLabel(Label goldStandard) {
        if (goldStandard.getEndTime() - goldStandard.getStartTime() == 0.0f) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws IOException {
        File goldStandardFile = new File(args[0]);
        File userLabelsFile = new File(args[1]);
        getLabelsForGoldStandard(goldStandardFile);
        getLabelsForUserLabels(userLabelsFile);
        createCSV();

        System.out.println("Num Correct is " + numCorrect);
        System.out.println("Num Type only is " + numTypeCorrect);
        System.out.println("Num Bounds only is " + numBoundsCorrect);
        System.out.println("Num incorrect is " + numIncorrect);
        System.out.println("Total is " + totalGait);


        writer.close();
    }
}
